package dev.cryptcraft.talkative.client.gui.editor.branch

import dev.cryptcraft.talkative.api.tree.node.NodeBase
import dev.cryptcraft.talkative.client.gui.GuiConstants
import dev.cryptcraft.talkative.client.gui.TalkativeScreen
import dev.cryptcraft.talkative.client.gui.widgets.IconButton
import dev.cryptcraft.talkative.client.gui.widgets.NestedWidget
import dev.cryptcraft.talkative.client.gui.widgets.lists.WidgetList
import dev.cryptcraft.talkative.client.gui.widgets.popup.PopupWidget
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.EditBox
import net.minecraft.network.chat.Component

class CommandEditorPopup(parent: TalkativeScreen, val node: NodeBase, x: Int, y: Int, width: Int, height: Int) : PopupWidget(x, y, width, height, parent) {
    private var entryList: WidgetList<*> = addChild(WidgetList(parent, x + 5, y + 20, width - 10, height - 25))
    private val removalList = ArrayList<CommandWidget>()

    init {
        entryList.renderBackground = false

        label((width / 2) - (Minecraft.getInstance().font.width("Commands") / 2), 9, "Commands")

        button(width - 28, 5, "+", 16, 16) {
            entryList.addChild(CommandWidget(width - 10, this))
        }

        node.commands?.forEach {
            val entry = CommandWidget(width - 10, this)
            entry.commandEntry.value = it
            entryList.addChild(entry)
        }
    }

    fun saveCommands() {
        val newCommands = ArrayList<String>()
        entryList.children.forEach {
            val entry = it as CommandWidget
             newCommands.add(entry.commandEntry.value)
        }
        node.commands = newCommands
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (keyCode == 256) {
            parent.closePopup()
            return true
        }
        return entryList.keyPressed(keyCode, scanCode, modifiers)
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, delta: Double): Boolean {
        entryList.mouseScrolled(mouseX, mouseY, delta)
        return true
    }

    override fun onClose() {
        saveCommands()
    }

    override fun tick() {
        removalList.forEach(entryList::remove)
    }

    override fun recalculateChildren() {
        entryList.x = x
        entryList.y = y + 20
        entryList.width = width - 10
        entryList.height = height - 20
    }

    class CommandWidget(width: Int, parentPopup: CommandEditorPopup) : NestedWidget(parentPopup.x + 5, parentPopup.y + 5, width, 30, Component.empty()) {
        val commandEntry = addChild(EditBox(Minecraft.getInstance().font, x + 5, y + 5, width - 35, 20, Component.empty()))
        private val deleteButton = addChild(IconButton(x + width - 25, y + 5, 20, 20, GuiConstants.DELETE_ICON) {
            parentPopup.removalList.add(this)
        })

        override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
            return commandEntry.keyPressed(keyCode, scanCode, modifiers)
        }

        override fun recalculateChildren() {
            commandEntry.x = x + 5
            commandEntry.y = y + 5
            commandEntry.width = width - 35

            deleteButton.x = x + width - 25
            deleteButton.y = y + 5
        }
    }
}