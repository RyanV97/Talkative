package ryanv.talkative.client.gui

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.TextComponent
import ryanv.talkative.client.gui.widgets.NestedWidget
import ryanv.talkative.client.gui.widgets.lists.DialogList

class DialogScreen: Screen(TextComponent("NPC Dialog")) {

    private var dialogEntryList = DialogList(this, 0, 0, 0, 0)
    private val pendingTasks = ArrayList<Runnable>()

    override fun init() {
        val mid = width / 2
        val listWidth = width - (width / 2)
        dialogEntryList.clear()

        dialogEntryList.setX(mid - (listWidth / 2))
        dialogEntryList.bottom = height
        dialogEntryList.width = listWidth

        dialogEntryList.renderBackground = false
        dialogEntryList.renderEntryBackground = false

        addButton(dialogEntryList)

        dialogEntryList.addChild(DialogEntry(TextComponent("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat."), TextComponent("Speaker"), this))
        addResponses()
    }

    override fun render(poseStack: PoseStack?, mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground(poseStack)
        super.render(poseStack, mouseX, mouseY, delta)
    }

    override fun renderBackground(poseStack: PoseStack?) {
        fill(poseStack, 0, 0, width, height, 0x66000000)
    }

    fun addResponses() {
        dialogEntryList.addChild(ResponsesWidget(this, arrayOf("Test Response", "Another Response", "More Responses")))
    }

    fun onResponse(response: String) {
        dialogEntryList.remove(dialogEntryList.getSize() - 1)
        dialogEntryList.addChild(DialogEntry(TextComponent(response), TextComponent("Player"), this, false))
        dialogEntryList.addChild(DialogEntry(TextComponent("Some more dialog text"), TextComponent("Speaker"), this))
        //if next nodes are responses
        addResponses()
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
            fill(poseStack, x, y, x + width, y + height, -267386864)

            hLine(poseStack, x, x + width - 1, y, 0x505000FF)
            hLine(poseStack, x, x + width - 1, y + height - 1, 0x505000FF)

            vLine(poseStack, x, y, y + height - 1, 0x505000FF)
            vLine(poseStack, x + width - 1, y, y + height, 0x505000FF)

            parentScreen.font.drawWordWrap(contents, x + 5, y + 6, width - 5, 0xFFFFFF)
            parentScreen.renderTooltip(poseStack, speaker, if (speakerOnRight) x + width - parentScreen.font.width(speaker) - 16 else x - 8, y + height + 12)
        }

        override fun setWidth(width: Int) {
            super.setWidth(width)
            setHeight(parentScreen.font.wordWrapHeight(contents.contents, width - 5))
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