package dev.cryptcraft.talkative.client.gui.editor.branch.widgets.nodes

import com.google.common.collect.Lists
import com.mojang.blaze3d.vertex.PoseStack
import dev.cryptcraft.talkative.api.tree.node.NodeBase
import dev.cryptcraft.talkative.client.gui.GuiConstants
import dev.cryptcraft.talkative.client.gui.editor.branch.BranchNodeEditorScreen
import dev.cryptcraft.talkative.client.gui.widgets.NestedWidget
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiComponent
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.Component

abstract class NodeWidget(x: Int, y: Int, height: Int, val node: NodeBase, val parentWidget: NodeWidget?, val parentScreen: BranchNodeEditorScreen) : NestedWidget(x, y, 200, height, Component.literal("Dialog Node")) {
    protected val minecraft: Minecraft = Minecraft.getInstance()
    val childNodes: ArrayList<NodeWidget> = Lists.newArrayList()

    var lowestChildY: Int = 0

    fun removeChildNode(child: NodeWidget) {
        if (childNodes.remove(child)) {
            node.removeChild(child.node.nodeId)
            parentScreen.removeChild(child)
        }
    }

    //Rendering
    fun renderNodeAndChildren(poseStack: PoseStack, mouseX: Int, mouseY: Int, delta: Float) {
        renderNode(poseStack, mouseX, mouseY, delta)
        childNodes.forEach { it.renderNodeAndChildren(poseStack, mouseX, mouseY, delta) }
    }

    protected open fun renderNode(poseStack: PoseStack, mouseX: Int, mouseY: Int, delta: Float) {
        if (visible) {
            val posX = this.x + parentScreen.offsetX
            val posY = this.y + parentScreen.offsetY

            if(childNodes.isNotEmpty())
                drawConnections(poseStack, posX, posY, 0x55FFFFFF)

            if(shouldRender(posX, posY)) {
                val bgColour = getBackgroundColour()
                val hover = (isMouseOver(mouseX.toDouble(), mouseY.toDouble()) && parentScreen.getPopup() == null) or (parentScreen.selectedNode == this)
                var outlineColour: Int = if (parentScreen.selectedNode == this) GuiConstants.COLOR_BTN_BORDER_HL else if (hover) GuiConstants.COLOR_BTN_BORDER_HL else GuiConstants.COLOR_BTN_BORDER

                fill(poseStack, posX - 1, posY - 1, posX + width + 1, posY + height + 1, outlineColour)
                fill(poseStack, posX, posY, posX + width, posY + 11, 0xFF333333.toInt())
                fill(poseStack, posX, posY + 11, posX + width, posY + height, bgColour)

                GuiComponent.drawString(poseStack, minecraft.font, "${node?.getNodeType()?.name ?: "Missing"} Node [${node.nodeId}]", posX + 2, posY + 1, 0xFFFFFF)

                if(parentScreen.selectedNode == this) {
                    fill(poseStack, posX, posY + height + 1, posX + width, posY + height + 12, 0xFF333333.toInt())
                    GuiComponent.drawString(poseStack, minecraft.font, "[ESC] to Stop Editing", posX + 2, posY + height + 3, 0xFFFFFF)
                }
            }
        }
    }

    private fun drawConnections(poseStack: PoseStack, posX: Int, posY: Int, colour: Int) {
        val myConnectionPosX = posX + width
        val myConnectionPosY = posY + (height / 2)
        hLine(poseStack, myConnectionPosX, myConnectionPosX + 4, myConnectionPosY, colour)

        childNodes.forEach {
            val childConnectionX = it.x + parentScreen.offsetX
            val childConnectionY = it.y + parentScreen.offsetY + (it.height / 2)
            hLine(poseStack, childConnectionX - 4, childConnectionX, childConnectionY, colour)
        }

        val firstChild = childNodes.first()
        val lastChild = childNodes.last()
        val firstConnectionY = (firstChild.y + parentScreen.offsetY) + (firstChild.height / 2)
        val lastConnectionY = (lastChild.y + parentScreen.offsetY) + (lastChild.height / 2)

        val yPos1 = Math.min(myConnectionPosY, firstConnectionY) - 1
        val yPos2 = Math.max(myConnectionPosY, lastConnectionY) + 1

        vLine(poseStack, posX + width + 5, yPos1, yPos2, colour)
    }

    abstract fun getBackgroundColour(): Int

    //Mouse
    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if(isMouseOver(mouseX, mouseY)) {
            when(button) {
                0 -> {
                    parentScreen.selectedNode = this
                }
                1 -> {
                    parentScreen.createSubMenu(mouseX.toInt(), mouseY.toInt(), this)
                }
            }
            return true
        }
        return false
    }

    //Misc.
    private fun shouldRender(posX: Int, posY: Int): Boolean {
        val guiWidth = Minecraft.getInstance().screen?.width
        val guiHeight = Minecraft.getInstance().screen?.height
        return posX + width > 0 && posX < guiWidth!! / parentScreen.zoomScale && posY + height > 0 && posY < guiHeight!! / parentScreen.zoomScale
    }

    override fun isMouseOver(mouseX: Double, mouseY: Double): Boolean {
        if (mouseY < 30) return false
        if (parentScreen.submenu != null && parentScreen.submenu!!.isMouseOver(mouseX, mouseY)) return false

        val scaledMouseX = mouseX / parentScreen.zoomScale
        val scaledMouseY = mouseY / parentScreen.zoomScale
        val posX = this.x + parentScreen.offsetX
        val posY = this.y + parentScreen.offsetY
        return scaledMouseX > posX - 1 && scaledMouseY > posY - 1 && scaledMouseX < posX + width + 1 && scaledMouseY < posY + height + 1
    }

    override fun updateNarration(narrationElementOutput: NarrationElementOutput) {
    }

}