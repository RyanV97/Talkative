package dev.cryptcraft.talkative.client.gui.editor.widgets

import com.mojang.blaze3d.vertex.PoseStack
import dev.cryptcraft.talkative.api.tree.BranchReference
import dev.cryptcraft.talkative.client.TalkativeClient
import dev.cryptcraft.talkative.client.data.ConditionalContext
import dev.cryptcraft.talkative.client.gui.GuiConstants
import dev.cryptcraft.talkative.client.gui.editor.ConditionalEditorPopup
import dev.cryptcraft.talkative.client.gui.editor.MainEditorScreen
import dev.cryptcraft.talkative.client.gui.editor.tabs.ActorTab
import dev.cryptcraft.talkative.client.gui.widgets.IconButton
import dev.cryptcraft.talkative.client.gui.widgets.NestedWidget
import dev.cryptcraft.talkative.client.gui.widgets.lists.WidgetList
import dev.cryptcraft.talkative.common.network.serverbound.RequestBranchForEditPacket
import dev.cryptcraft.talkative.common.network.serverbound.UnAttachBranchPacket
import dev.cryptcraft.talkative.common.network.serverbound.UpdateBranchConditionalPacket
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiComponent
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style

class ActorBranchList(private val parentTab: ActorTab, x: Int, y: Int, width: Int, height: Int, title: Component) : WidgetList<MainEditorScreen>(parentTab.parentScreen, x, y, width, height, title) {

    fun addEntry(actorIndex: Int, branch: BranchReference) {
        addChild(BranchListEntry(parentTab, actorIndex, branch))
    }

    class BranchListEntry(private val parentTab: ActorTab, val index: Int, val branch: BranchReference) : NestedWidget(0, 0, 0, 20, Component.literal(branch.filePath)) {
        private val editButton = addChild(IconButton(0, 0, 20, 20, GuiConstants.EDIT_ICON, {
            RequestBranchForEditPacket(branch.filePath).sendToServer()
        }, ::handleTooltip))
        private val conditionalButton = addChild(IconButton(0, 0, 20, 20, GuiConstants.CONDITIONAL_ICON, ::openConditionalEditor, ::handleTooltip))
        private val detachButton = addChild(IconButton(0, 0, 20, 20, GuiConstants.DETACH_ICON, ::detachBranch, ::handleTooltip))

        init {
            conditionalButton.active = branch.valid
        }

        override fun renderButton(poseStack: PoseStack, mouseX: Int, mouseY: Int, partialTicks: Float) {
            val color: Int = if(!branch.valid) 0xFF0000 else if (isMouseOver(mouseX.toDouble(), mouseY.toDouble())) 0xFFFFFF else 0xCCCCCC
            val label = Component.literal(branch.filePath)

            if (!branch.valid) label.style = Style.EMPTY.withColor(ChatFormatting.RED).withBold(true)

            GuiComponent.drawString(poseStack, Minecraft.getInstance().font, label, x + 2, y + 6, color)

            super.renderButton(poseStack, mouseX, mouseY, partialTicks)
        }

        override fun recalculateChildren() {
            //Button - Edit Branch Button
            this.editButton.x = this.x + this.width - 60
            this.editButton.y = this.y

            //Button - Edit Conditional Button
            this.conditionalButton.x = this.x + this.width - 40
            this.conditionalButton.y = this.y

            //Button - Detach Branch Button
            this.detachButton.x = this.x + this.width - 20
            this.detachButton.y = this.y
        }

        private fun openConditionalEditor(button: Button) {
            val parentScreen = parentTab.parentScreen
            val context = ConditionalContext.BranchContext(TalkativeClient.editingActorEntity!!.id, index, branch.getConditional())

            val popupSize = parentScreen.height - 10
            val popupX = (parentTab.width / 2) - (popupSize / 2)

            parentScreen.popup = ConditionalEditorPopup(parentScreen, popupX, 10, popupSize, popupSize, context) {
                val newContext = it as ConditionalContext.BranchContext
                UpdateBranchConditionalPacket(newContext.actorId, newContext.branchIndex, newContext.conditional).sendToServer()
                parentTab.parentScreen.closePopup()
            }
        }

        private fun detachBranch(button: Button) {
            UnAttachBranchPacket(TalkativeClient.editingActorEntity!!.id, index).sendToServer()
        }

        private fun handleTooltip(button: Button, poseStack: PoseStack, mouseX: Int, mouseY: Int) {
            if (button == this.conditionalButton && !branch.valid)
                return

            val tooltip =
                if (button == this.editButton) {
                    Component.literal("Edit Branch")
                }
                else if (button == this.conditionalButton) {
                    Component.literal("Edit Conditional")
                }
                else
                    Component.literal("Detach Branch").withStyle {
                        return@withStyle it.withBold(true).withColor(ChatFormatting.RED)
                    }

            parentTab.parentScreen.renderTooltip(poseStack, tooltip, button.x + 10, button.y + 18)
        }

        override fun updateNarration(narrationElementOutput: NarrationElementOutput) {
        }
    }
}