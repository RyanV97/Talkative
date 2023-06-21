package ryanv.talkative.client.gui.editor.branch.widgets.nodes

import com.google.common.collect.Lists
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiComponent
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.Component
import ryanv.talkative.client.gui.editor.branch.BranchNodeEditorScreen
import ryanv.talkative.client.gui.widgets.NestedWidget
import ryanv.talkative.common.data.tree.DialogNode
import ryanv.talkative.mixin.AbstractWidgetAccessor

class NodeWidget(x: Int, y: Int, val node: DialogNode, val parentWidget: NodeWidget?, val parentScreen: BranchNodeEditorScreen): NestedWidget(x, y, 200, if (node.nodeType == DialogNode.NodeType.Dialog) 75 else 40, Component.literal("Dialog Node")) {
    private val minecraft: Minecraft = Minecraft.getInstance()

    val editBox = addChild(NodeEditBox(this, x, y + 10, width, height - 10))
    val childNodes: ArrayList<NodeWidget> = Lists.newArrayList()
    var lowestChildY: Int = 0

    init {
        editBox.value = node.content
        editBox.setValueListener { node.content = it }
    }

    fun addChild(type: DialogNode.NodeType, id: Int) {
        val child = parentScreen.createWidgetForNode(DialogNode(id, type), this)
        node.addChild(id, type)
        childNodes.add(child)
    }

    fun removeChild(child: NodeWidget) {
        if(childNodes.remove(child))
            parentScreen.removeChild(child)
    }

    //Rendering
    fun renderNodeAndChildren(poseStack: PoseStack, mouseX: Int, mouseY: Int, delta: Float) {
        renderNode(poseStack, mouseX, mouseY, delta)
        childNodes.forEach { it.renderNodeAndChildren(poseStack, mouseX, mouseY, delta) }
    }

    private fun renderNode(poseStack: PoseStack, mouseX: Int, mouseY: Int, delta: Float) {
        if(visible) {
            val posX = this.x + parentScreen.offsetX
            val posY = this.y + parentScreen.offsetY

            if(childNodes.isNotEmpty())
                drawConnections(poseStack, posX, posY, 0x55FFFFFF)

            if(shouldRender(posX, posY)) {

                val bgColour: Int = when (node.nodeType) {
                    DialogNode.NodeType.Dialog -> 0xFF116611.toInt()
                    DialogNode.NodeType.Response -> 0xFF111166.toInt()
                }

                val outline = isMouseOver(mouseX.toDouble(), mouseY.toDouble()) or (parentScreen.selectedNode == this)
                var outlineColour: Int = 0xFF00FF00.toInt()

                if(parentScreen.selectedNode == this)
                    outlineColour = 0xFFFF0000.toInt()
                if(outline)
                    fill(poseStack, posX - 1, posY - 1, posX + width + 1, posY + height + 1, outlineColour)

                fill(poseStack, posX, posY, posX + width, posY + 11, 0xFF333333.toInt())
                fill(poseStack, posX, posY + 11, posX + width, posY + height, bgColour)

                val label: String = when (node.nodeType) {
                    DialogNode.NodeType.Dialog -> "Dialog Node"
                    DialogNode.NodeType.Response -> "Response Node"
                }

                GuiComponent.drawString(poseStack, minecraft.font, label, posX + 2, posY + 1, 0xFFFFFF)

                if(parentScreen.selectedNode == this) {
                    fill(poseStack, posX, posY + height + 1, posX + width, posY + height + 12, 0xFF333333.toInt())
                    GuiComponent.drawString(poseStack, minecraft.font, "[ESC] to Stop Editing", posX + 2, posY + height + 3, 0xFFFFFF)
                }

                editBox.x = x + parentScreen.offsetX
                editBox.y = y + 10 + parentScreen.offsetY
                editBox.render(poseStack, mouseX, mouseY, delta)
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

    //Mouse
    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if(isMouseOver(mouseX, mouseY)) {
            when(button) {
                0 -> {
                    parentScreen.selectedNode = this
                    (editBox as AbstractWidgetAccessor).pleaseSetFocused(true)
                }
                1 -> {
                    parentScreen.createSubMenu(mouseX.toInt(), mouseY.toInt(), this)
                }
            }
            return true
        }
        (editBox as AbstractWidgetAccessor).pleaseSetFocused(false)
        return false
    }

    //Misc.
    private fun shouldRender(posX: Int, posY: Int): Boolean {
        val guiWidth = Minecraft.getInstance().screen?.width
        val guiHeight = Minecraft.getInstance().screen?.height
        return posX + width > 0 && posX < guiWidth!! / parentScreen.zoomScale && posY + height > 0 && posY < guiHeight!! / parentScreen.zoomScale
    }

    override fun isMouseOver(mouseX: Double, mouseY: Double): Boolean {
        val scaledMouseX = mouseX / parentScreen.zoomScale
        val scaledMouseY = mouseY / parentScreen.zoomScale
        val posX = this.x + parentScreen.offsetX
        val posY = this.y + parentScreen.offsetY
        return scaledMouseX > posX - 1 && scaledMouseY > posY - 1 && scaledMouseX < posX + width + 1 && scaledMouseY < posY + height + 1
    }

    override fun updateNarration(narrationElementOutput: NarrationElementOutput) {
    }

}