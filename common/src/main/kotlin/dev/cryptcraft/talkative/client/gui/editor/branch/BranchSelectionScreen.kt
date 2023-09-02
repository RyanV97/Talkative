package dev.cryptcraft.talkative.client.gui.editor.branch

import com.mojang.blaze3d.vertex.PoseStack
import dev.cryptcraft.talkative.client.gui.GuiConstants
import dev.cryptcraft.talkative.client.gui.TalkativeScreen
import dev.cryptcraft.talkative.client.gui.widgets.TalkativeButton
import dev.cryptcraft.talkative.client.gui.widgets.lists.BranchDirectoryList
import dev.cryptcraft.talkative.client.gui.widgets.popup.PopupWidget
import dev.cryptcraft.talkative.common.network.serverbound.RequestBranchListPacket
import dev.cryptcraft.talkative.common.network.serverbound.UpdateBranchPacket
import net.minecraft.client.gui.components.Button
import net.minecraft.nbt.ListTag
import net.minecraft.network.chat.Component

class BranchSelectionScreen(parentScreen: TalkativeScreen, private val mode: ListMode) : TalkativeScreen(parentScreen, Component.empty()) {
    private val branchList = BranchDirectoryList(this, 5, 26, 0, 0)

    override fun init() {
        super.init()
        branchList.width = width - 10
        branchList.height = height - 26

        addRenderableWidget(branchList)
        addRenderableWidget(TalkativeButton(3, 3, 70, 20, Component.literal("New Branch"), ::openCreateBranchPopup))
        RequestBranchListPacket().sendToServer()
    }

    override fun render(poseStack: PoseStack, mouseX: Int, mouseY: Int, delta: Float) {
        fill(poseStack, 0, 0, width, height, GuiConstants.COLOR_EDITOR_BG_PRIMARY)
        super.render(poseStack, mouseX, mouseY, delta)
    }

    fun loadBranchList(listTag: ListTag?) {
        branchList.clear()
        listTag?.forEach {
            when (mode) {
                ListMode.ATTACH -> branchList.addAttachEntry(it.asString)
                ListMode.BRIDGE_LINK -> branchList.addBridgeEntry(it.asString)
                ListMode.EDIT -> branchList.addEditableEntry(it.asString)
            }
        }
    }

    private fun createBranch(path: String) {
        UpdateBranchPacket(path, UpdateBranchPacket.UpdateAction.CREATE).sendToServer()
        closePopup()
    }

    private fun openCreateBranchPopup(button: Button) {
        openPopup(PopupWidget.Builder((width / 2) - 155, (height / 2) - 15, 310, 30, this)
            .textField(5, 5, width = 195)
            .button(205, 5, "Save") {
                createBranch(getPopup()!!.getAllTextFields()[0].value)
            }
            .button(259, 5, "Cancel") {
                closePopup()
            }
            .build())
    }

    enum class ListMode {
        ATTACH, BRIDGE_LINK, EDIT
    }
}