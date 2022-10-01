package ryanv.talkative.client.gui

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.screens.Screen
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.StringTag
import net.minecraft.network.chat.TextComponent
import ryanv.talkative.client.gui.widgets.NestedWidget
import ryanv.talkative.client.gui.widgets.lists.DialogList
import ryanv.talkative.common.data.tree.DialogNode

class DialogScreen: Screen(TextComponent("NPC Dialog")) {

    private var dialogEntryList: DialogList? = null
    private val pendingTasks = ArrayList<Runnable>()

    override fun init() {
        val mid = width / 2
        val listWidth = width - (width / 2)
        dialogEntryList = DialogList(this, mid - (listWidth / 2), 0, listWidth, height, height)
        dialogEntryList!!.renderBackground = false
        dialogEntryList!!.renderEntryBackground = false

        addButton(dialogEntryList)
    }

    override fun render(poseStack: PoseStack?, mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground(poseStack)
        super.render(poseStack, mouseX, mouseY, delta)
    }

    override fun renderBackground(poseStack: PoseStack?) {
        fill(poseStack, 0, 0, width, height, 0x66000000)
    }

    fun loadDialog(node: DialogNode, responses: Array<String>?) {
        //ToDo: Add Speaker to DialogNode
        val speaker = TextComponent("Speaker")
        dialogEntryList?.addChild(DialogEntry(TextComponent(node.content), speaker, this, true))
        if(!responses.isNullOrEmpty()) {
            dialogEntryList?.addChild(ResponsesWidget(this, responses))
        }
    }

    fun onResponse(response: String) {
        dialogEntryList?.remove(dialogEntryList!!.getSize() - 1)
        dialogEntryList?.addChild(DialogEntry(TextComponent(response), TextComponent("Player"), this, false))
    }

    override fun tick() {
        if(pendingTasks.isNotEmpty()) {
            for(task in pendingTasks) {
                task.run()
            }
            pendingTasks.clear()
        }
    }

    class DialogEntry(val contents: TextComponent, val speaker: TextComponent, val parentScreen: DialogScreen, val speakerOnRight: Boolean = true): NestedWidget(0, 0, 0, 0, null) {
        override fun renderButton(poseStack: PoseStack, mouseX: Int, mouseY: Int, partialTicks: Float) {
            fill(poseStack, x, y, x + width, y + height - 9, -267386864)

            hLine(poseStack, x, x + width - 1, y, 0x505000FF)
            hLine(poseStack, x, x + width - 1, y + height - 10, 0x505000FF)

            vLine(poseStack, x, y, y + height - 10, 0x505000FF)
            vLine(poseStack, x + width - 1, y, y + height - 10, 0x505000FF)

            parentScreen.font.drawWordWrap(contents, x + 5, y + 6, width - 5, 0xFFFFFF)
            parentScreen.renderTooltip(poseStack, speaker, if (speakerOnRight) x + width - parentScreen.font.width(speaker) - 16 else x - 8, y + height - 3)
        }

        override fun setWidth(width: Int) {
            super.setWidth(width)
            setHeight(parentScreen.font.wordWrapHeight(contents.contents, width - 5) + 25)
        }
    }

    class ResponsesWidget(val parent: DialogScreen, responses: Array<String>): NestedWidget(0, 0, 0, 10 + responses.size * 21, null) {
        init {
            for(response in responses) {
                addChild(Button((width / 2) - 50, height - 30, 150, 20, TextComponent(response)) {
                    parent.pendingTasks.add {
                        parent.onResponse(response)
                    }
                })
            }
        }

        override fun recalculateChildren() {
            for(i in 0 until children.size) {
                val child = children[i]
                child.x = x + (width / 2) - 75
                child.y = y + 5 + (i * 21)
            }
        }
    }
}