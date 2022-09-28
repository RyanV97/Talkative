package ryanv.talkative.client.gui.widgets

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiComponent
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.font.TextFieldHelper
import net.minecraft.network.chat.TextComponent
import ryanv.talkative.client.gui.editor.BranchEditorScreen
import ryanv.talkative.client.util.NodePositioner
import ryanv.talkative.common.data.tree.DialogNode

class DialogNodeWidget(x: Int, y: Int, var contents: String = "", val nodeType: DialogNode.NodeType, val nodeId: Int, val parentWidget: DialogNodeWidget?, private val parentScreen: BranchEditorScreen): AbstractWidget(x, y, 121, 25, TextComponent("Dialog Node")) {

    val minecraft: Minecraft = Minecraft.getInstance()

    private val text = TextComponent(contents)
    var children: ArrayList<DialogNodeWidget> = ArrayList()
    var lowestChildY: Int = 0

    val textEntry: TextFieldHelper = TextFieldHelper(this::getText, this::setText, this::getClipboard, this::setClipboard) {
        return@TextFieldHelper contents.length < 1024
    }

    init {
        calculateHeight()
    }

    fun serializeNodeAndChildren(): DialogNode {
        val node = serializeNode()
        for(child in children) {
            node.children.add(child.nodeId)
        }
        return node
    }

    private fun serializeNode(): DialogNode {
        return DialogNode(nodeType, contents, nodeId = nodeId)
    }

    fun addChild(type: DialogNode.NodeType, id: Int) {
        val child = parentScreen.createWidgetForNode(DialogNode(nodeType = type, nodeId = id), this)
        children.add(child)
    }

    fun removeChild(child: DialogNodeWidget) {
        if(children.remove(child))
            parentScreen.removeChild(child)
    }

    //Rendering
    fun renderNodeAndChildren(poseStack: PoseStack?, mouseX: Int, mouseY: Int, delta: Float) {
        renderNode(poseStack, mouseX, mouseY, delta)
        children.forEach { it.renderNodeAndChildren(poseStack, mouseX, mouseY, delta) }
    }

    private fun renderNode(poseStack: PoseStack?, mouseX: Int, mouseY: Int, delta: Float) {
        if(visible) {
            val posX = this.x + parentScreen.offsetX
            val posY = this.y + parentScreen.offsetY

            if(children.isNotEmpty())
                drawConnections(poseStack, posX, posY, 0x55FFFFFF.toInt())

            if(shouldRender(posX, posY)) {

                val bgColour: Int = when (nodeType) {
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

                val label: String = when (nodeType) {
                    DialogNode.NodeType.Dialog -> "Dialog Node"
                    DialogNode.NodeType.Response -> "Response Node"
                }

                GuiComponent.drawString(poseStack, minecraft.font, label, posX + 2, posY + 1, 0xFFFFFF)
                minecraft.font.drawWordWrap(TextComponent(contents), posX + 2, posY + 12, width - 4, 0xFFFFFF)

                if(parentScreen.selectedNode == this) {
                    fill(poseStack, posX, posY + height + 1, posX + width, posY + height + 12, 0xFF333333.toInt())
                    GuiComponent.drawString(poseStack, minecraft.font, "[ESC] to Stop Editing", posX + 2, posY + height + 3, 0xFFFFFF)
                }
            }
        }
        super.render(poseStack, mouseX, mouseY, delta)
    }

    private fun drawConnections(poseStack: PoseStack?, posX: Int, posY: Int, colour: Int) {
        hLine(poseStack, posX + width, posX + width + 4, posY + (height / 2), colour)
        children.forEach {
            val x = it.x + parentScreen.offsetX
            val y = it.y + parentScreen.offsetY + (it.height / 2)
            hLine(poseStack, x - 4, x, y, colour)
        }
        val lastChild = children.last()
        val firstChild = children.first()
        val yPos1 = (firstChild.y + parentScreen.offsetY) + (firstChild.height / 2) - 1
        val yPos2 = lastChild.y + parentScreen.offsetY + (lastChild.height / 2) + 1
        vLine(poseStack, posX + width + 5, yPos1, yPos2, colour)
    }

    //Text Editing
    private fun getText(): String {
        return contents
    }

    private fun setText(s: String) {
        contents = s
        calculateHeight()
    }

    private fun getClipboard(): String {
        return TextFieldHelper.getClipboardContents(minecraft)
    }

    private fun setClipboard(s: String) {
        TextFieldHelper.setClipboardContents(minecraft, s)
    }

    //Mouse
    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if(isMouseOver(mouseX, mouseY)) {
            when(button) {
                0 -> {
                    parentScreen.selectedNode = this
                    parentScreen.focused = this
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
    private fun calculateHeight() {
        val i = (12 + minecraft.font.wordWrapHeight(contents, width - 4)).coerceAtLeast(40)
        if(i != height) {
            height = i
            parentScreen.rootNodeWidget?.let { NodePositioner.layoutTree(it) }
        }
    }

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

    override fun renderButton(poseStack: PoseStack?, i: Int, j: Int, f: Float) {
    }

}