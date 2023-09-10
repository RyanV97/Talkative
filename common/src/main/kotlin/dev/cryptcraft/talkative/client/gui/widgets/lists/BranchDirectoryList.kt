package dev.cryptcraft.talkative.client.gui.widgets.lists

import com.mojang.blaze3d.vertex.PoseStack
import dev.cryptcraft.talkative.api.tree.BranchReference
import dev.cryptcraft.talkative.client.TalkativeClient
import dev.cryptcraft.talkative.client.gui.GuiConstants
import dev.cryptcraft.talkative.client.gui.TalkativeScreen
import dev.cryptcraft.talkative.client.gui.editor.branch.BranchSelectionScreen
import dev.cryptcraft.talkative.client.gui.editor.branch.NodeEditorScreen
import dev.cryptcraft.talkative.client.gui.editor.branch.widgets.nodes.BridgeNodeWidget
import dev.cryptcraft.talkative.client.gui.widgets.IconButton
import dev.cryptcraft.talkative.client.gui.widgets.NestedWidget
import dev.cryptcraft.talkative.common.network.serverbound.RequestBranchForEditPacket
import dev.cryptcraft.talkative.common.network.serverbound.UpdateBranchPacket
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiComponent
import net.minecraft.network.chat.Component

open class BranchDirectoryList(val parentScreen: BranchSelectionScreen, x: Int, y: Int, width: Int, height: Int) : WidgetList<TalkativeScreen>(parentScreen, x, y, width, height) {
    init {
        renderBackground = false
    }

    fun addAttachEntry(value: String) {
        addChild(AttachEntry(parentScreen, value, width))
    }

    fun addBridgeEntry(value: String) {
        addChild(BridgeEntry(parentScreen, value, width))
    }

    fun addEditableEntry(value: String) {
        addChild(EditableEntry(value, width))
    }

    fun contains(value: String): Boolean {
        children.forEach {
            val path = when(it) {
                is AttachEntry -> it.branchPath
                is BridgeEntry -> it.branchPath
                is EditableEntry -> it.branchPath
                else -> null
            }
            if (path.equals(value, true))
                return true
        }
        return false
    }

    override fun recalculateChildren() {
        children.forEachIndexed { index, child ->
            child.x = x
            child.y = index * 20
            child.width = width
        }
    }

    class AttachEntry(val parentScreen: TalkativeScreen, val branchPath: String, width: Int) : NestedWidget(0, 0, width, 20, Component.empty()) {
        private val deleteButton = addChild(IconButton(0, 0, 0, 0, GuiConstants.DELETE_ICON) {
            UpdateBranchPacket(branchPath, UpdateBranchPacket.UpdateAction.DELETE).sendToServer()
        })
        private val attachButton = addChild(IconButton(0, 0, 0, 0, GuiConstants.ATTACH_ICON) {
            TalkativeClient.editingActorData?.dialogBranches?.add(BranchReference(branchPath))
            parentScreen.onClose()
        })
        private val editButton = addChild(IconButton(0, 0, 0, 0, GuiConstants.EDIT_ICON) {
            RequestBranchForEditPacket(branchPath).sendToServer()
        })

        override fun renderButton(poseStack: PoseStack, mouseX: Int, mouseY: Int, partialTicks: Float) {
            GuiComponent.drawString(poseStack, Minecraft.getInstance().font, branchPath, x + 2, y + 6, if (isMouseOver(mouseX.toDouble(), mouseY.toDouble())) 0xFFFFFF else 0xCCCCCC)
            super.renderButton(poseStack, mouseX, mouseY, partialTicks)
        }

        override fun recalculateChildren() {
            super.recalculateChildren()
            deleteButton.x = x + width - height
            deleteButton.y = y
            deleteButton.width = height
            deleteButton.height = height

            attachButton.x = x + width - (height * 2)
            attachButton.y = y
            attachButton.width = height
            attachButton.height = height

            editButton.x = x + width - (height * 3)
            editButton.y = y
            editButton.width = height
            editButton.height = height
        }
    }

    class BridgeEntry(val parentScreen: TalkativeScreen, val branchPath: String, width: Int) : NestedWidget(0, 0, width, 20, Component.empty()) {
        private val linkButton = addChild(IconButton(0, 0, 0, 0, GuiConstants.ATTACH_ICON) {
            val nodeWidget = ((parentScreen.parent as NodeEditorScreen).selectedNode as BridgeNodeWidget)
            val lastScreen = (parentScreen.parent as NodeEditorScreen)
            nodeWidget.setDestinationBranch(branchPath)
            (lastScreen.getPopup() as BridgeNodeWidget.BridgePopup).branchDestinationLabel.contents = Component.literal(branchPath)
            lastScreen.selectedNode = null
            parentScreen.onClose()
        })

        override fun renderButton(poseStack: PoseStack, mouseX: Int, mouseY: Int, partialTicks: Float) {
            GuiComponent.drawString(poseStack, Minecraft.getInstance().font, branchPath, x + 2, y + 6, if (isMouseOver(mouseX.toDouble(), mouseY.toDouble())) 0xFFFFFF else 0xCCCCCC)
            super.renderButton(poseStack, mouseX, mouseY, partialTicks)
        }

        override fun recalculateChildren() {
            super.recalculateChildren()
            linkButton.x = x + width - height
            linkButton.y = y
            linkButton.width = height
            linkButton.height = height
        }
    }

    class EditableEntry(val branchPath: String, width: Int) : NestedWidget(0, 0, width, 20, Component.empty()) {
        private val deleteButton = addChild(IconButton(0, 0, 0, 0, GuiConstants.DELETE_ICON) {
            UpdateBranchPacket(branchPath, UpdateBranchPacket.UpdateAction.DELETE).sendToServer()
        })
        private val editButton = addChild(IconButton(0, 0, 0, 0, GuiConstants.EDIT_ICON) {
            RequestBranchForEditPacket(branchPath).sendToServer()
        })

        override fun renderButton(poseStack: PoseStack, mouseX: Int, mouseY: Int, partialTicks: Float) {
            GuiComponent.drawString(poseStack, Minecraft.getInstance().font, branchPath, x + 2, y + 6, if (isMouseOver(mouseX.toDouble(), mouseY.toDouble())) 0xFFFFFF else 0xCCCCCC)
            super.renderButton(poseStack, mouseX, mouseY, partialTicks)
        }

        override fun recalculateChildren() {
            super.recalculateChildren()
            deleteButton.x = x + width - height
            deleteButton.y = y
            deleteButton.width = height
            deleteButton.height = height

            editButton.x = x + width - (height * 2)
            editButton.y = y
            editButton.width = height
            editButton.height = height
        }
    }
}