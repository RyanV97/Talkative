package dev.cryptcraft.talkative.client.gui.editor.branch

import net.minecraft.client.gui.components.Button
import net.minecraft.network.chat.Component
import dev.cryptcraft.talkative.client.gui.editor.MainEditorScreen
import dev.cryptcraft.talkative.client.gui.widgets.lists.StringSelectionList

class AttachBranchScreen(parentScreen: MainEditorScreen, private var onConfirm: (selection: StringSelectionList.StringEntry?) -> Unit) : BranchDirectoryScreen(parentScreen) {
    private lateinit var confirmButton: Button

    override fun init() {
        super.init()

        confirmButton = addRenderableWidget(Button(width - 50, height - 20, 50, 20, Component.literal("Confirm")) {
            onConfirm(list.selectedEntry)
            onClose()
        })
        confirmButton.active = false
    }

    override fun onSelectionChange(selection: StringSelectionList.StringEntry?) {
        confirmButton.active = true
    }
}