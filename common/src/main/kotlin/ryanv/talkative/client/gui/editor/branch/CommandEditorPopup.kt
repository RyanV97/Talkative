package ryanv.talkative.client.gui.editor.branch

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.EditBox
import net.minecraft.client.gui.components.Widget
import net.minecraft.network.chat.Component
import org.apache.commons.compress.utils.Lists
import ryanv.talkative.client.gui.widgets.NestedWidget
import ryanv.talkative.client.gui.widgets.popup.PopupWidget
import ryanv.talkative.common.data.tree.DialogNode

class CommandEditorPopup(x: Int, y: Int, width: Int, height: Int, parentScreen: BranchNodeEditorScreen, val node: DialogNode) : PopupWidget(x, y, width, height, parentScreen, "Commands") {
    val list = list(x, y + 10, width, height - 30)
    private val removalList = Lists.newArrayList<CommandEntry>()

    init {
        button(x + width - 15, y, "+", 15, 15) {
            list.addChild(CommandEntry(width, 20, this))
        }
        button(x, y + height - 20, "Save") {
            val commands = Lists.newArrayList<String>()
            list.children.forEach { commands.add((it as CommandEntry).commandBox.value) }
        }

        node.commands?.forEach {
            val entry = CommandEntry(width, 20, this)
            entry.commandBox.value = it
            list.addChild(entry)
        }
    }

    private fun addForRemoval(commandEntry: CommandEditorPopup.CommandEntry) {
        this.removalList.add(commandEntry)
    }

    override fun tick() {
        this.removalList.forEach { list.remove(it) }
        this.removalList.clear()
    }

    class CommandEntry(width: Int, height: Int, private val parentPopup: CommandEditorPopup) : NestedWidget(0,0, width, height, Component.empty()) {
        val commandBox = addChild(EditBox(Minecraft.getInstance().font, x, y, width - 25, height, Component.empty()))

        init {
            addChild(Button(x + width - 20, y, 20, 20, Component.literal("X")) {
                parentPopup.addForRemoval(this)
            })
        }
    }
}