package ryanv.talkative.client.gui.editor

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.SharedConstants
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.components.Button
import net.minecraft.network.chat.TextComponent
import ryanv.talkative.client.gui.TalkativeScreen
import ryanv.talkative.client.gui.widgets.DialogNodeWidget
import ryanv.talkative.client.gui.widgets.SubmenuWidget
import ryanv.talkative.client.util.NodePositioner
import ryanv.talkative.common.data.Actor
import ryanv.talkative.common.data.tree.DialogNode
import kotlin.math.ceil
import kotlin.math.floor

class TreeEditorScreen(val actor: Actor, val rootNode: DialogNode): TalkativeScreen(null, TextComponent("Dialog Tree Editor")) {

    var offsetX: Int = 0
    var offsetY: Int = 0
    var zoomScale: Float = 1.0F

    var rootNodeWidget: DialogNodeWidget? = null
    var selectedNode: DialogNodeWidget? = null

    override fun init() {
        rootNodeWidget = loadNodeAndChildren(rootNode)
        NodePositioner.layoutTree(rootNodeWidget!!)

        addButton(Button(width - 50, height - 20, 50, 20, TextComponent("Save")) {
            onClose()
        })

        addButton(Button(width - 100, height - 20, 50, 20, TextComponent("Discard")) {
            onClose()
        })

        addButton(Button(width - 150, height - 20, 50, 20, TextComponent("Options")) {
            NodePositioner.layoutTree(rootNodeWidget!!)
        })
    }

    override fun render(poseStack: PoseStack?, mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground(poseStack)
//        GuiComponent.drawString(poseStack, font, "$offsetX, $offsetY", 0, 1, 0xFFFFFF)
//        GuiComponent.drawString(poseStack, font, zoomScale.toString(), 0, 12, 0xFFFFFF)

        RenderSystem.pushMatrix()
        RenderSystem.scalef(zoomScale, zoomScale, 1.0F)

        rootNodeWidget?.renderNodeAndChildren(poseStack, mouseX, mouseY, delta)

        RenderSystem.popMatrix()

        super.render(poseStack, mouseX, mouseY, delta)
    }

    override fun onMouseClick(mouseX: Double, mouseY: Double, mouseButton: Int): Boolean {
        return false
    }

    override fun onMouseRelease(mouseX: Double, mouseY: Double, mouseButton: Int): Boolean {
        return false
    }

    override fun onMouseDrag(mouseX: Double, mouseY: Double, mouseButton: Int, diffX: Double, diffY: Double): Boolean {
        if(mouseButton == 1) {
            if(!isDragging)
                isDragging = true

            offsetX += when (diffX > 0) {
                true -> ceil(diffX / zoomScale).toInt()
                false -> floor(diffX / zoomScale).toInt()
            }
            offsetY += when (diffY > 0) {
                true -> ceil(diffY / zoomScale).toInt()
                false -> floor(diffY / zoomScale).toInt()
            }
        }
        return false
    }

    override fun onMouseScroll(mouseX: Double, mouseY: Double, scrollAmount: Double): Boolean {
        val i = (scrollAmount.toFloat() * 0.1F)
        zoomScale = (zoomScale + i).coerceIn(0.2F, 2.0F)
        return true
    }

    override fun onKeyPressed(keyCode: Int, j: Int, k: Int): Boolean {
        if(selectedNode != null) {
            val textEntry = selectedNode!!.textEntry
            if(isSelectAll(keyCode))
                textEntry.selectAll()
            if(isCut(keyCode))
                textEntry.cut()
            if(isCopy(keyCode))
                textEntry.copy()
            if(isPaste(keyCode))
                textEntry.paste()
            if(hasShiftDown() && (keyCode == 257 || keyCode == 335))
                textEntry.insertText("\n")
            else
                selectedNode = null
            when(keyCode) {
                259 -> textEntry.removeCharsFromCursor(-1)
                261 -> textEntry.removeCharsFromCursor(1)
                262 -> textEntry.moveByChars(1, hasShiftDown())
                263 -> textEntry.moveByChars(-1, hasShiftDown())
            }
            return true
        }
        return false
    }

    override fun onCharTyped(char: Char, i: Int): Boolean {
        if(super.charTyped(char, i)) {
            return true
        }
        else if(selectedNode != null && SharedConstants.isAllowedChatCharacter(char)) {
            selectedNode!!.textEntry.insertText(char.toString())
            return true
        }
        return false
    }

    private fun loadNodeAndChildren(node: DialogNode, parent: DialogNodeWidget? = null): DialogNodeWidget {
        val widget = createWidgetForNode(node, parent)
        node.children.forEach { widget.children.add(loadNodeAndChildren(it, widget)) }
        this.children.add(widget)
        return widget
    }

    private fun createWidgetForNode(node: DialogNode, parent: DialogNodeWidget?): DialogNodeWidget {
        return DialogNodeWidget(width / 2, height / 2, node.content, node.nodeType, node.nodeId, parent, this)
    }

    fun createSubMenu(mouseX: Int, mouseY: Int, widget: AbstractWidget) {
        if (widget is DialogNodeWidget) {
            val actionMap = HashMap<String, (DialogNodeWidget) -> Unit>()

            if (widget.children.isEmpty() or (widget.children[0].nodeType == DialogNode.NodeType.Dialog))
                actionMap["New Child (Dialog)"] = {
                    println("Make New Dialog Child")
                }
            if (widget.children.isEmpty() or (widget.children[0].nodeType == DialogNode.NodeType.Response))
                actionMap["New Child (Response)"] = {
                    println("Make New Response Child")
                }

            actionMap["Copy Node ID"] = {
                minecraft?.keyboardHandler?.clipboard = it.nodeId.toString()
            }

            if (widget.children.isEmpty())
                actionMap["Remove Node"] = {
                    println("Delete Node")
                }
            else
                actionMap["Remove Node (And Children)"] = {

                }

            submenu = SubmenuWidget(mouseX, mouseY, "${widget.nodeType.name} Node", actionMap)
        }
    }

}