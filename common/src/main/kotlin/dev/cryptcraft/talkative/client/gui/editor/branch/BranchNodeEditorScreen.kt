package dev.cryptcraft.talkative.client.gui.editor.branch

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.Tesselator
import com.mojang.blaze3d.vertex.VertexFormat
import dev.cryptcraft.talkative.api.tree.node.BridgeNode
import dev.cryptcraft.talkative.api.tree.node.NodeBase
import dev.cryptcraft.talkative.api.tree.node.TextNode
import dev.cryptcraft.talkative.client.NodePositioner
import dev.cryptcraft.talkative.client.TalkativeClient
import dev.cryptcraft.talkative.client.data.ConditionalContext
import dev.cryptcraft.talkative.client.gui.EditorScreen
import dev.cryptcraft.talkative.client.gui.GuiConstants
import dev.cryptcraft.talkative.client.gui.TalkativeScreen
import dev.cryptcraft.talkative.client.gui.editor.ConditionalEditorPopup
import dev.cryptcraft.talkative.client.gui.editor.branch.widgets.nodes.BridgeNodeWidget
import dev.cryptcraft.talkative.client.gui.editor.branch.widgets.nodes.NodeWidget
import dev.cryptcraft.talkative.client.gui.editor.branch.widgets.nodes.TextNodeWidget
import dev.cryptcraft.talkative.client.gui.widgets.SubmenuWidget
import dev.cryptcraft.talkative.client.gui.widgets.TalkativeButton
import dev.cryptcraft.talkative.common.markdown.MarkdownParser
import dev.cryptcraft.talkative.common.network.serverbound.UpdateBranchPacket
import dev.cryptcraft.talkative.common.network.serverbound.UpdateNodeConditionalPacket
import dev.cryptcraft.talkative.mixin.client.AbstractWidgetAccessor
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.network.chat.Component
import kotlin.math.ceil
import kotlin.math.floor

class BranchNodeEditorScreen(parent: Screen?) : TalkativeScreen(parent, Component.literal("Dialog Tree Editor")), EditorScreen {
    private var rootNodeWidget: NodeWidget? = null
    private var nodeWidgets: ArrayList<NodeWidget> = ArrayList()
    var selectedNode: NodeWidget? = null

    var offsetX: Int = 100
    var offsetY: Int = -50
    var zoomScale: Float = 1.0F

    override fun init() {
        super.init()

        if (rootNodeWidget == null) {
            refresh()
            offsetX = rootNodeWidget!!.width / 2
            offsetY = -(rootNodeWidget!!.height / 2)
        }
        else
            refresh()

        addRenderableWidget(TalkativeButton(5, 5, 35, 20, Component.literal("Save")) {
            saveChanges()
            onClose()
        })

        addRenderableWidget(TalkativeButton(45, 5, 50, 20, Component.literal("Cancel")) {
            onClose()
        })
    }

    override fun render(poseStack: PoseStack, mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground(poseStack)
        poseStack.pushPose()
        poseStack.scale(zoomScale, zoomScale, 0.0F)

        rootNodeWidget?.renderNodeAndChildren(poseStack, mouseX, mouseY, delta)

        poseStack.popPose()

        fill(poseStack, 0, 0, width, 30, 0xA50F0F0F.toInt())
        super.render(poseStack, mouseX, mouseY, delta)
    }

    override fun renderBackground(poseStack: PoseStack) {
        val tesselator = Tesselator.getInstance()
        val bufferBuilder = tesselator.builder
        RenderSystem.setShader { GameRenderer.getPositionTexColorShader() }
        RenderSystem.setShaderTexture(0, GuiConstants.BRANCH_EDITOR_BACKGROUND)
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
        val scale = 16f
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR)
        bufferBuilder.vertex(0.0, height.toDouble(), 0.0).uv(0.0f, height.toFloat() / scale).color(80, 100, 80, 255).endVertex()
        bufferBuilder.vertex(width.toDouble(), height.toDouble(), 0.0).uv(width.toFloat() / scale, height.toFloat() / scale).color(64, 85, 64, 255).endVertex()
        bufferBuilder.vertex(width.toDouble(), 0.0, 0.0).uv(width.toFloat() / scale, 0f).color(64, 69, 64, 255).endVertex()
        bufferBuilder.vertex(0.0, 0.0, 0.0).uv(0f, 0f).color(64, 64, 64, 255).endVertex()
        tesselator.end()
    }

    private fun saveChanges() {
        TalkativeClient.editingBranch?.let { branch ->
            branch.clearNodes()
            nodeWidgets.forEach { widget ->
                val node = widget.node
                if (node is TextNode) {
                    MarkdownParser.parse(node.getContents().string)?.let { node.setContents(it) }
                }
                branch.addNode(node)
            }
            UpdateBranchPacket(TalkativeClient.editingBranchPath!!, UpdateBranchPacket.UpdateAction.MODIFY, branch).sendToServer()
        }
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
        if (selectedNode is TextNodeWidget)
            return (selectedNode!! as TextNodeWidget).editBox.mouseScrolled(mouseX, mouseY, scrollAmount)

        val i = (scrollAmount.toFloat() * 0.1F)
        zoomScale = (zoomScale + i).coerceIn(0.2F, 2.0F)
        return true
    }

    override fun onKeyPressed(keyCode: Int, j: Int, k: Int): Boolean {
        if (selectedNode != null && keyCode == 256) {
            if (selectedNode is TextNodeWidget)
                ((selectedNode as TextNodeWidget).editBox as AbstractWidgetAccessor).pleaseSetFocused(false)
            selectedNode = null
            return true
        }
        return selectedNode?.keyPressed(keyCode, j, k) ?: false
    }

    private fun loadNodeAndChildren(node: NodeBase, parent: NodeWidget? = null): NodeWidget? {
        val widget = createWidgetForNode(node, parent) ?: return null
        node.getChildren().forEach {
            val child = TalkativeClient.editingBranch?.getNode(it.nodeId)
            if (child != null)
                widget.childNodes.add(loadNodeAndChildren(child, widget) ?: return@forEach)
//            else //ToDo Add a node type/widget to show child connections to non-existing IDs
//                widget.childNodes.add(MissingNodeWidget(0, 0, node, this))
        }
        return widget
    }

    fun createWidgetForNode(node: NodeBase, parent: NodeWidget?): NodeWidget? {
        val widget = when (node) {
            is TextNode -> TextNodeWidget(width / 2, height / 2, node, parent, this)
            is BridgeNode -> BridgeNodeWidget(width / 2, height / 2, node, parent, this)
            else -> return null //ToDo MissingNodeWidget(width / 2, height / 2, parent, this)
        }

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
        rootNodeWidget = null
    }

    override fun refresh() {
        clearNodes()
        rootNodeWidget = loadNodeAndChildren(TalkativeClient.editingBranch!!.getNode(0) ?: return)
        NodePositioner.layoutTree(rootNodeWidget!!)
    }

    override fun shouldCloseOnEsc(): Boolean {
        return false
    }

    override fun onClose() {
        minecraft?.setScreen(parent)
    }

    fun createSubMenu(mouseX: Int, mouseY: Int, widget: AbstractWidget) {
        if (widget is NodeWidget) {
            val actionMap = LinkedHashMap<String, () -> Unit>()

            addChildEntries(actionMap, widget)

            if (widget != rootNodeWidget) {
                actionMap["Edit Conditional"] = {
                    val context = ConditionalContext.NodeContext(TalkativeClient.editingBranchPath!!, widget.node.nodeId, widget.node.getConditional())
                    val popupSize = height - 10
                    val popupX = (width / 2) - (popupSize / 2)
                    openPopup(ConditionalEditorPopup(this, popupX, 10, popupSize, popupSize, context) {
                        val newContext = it as ConditionalContext.NodeContext
                        widget.node.setConditional(newContext.conditional)
                        UpdateNodeConditionalPacket(newContext.branchPath, newContext.nodeId, newContext.conditional).sendToServer()
                    })
                    closeSubmenu()
                }
            }

            actionMap["Edit Commands"] = {
                val popupWidth = 250
                val popupHeight = height - 20
                openPopup(CommandEditorPopup(this, widget.node, (width / 2) - (popupWidth / 2), (height / 2) - (popupHeight / 2), popupWidth, popupHeight))
                closeSubmenu()
            }

            actionMap["Copy Node ID"] = {
                minecraft?.keyboardHandler?.clipboard = widget.node.nodeId.toString()
                //ToDo: Visual feedback to confirm that ID was copied
            }

            if (rootNodeWidget!! != widget) {
                if (widget.childNodes.isEmpty())
                    actionMap["Delete Node"] = {
                        widget.parentWidget?.removeChildNode(widget)
                        NodePositioner.layoutTree(rootNodeWidget!!)
                        closeSubmenu()
                    }
                else
                    actionMap["Delete Node (And Children)"] = {
                        widget.parentWidget?.removeChildNode(widget)
                        NodePositioner.layoutTree(rootNodeWidget!!)
                        closeSubmenu()
                    }
            }

            submenu = SubmenuWidget(mouseX, mouseY, "${widget.node.getNodeType().name} Node", actionMap)
        }
    }

    fun addChildEntries(actionMap: LinkedHashMap<String, () -> Unit>, nodeWidget: NodeWidget) {
        if (nodeWidget.node.getNodeType() == NodeBase.NodeType.Bridge)
            return

        for (type in NodeBase.NodeType.values()) {
            if (nodeWidget.childNodes.isEmpty() || nodeWidget.node.getChildrenType() == type) {
                actionMap["New Child (${type.name})"] = { addNewChild(nodeWidget, type) }
            }
        }
    }

    private fun addNewChild(parentWidget: NodeWidget, type: NodeBase.NodeType) {
        val newNode = NodeBase.createNodeFromType(type, TalkativeClient.editingBranch!!.highestId)
        TalkativeClient.editingBranch!!.addNode(newNode!!)
        parentWidget.node.addChild(newNode)
        refresh()
        closeSubmenu()
    }
}