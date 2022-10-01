package ryanv.talkative.client.gui.editor

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.SharedConstants
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.components.Button
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.TextComponent
import ryanv.talkative.client.gui.TalkativeScreen
import ryanv.talkative.client.gui.widgets.DialogNodeWidget
import ryanv.talkative.client.gui.widgets.SubmenuWidget
import ryanv.talkative.client.util.NodePositioner
import ryanv.talkative.common.data.tree.DialogBranch
import ryanv.talkative.common.data.tree.DialogNode
import ryanv.talkative.common.network.NetworkHandler
import ryanv.talkative.common.network.c2s.UpdateBranchPacket
import kotlin.math.ceil
import kotlin.math.floor

class BranchEditorScreen(parent: TalkativeScreen?, private val branchPath: String, private val branch: DialogBranch): TalkativeScreen(parent, TextComponent("Dialog Tree Editor")) {
    var offsetX: Int = 0
    var offsetY: Int = 0
    var zoomScale: Float = 1.0F

    var rootNodeWidget: DialogNodeWidget? = null
    var nodeWidgets: ArrayList<DialogNodeWidget> = ArrayList()
    var selectedNode: DialogNodeWidget? = null

    override fun init() {
        rootNodeWidget = branch.nodes[0]?.let { loadNodeAndChildren(it) }
        NodePositioner.layoutTree(rootNodeWidget!!)

        addButton(Button(width - 50, height - 20, 50, 20, TextComponent("Save")) {
            saveChanges()
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

    private fun saveChanges() {
        branch.nodes.clear()
        nodeWidgets.forEach {
            branch.nodes[it.nodeId] = it.serializeNodeAndChildren()
        }
        NetworkHandler.CHANNEL.sendToServer(UpdateBranchPacket(branchPath, branch.serialize(CompoundTag())))
    }

    override fun onMouseClick(mouseX: Double, mouseY: Double, mouseButton: Int): Boolean {
        return false
    }

    override fun onMouseRelease(mouseX: Double, mouseY: Double, mouseButton: Int): Boolean {
        return false
    }

    override fun onMouseDrag(mouseX: Double, mouseY: Double, mouseButton: Int, diffX: Double, diffY: Double): Boolean {
        if(mouseButton == 2) {
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
            if(keyCode == 257 || keyCode == 335)
                textEntry.insertText("\n")
            when(keyCode) {
                256 -> {
                    selectedNode = null
                    focused = null
                }
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
        if(selectedNode != null && SharedConstants.isAllowedChatCharacter(char)) {
            selectedNode!!.textEntry.insertText(char.toString())
            return true
        }
        return false
    }

    private fun loadNodeAndChildren(node: DialogNode, parent: DialogNodeWidget? = null): DialogNodeWidget {
        val widget = createWidgetForNode(node, parent)
        node.getChildren().forEach { widget.children.add(loadNodeAndChildren(branch.nodes[it]!!, widget)) }
        return widget
    }

    fun createWidgetForNode(node: DialogNode, parent: DialogNodeWidget?): DialogNodeWidget {
        val widget = DialogNodeWidget(width / 2, height / 2, node.content, node.nodeType, node.nodeId, parent, this)
        addChild(widget)
        nodeWidgets.add(widget)
        return widget
    }

    private fun addChild(child: DialogNodeWidget) {
        children.add(child)
    }

    fun removeChild(child: DialogNodeWidget) {
        children.remove(child)
        nodeWidgets.remove(child)
    }

    fun createSubMenu(mouseX: Int, mouseY: Int, widget: AbstractWidget) {
        if (widget is DialogNodeWidget) {
            val actionMap = HashMap<String, () -> Unit>()

            if (widget.children.isEmpty() || (widget.children[0].nodeType == DialogNode.NodeType.Dialog))
                actionMap["New Child (Dialog)"] = {
                    println("Make New Dialog Child")
                    widget.addChild(DialogNode.NodeType.Dialog, branch.highestId)
                    NodePositioner.layoutTree(rootNodeWidget!!)
                    closeSubmenu()
                }
            if (widget.children.isEmpty() || (widget.children[0].nodeType == DialogNode.NodeType.Response))
                actionMap["New Child (Response)"] = {
                    println("Make New Response Child")
                    widget.addChild(DialogNode.NodeType.Response, branch.highestId)
                    NodePositioner.layoutTree(rootNodeWidget!!)
                    closeSubmenu()
                }

            actionMap["Copy Node ID"] = {
                minecraft?.keyboardHandler?.clipboard = widget.nodeId.toString()
            }

            if(rootNodeWidget!! != widget) {
                if (widget.children.isEmpty())
                    actionMap["Remove Node"] = {
                        println("Delete Node")
                        widget.parentWidget?.removeChild(widget)
                        NodePositioner.layoutTree(rootNodeWidget!!)
                        closeSubmenu()
                    }
                else
                    actionMap["Remove Node (And Children)"] = {
                        println("Delete Node and Children")
                        widget.parentWidget?.removeChild(widget)
                        NodePositioner.layoutTree(rootNodeWidget!!)
                        closeSubmenu()
                    }
            }

            submenu = SubmenuWidget(mouseX, mouseY, "${widget.nodeType.name} Node", actionMap)
        }
    }
}