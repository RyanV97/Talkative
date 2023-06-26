package dev.cryptcraft.talkative.client.gui.editor.branch

import net.minecraft.client.gui.components.Button
import net.minecraft.network.chat.Component
import dev.cryptcraft.talkative.client.gui.editor.MainEditorScreen
import dev.cryptcraft.talkative.client.gui.widgets.lists.StringSelectionList
import dev.cryptcraft.talkative.common.network.serverbound.RequestBranchForEditPacket
import dev.cryptcraft.talkative.common.network.serverbound.UpdateBranchPacket

class EditBranchesScreen(parentScreen: MainEditorScreen) : BranchDirectoryScreen(parentScreen) {
    private lateinit var doneButton: Button
    private lateinit var editButton: Button
    private lateinit var deleteButton: Button

    override fun init() {
        super.init()

        doneButton = addRenderableWidget(Button(width - 50, height - 20, 50, 20, Component.literal("Done")) {
            onClose()
        })

        editButton = addRenderableWidget(Button(width - 50, height - 40, 50, 20, Component.literal("Edit")) {
            RequestBranchForEditPacket(list.selectedEntry!!.value).sendToServer()
        })
        editButton.active = false

        deleteButton = addRenderableWidget(Button(width - 50, height - 60, 50, 20, Component.literal("Delete")) {
            UpdateBranchPacket(list.selectedEntry!!.value, UpdateBranchPacket.UpdateAction.DELETE).sendToServer()
            onSelectionChange(null)
        })
        deleteButton.active = false
    }

    override fun onSelectionChange(selection: StringSelectionList.StringEntry?) {
        editButton.active = selection != null
        deleteButton.active = selection != null
    }

}