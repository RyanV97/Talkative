package dev.cryptcraft.talkative.client.gui.editor.branch

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.EditBox
import net.minecraft.network.chat.Component
import org.apache.commons.compress.utils.Lists
import dev.cryptcraft.talkative.client.gui.TalkativeScreen
import dev.cryptcraft.talkative.client.gui.widgets.NestedWidget
import dev.cryptcraft.talkative.client.gui.widgets.lists.WidgetList
import dev.cryptcraft.talkative.common.data.tree.DialogNode

class CommandEditorScreen(parentScreen: BranchNodeEditorScreen, private val node: DialogNode) : TalkativeScreen(parentScreen, Component.literal("Commands")) {
    lateinit var list: WidgetList<CommandEditorScreen>
    private val removalList = Lists.newArrayList<CommandEntry>()

    override fun init() {
        list = addRenderableWidget(WidgetList(this, 0, 0, width / 2, height - 20))

        //Add Command Button
        addRenderableWidget(Button(width - 15, 0, 15, 15, Component.literal("+")) {
            list.addChild(CommandEntry(width / 2, 20, this))
            list.recalculateChildren()
        })

        //Save Button
        addRenderableWidget(Button(width - 50, 15, 50, 20, Component.literal("Save")) {
            val commands = Lists.newArrayList<String>()
            list.children.forEach { commands.add((it as CommandEntry).commandBox.value) }
            node.commands = commands
            onClose()
        })

        node.commands?.forEach {
            val entry = CommandEntry(width / 2, 20, this)
            entry.commandBox.value = it
            list.addChild(entry)
        }

        list.width = width / 2
        list.height = height - 20
        list.recalculateChildren()
    }

    private fun addForRemoval(commandEntry: CommandEntry) {
        this.removalList.add(commandEntry)
    }

    override fun tick() {
        this.removalList.forEach { list.remove(it) }
        this.removalList.clear()
    }

    class CommandEntry(width: Int, height: Int, private val parentPopup: CommandEditorScreen) : NestedWidget(0,0, width, height, Component.empty()) {
        val commandBox = addChild(EditBox(Minecraft.getInstance().font, 0, 0, width - 25, height - 2, Component.empty()))
        val deleteButton = addChild(Button(0, 0, 20, 20, Component.literal("X")) {
            parentPopup.addForRemoval(this)
        })

        override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
            if (commandBox.isFocused) {
                return commandBox.keyPressed(keyCode, scanCode, modifiers)
            }
            return false
        }

        override fun recalculateChildren() {
            commandBox.x = x + 1
            commandBox.y = y + 1
            deleteButton.x = x + width - 20
            deleteButton.y = y

            super.recalculateChildren()
        }
    }
}