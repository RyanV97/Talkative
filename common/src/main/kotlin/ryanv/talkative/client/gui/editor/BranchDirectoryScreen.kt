package ryanv.talkative.client.gui.editor

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.screens.Screen
import net.minecraft.nbt.ListTag
import net.minecraft.network.chat.TextComponent
import ryanv.talkative.client.gui.TalkativeScreen
import ryanv.talkative.client.gui.widgets.popup.PopupWidget
import ryanv.talkative.client.gui.widgets.lists.StringSelectionList
import ryanv.talkative.client.gui.widgets.lists.TalkativeList
import ryanv.talkative.common.network.clientbound.SyncBranchListPacket
import ryanv.talkative.common.network.serverbound.RequestBranchListPacket
import ryanv.talkative.common.network.serverbound.UpdateBranchPacket

class BranchDirectoryScreen(parent: Screen?, private var onConfirm: (selection: StringSelectionList.StringEntry?) -> Unit) : TalkativeScreen(parent, TextComponent.EMPTY) {
    private lateinit var list: StringSelectionList
    private var selectedEntry: StringSelectionList.StringEntry? = null

    private lateinit var confirmButton: Button

    override fun init() {
        super.init()

        val listRight = width - (width / 3)

        list = addWidget(StringSelectionList(0, height, 0, listRight, ::onSelectionChange))

        confirmButton = addButton(Button(width - 50, height - 20, 50, 20, TextComponent("Confirm")) {
            onConfirm(selectedEntry)
            onClose()
        })
        confirmButton.active = false

        addButton(Button(listRight, height - 20, 70, 20, TextComponent("New Branch")) {
            popup = PopupWidget((width / 2) - 155, (height / 2) - 15, 310, 30, this)
                .textField(5, 5, width = 195, defaultString = getSelectedPath())
                .button(205, 5, "Save") {
                    createBranch(popup!!.getAllTextFields()[0].value)
                }
                .button(259, 5, "Cancel") {
                    closePopup()
                }
        })

        RequestBranchListPacket().sendToServer()
    }

    fun loadBranchList(list: ListTag?) {
        this.list.children().clear()
        list?.forEach {
            this.list.addEntry(it.asString)
        }
    }

    override fun render(poseStack: PoseStack?, mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground(poseStack)
        list.render(poseStack, mouseX, mouseY, delta)
        super.render(poseStack, mouseX, mouseY, delta)
    }

    override fun onClose() {
        minecraft!!.setScreen(parent)
    }

    private fun createBranch(path: String) {
        UpdateBranchPacket(path, UpdateBranchPacket.UpdateAction.CREATE).sendToServer()
        closePopup()
    }

    private fun getSelectedPath(): String {
        return if (selectedEntry != null) selectedEntry!!.value else ""
    }

    private fun onSelectionChange(selection: TalkativeList.TalkativeListEntry?) {
        selectedEntry = selection as StringSelectionList.StringEntry?
        confirmButton.active = true
    }

}