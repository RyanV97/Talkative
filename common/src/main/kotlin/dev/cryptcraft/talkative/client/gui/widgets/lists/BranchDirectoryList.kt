package dev.cryptcraft.talkative.client.gui.widgets.lists

import com.mojang.blaze3d.vertex.PoseStack
import dev.cryptcraft.talkative.client.TalkativeClient
import dev.cryptcraft.talkative.client.gui.GuiConstants
import dev.cryptcraft.talkative.client.gui.TalkativeScreen
import dev.cryptcraft.talkative.client.gui.editor.branch.BranchSelectionScreen
import dev.cryptcraft.talkative.client.gui.widgets.IconButton
import dev.cryptcraft.talkative.client.gui.widgets.NestedWidget
import dev.cryptcraft.talkative.common.network.serverbound.AttachBranchPacket
import dev.cryptcraft.talkative.common.network.serverbound.RequestBranchForEditPacket
import dev.cryptcraft.talkative.common.network.serverbound.UpdateBranchPacket
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiComponent
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.Component

open class BranchDirectoryList(parentScreen: BranchSelectionScreen, x: Int, y: Int, width: Int, height: Int) : WidgetList<TalkativeScreen>(parentScreen, x, y, width, height) {
    init {
        renderBackground = false
    }

    fun addEntry(value: String) {
        addChild(BranchEntry(value, width))
    }

    fun addAttachEntry(value: String) {
        addChild(AttachEntry(value, width))
    }

    fun addEditableEntry(value: String) {
        addChild(EditableEntry(value, width))
    }

    override fun recalculateChildren() {
        children.forEachIndexed { index, child ->
            child.x = x
            child.y = index * 20
            child.width = width
        }
    }

    open class BranchEntry(val branchPath: String, width: Int) : NestedWidget(0, 0, width, 20, Component.empty()) {
        private val deleteButton = addChild(IconButton(0, 0, 0, 0, GuiConstants.DELETE_ICON) {
            UpdateBranchPacket(branchPath, UpdateBranchPacket.UpdateAction.DELETE).sendToServer()
        })

        override fun renderButton(poseStack: PoseStack, mouseX: Int, mouseY: Int, partialTicks: Float) {
            GuiComponent.drawString(poseStack, Minecraft.getInstance().font, branchPath, x + 2, y + 6, if (isMouseOver(mouseX.toDouble(), mouseY.toDouble())) 0xFFFFFF else 0xCCCCCC)
            super.renderButton(poseStack, mouseX, mouseY, partialTicks)
        }

        override fun recalculateChildren() {
            deleteButton.x = x + width - height
            deleteButton.y = y
            deleteButton.width = height
            deleteButton.height = height
        }

        override fun updateNarration(narrationElementOutput: NarrationElementOutput) {}
    }

    class AttachEntry(branchPath: String, width: Int) : BranchEntry(branchPath, width) {
        private val attachButton = addChild(IconButton(0, 0, 0, 0, GuiConstants.ATTACH_ICON) {
            AttachBranchPacket(TalkativeClient.editingActorEntity!!.id, branchPath).sendToServer()
        })
        private val editButton = addChild(IconButton(0, 0, 0, 0, GuiConstants.EDIT_ICON) {
            RequestBranchForEditPacket(branchPath).sendToServer()
        })

        override fun recalculateChildren() {
            super.recalculateChildren()
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

    class EditableEntry(branchPath: String, width: Int) : BranchEntry(branchPath, width) {
        private val editButton = addChild(IconButton(0, 0, 0, 0, GuiConstants.EDIT_ICON) {
            RequestBranchForEditPacket(branchPath).sendToServer()
        })

        override fun recalculateChildren() {
            super.recalculateChildren()
            editButton.x = x + width - (height * 2)
            editButton.y = y
            editButton.width = height
            editButton.height = height
        }
    }
}