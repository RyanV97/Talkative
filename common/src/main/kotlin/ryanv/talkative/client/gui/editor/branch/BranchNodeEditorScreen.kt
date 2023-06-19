package ryanv.talkative.client.gui.editor.branch

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.SharedConstants
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.screens.Screen
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import ryanv.talkative.client.TalkativeClient
import ryanv.talkative.client.gui.DataScreen
import ryanv.talkative.client.gui.TalkativeScreen
import ryanv.talkative.client.gui.editor.ConditionalEditorPopup
import ryanv.talkative.client.gui.editor.widgets.NodeWidget
import ryanv.talkative.client.gui.widgets.SubmenuWidget
import ryanv.talkative.client.util.ConditionalContext
import ryanv.talkative.client.util.NodePositioner
import ryanv.talkative.common.data.tree.DialogNode
import ryanv.talkative.common.network.serverbound.UpdateBranchPacket
import ryanv.talkative.common.network.serverbound.UpdateNodeConditionalPacket
import kotlin.math.ceil
import kotlin.math.floor

class BranchNodeEditorScreen(parent: Screen?) : TalkativeScreen(parent, Component.literal("Dialog Tree Editor")), DataScreen {
    var nodeWidgets: ArrayList<NodeWidget> = ArrayList()
    var rootNodeWidget: NodeWidget? = null
    var selectedNode: NodeWidget? = null

    var offsetX: Int = 100
    var offsetY: Int = -50
    var zoomScale: Float = 1.0F

    override fun init() {
        super.init()
        TalkativeClient.editingBranch?.let {
            refresh()
            offsetX = rootNodeWidget!!.width / 2
            offsetY = -(rootNodeWidget!!.height / 2)
        }

        addRenderableWidget(Button(width - 50, height - 20, 50, 20, Component.literal("Save")) {
            saveChanges()
            onClose()
        })

        addRenderableWidget(Button(width - 100, height - 20, 50, 20, Component.literal("Discard")) {
            onClose()
        })

        addRenderableWidget(Button(width - 150, height - 20, 50, 20, Component.literal("Options")) {
            NodePositioner.layoutTree(rootNodeWidget!!)
        })
    }

    override fun render(poseStack: PoseStack?, mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground(poseStack!!)

        poseStack.pushPose()
        poseStack.scale(zoomScale, zoomScale, 1.0F)

        rootNodeWidget?.renderNodeAndChildren(poseStack, mouseX, mouseY, delta)

        poseStack.popPose()

        super.render(poseStack, mouseX, mouseY, delta)
    }

    private fun saveChanges() {
        nodeWidgets.forEach {
            val newNode = it.serializeNodeAndChildren();
            TalkativeClient.editingBranch?.addNode(newNode)
        }
        UpdateBranchPacket(TalkativeClient.editingBranchPath!!, UpdateBranchPacket.UpdateAction.MODIFY, TalkativeClient.editingBranch?.serialize(CompoundTag())).sendToServer()
    }

    override fun onMouseClick(mouseX: Double, mouseY: Double, mouseButton: Int): Boolean {
        return false
    }

    override fun onMouseRelease(mouseX: Double, mouseY: Double, mouseButton: Int): Boolean {
        return false
    }

    override fun onMouseDrag(mouseX: Double, mouseY: Double, mouseButton: Int, diffX: Double, diffY: Double): Boolean {
        if (mouseButton == 2) {
            if (!isDragging)
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
        if (selectedNode != null) {
            val textEntry = selectedNode!!.textEntry
            if (isSelectAll(keyCode))
                textEntry.selectAll()
            if (isCut(keyCode))
                textEntry.cut()
            if (isCopy(keyCode))
                textEntry.copy()
            if (isPaste(keyCode))
                textEntry.paste()
            if (keyCode == 257 || keyCode == 335)
                textEntry.insertText("\n")
            when (keyCode) {
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

    private fun loadNodeAndChildren(node: DialogNode, parent: NodeWidget? = null): NodeWidget {
        val widget = createWidgetForNode(node, parent)
        node.getChildren().forEach { widget.children.add(loadNodeAndChildren(TalkativeClient.editingBranch?.getNode(it)!!, widget)) }
        return widget
    }

    fun createWidgetForNode(node: DialogNode, parent: NodeWidget?): NodeWidget {
        val widget = NodeWidget(width / 2, height / 2, node.content, node.nodeType, node.nodeId, parent, this)
        addChild(widget)
        return widget
    }

    private fun addChild(child: NodeWidget) {
        addWidget(child)
        nodeWidgets.add(child)
    }

    fun removeChild(child: NodeWidget) {
        removeWidget(child)
        nodeWidgets.remove(child)
    }

    private fun clearNodes() {
        children().removeIf { it is NodeWidget }
        nodeWidgets.clear()
    }

    override fun refresh() {
        clearNodes()
        rootNodeWidget = TalkativeClient.editingBranch!!.getNode(0)?.let { loadNodeAndChildren(it) }
        NodePositioner.layoutTree(rootNodeWidget!!)
    }

    override fun onClose() {
        minecraft?.setScreen(parent)
    }

    fun createSubMenu(mouseX: Int, mouseY: Int, widget: AbstractWidget) {
        if (widget is NodeWidget) {
            val actionMap = HashMap<String, () -> Unit>()

            if (widget.children.isEmpty() || (widget.children[0].nodeType == DialogNode.NodeType.Dialog))
                actionMap["New Child (Dialog)"] = {
                    println("Make New Dialog Child")
                    widget.addChild(DialogNode.NodeType.Dialog, TalkativeClient.editingBranch!!.highestId)
                    NodePositioner.layoutTree(rootNodeWidget!!)
                    closeSubmenu()
                }
            if (widget.children.isEmpty() || (widget.children[0].nodeType == DialogNode.NodeType.Response))
                actionMap["New Child (Response)"] = {
                    println("Make New Response Child")
                    widget.addChild(DialogNode.NodeType.Response, TalkativeClient.editingBranch!!.highestId)
                    NodePositioner.layoutTree(rootNodeWidget!!)
                    closeSubmenu()
                }

            actionMap["Edit Conditional"] = {
                val context = ConditionalContext.NodeContext(TalkativeClient.editingBranchPath!!, widget.nodeId, TalkativeClient.editingBranch?.getNode(widget.nodeId)?.getConditional())
                val popupSize = height - 10
                val popupX = (width / 2) - (popupSize / 2)
                popup = ConditionalEditorPopup(this, popupX, 10, popupSize, popupSize, context) {
                    val newContext = it as ConditionalContext.NodeContext
                    UpdateNodeConditionalPacket(newContext.branchPath, newContext.nodeId, newContext.conditional).sendToServer()
                    closePopup()
                }
                closeSubmenu()
            }

            actionMap["Copy Node ID"] = {
                minecraft?.keyboardHandler?.clipboard = widget.nodeId.toString()
            }

            if (rootNodeWidget!! != widget) {
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