package ryanv.talkative.client.gui.dialog

import com.mojang.blaze3d.vertex.PoseStack
import it.unimi.dsi.fastutil.ints.Int2ReferenceOpenHashMap
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.TextComponent
import ryanv.talkative.client.gui.dialog.widgets.DialogList
import ryanv.talkative.client.gui.dialog.widgets.ResponsesWidget
import ryanv.talkative.client.gui.widgets.NestedWidget
import ryanv.talkative.common.data.Response
import ryanv.talkative.common.network.serverbound.DialogResponsePacket
import ryanv.talkative.common.network.serverbound.FinishConversationPacket

class DialogScreen : Screen(TextComponent("NPC Dialog")) {
    private val pendingTasks = ArrayList<Runnable>()
    private var dialogEntryList: DialogList? = null
    private var currentDialogWidget: CurrentDialogWidget? = null
    private var responsesWidget: ResponsesWidget? = null

    override fun init() {
        super.init()
        val listWidth = width / 2

        dialogEntryList = addButton(DialogList(this, listWidth - (listWidth / 2), 0, listWidth, height - 50, height - 50))
        responsesWidget = addButton(ResponsesWidget(this, listWidth - (listWidth / 2), height - 50, listWidth, 50))
    }

    override fun render(poseStack: PoseStack?, mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground(poseStack)
        currentDialogWidget?.render(poseStack, mouseX, mouseY, delta)
        super.render(poseStack, mouseX, mouseY, delta)
    }

    override fun renderBackground(poseStack: PoseStack?) {
        fill(poseStack, 0, 0, width, height, 0x66000000)
    }

    fun loadDialog(dialogLine: Component, responses: Int2ReferenceOpenHashMap<Component>?, exitNode: Boolean) {
        //ToDo: Add Speaker to DialogNode
        val speaker = TextComponent("Speaker")

        if (dialogLine != TextComponent.EMPTY) {
            val listWidth = width / 2
            val contentHeight = font.wordWrapHeight(dialogLine.contents, listWidth - 10) + 20

            dialogEntryList!!.setBottom(responsesWidget!!.y - contentHeight)
            currentDialogWidget = CurrentDialogWidget(
                listWidth - (listWidth / 2),
                height - 50 - contentHeight,
                listWidth,
                contentHeight,
                dialogLine,
                speaker,
                this
            )
        }
        else
            dialogEntryList!!.setBottom(responsesWidget!!.y)

        if (!responses!!.isEmpty())
            responsesWidget?.repopulateResponses(responses)
        else if (!exitNode)
            responsesWidget?.clearResponsesAndContinue()
        else
            responsesWidget?.clearResponsesAndFinish()
    }

    fun onResponse(index: Int, responseContents: Component) {
        addTask {
            currentDialogWidget?.let {
                dialogEntryList?.addChild(DialogEntry(it.contents, it.speaker, this))
                currentDialogWidget = null
                dialogEntryList!!.setBottom(responsesWidget!!.y)
            }
            responsesWidget?.clear()
            dialogEntryList?.addChild(DialogEntry(responseContents, TextComponent("Player"), this, false))
            DialogResponsePacket(index).sendToServer()
        }
    }

    fun onContinue() {
        currentDialogWidget?.let { dialogEntryList?.addChild(DialogEntry(it.contents, it.speaker, this)) }
        DialogResponsePacket(-1).sendToServer()
    }

    override fun onClose() {
        FinishConversationPacket().sendToServer()
        super.onClose()
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if(responsesWidget?.currentState == ResponsesWidget.State.CONTINUE) {
            responsesWidget!!.children.get(0).onClick(mouseX, mouseY)
            return true
        }

        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun tick() {
        if (pendingTasks.isNotEmpty()) {
            for (task in pendingTasks) {
                task.run()
            }
            pendingTasks.clear()
        }
    }

    fun addTask(task: Runnable) {
        pendingTasks.add(task)
    }

    open class DialogEntry(val contents: Component, val speaker: Component, private val parentScreen: DialogScreen, private val speakerOnRight: Boolean = true): NestedWidget(0, 0, 0, 0, null) {
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

    class CurrentDialogWidget(x: Int, y: Int, width: Int, height: Int, contents: Component, speaker: Component, parentScreen: DialogScreen): DialogEntry(contents, speaker, parentScreen) {
        val animationFinished: Boolean = false

        init {
            this.setX(x)
            this.setY(y)
            this.setWidth(width)
        }
    }
}