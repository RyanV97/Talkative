package dev.cryptcraft.talkative.client.gui.editor.branch

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.gui.GuiComponent
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.screens.Screen
import net.minecraft.nbt.ListTag
import net.minecraft.network.chat.Component
import dev.cryptcraft.talkative.client.gui.TalkativeScreen
import dev.cryptcraft.talkative.client.gui.widgets.lists.StringSelectionList
import dev.cryptcraft.talkative.client.gui.widgets.popup.PopupWidget
import dev.cryptcraft.talkative.common.network.serverbound.RequestBranchListPacket
import dev.cryptcraft.talkative.common.network.serverbound.UpdateBranchPacket

abstract class BranchDirectoryScreen(parent: Screen?) : TalkativeScreen(parent, Component.empty()) {
    protected lateinit var list: StringSelectionList

    override fun init() {
        super.init()

        val listRight = width - (width / 3)

        list = addWidget(StringSelectionList(this, 0, 20, listRight, height - 20, ::onSelectionChange))

        addRenderableWidget(Button(listRight, height - 20, 70, 20, Component.literal("New Branch")) {
            popup = PopupWidget.Builder((width / 2) - 155, (height / 2) - 15, 310, 30, this)
                .textField(5, 5, width = 195)
                .button(205, 5, "Save") {
                    createBranch(popup!!.getAllTextFields()[0].value)
                }
                .button(259, 5, "Cancel") {
                    closePopup()
                }
                .build()
        })

        RequestBranchListPacket().sendToServer()
    }

    fun loadBranchList(listTag: ListTag?) {
        list.clear()
        listTag?.forEach {
            list.addEntry(it.asString)
        }
    }

    override fun render(poseStack: PoseStack?, mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground(poseStack!!)
        GuiComponent.drawCenteredString(poseStack, font, "Available Branches", width / 2, 6, 0xFFFFFF)
        list.render(poseStack, mouseX, mouseY, delta)
        super.render(poseStack, mouseX, mouseY, delta)
    }

    private fun createBranch(path: String) {
        UpdateBranchPacket(path, UpdateBranchPacket.UpdateAction.CREATE).sendToServer()
        closePopup()
    }

    private fun getSelectedPath(): String {
        return if (list.selectedEntry != null) list.selectedEntry!!.value else ""
    }

    open fun onSelectionChange(selection: StringSelectionList.StringEntry?) {}
}