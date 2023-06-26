package dev.cryptcraft.talkative.client.gui.dialog.widgets

import com.mojang.blaze3d.vertex.PoseStack
import it.unimi.dsi.fastutil.ints.Int2ReferenceLinkedOpenHashMap
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiComponent
import net.minecraft.client.gui.components.AbstractButton
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import dev.cryptcraft.talkative.Talkative
import dev.cryptcraft.talkative.client.gui.dialog.DialogScreen
import dev.cryptcraft.talkative.client.gui.widgets.lists.WidgetList

class ResponsesWidget(parent: DialogScreen, x: Int, y: Int, width: Int, height: Int) :
    WidgetList<DialogScreen>(parent, x, y, width, height, null) {

    private val font: Font = Minecraft.getInstance().font
    var currentState: State = State.FINISH

    companion object {
        val TEX: ResourceLocation = ResourceLocation(dev.cryptcraft.talkative.Talkative.MOD_ID, "textures/gui/dialog.png")
    }

    init {
        renderBackground = false
        renderEntryBackground = false
    }

    fun repopulateResponses(responses: Int2ReferenceLinkedOpenHashMap<Component>) {
        clear()

        //ToDo: These don't seem to be in order
        responses.forEach {
            addChild(ResponseButton(it.key, (width / 2) - 50, height - 30, 150, 20, it.value, parent, font))
        }

        currentState = State.RESPOND
        recalculateChildren()
    }

    override fun recalculateChildren() {
        when (currentState) {
            State.RESPOND -> {
                totalHeight = 0
                for (i in 0 until children.size) {
                    totalHeight += calculateChild(children[i] as ResponseButton) + 5
                }
            }

            State.CONTINUE -> {
                height = 7
                val btn = children.get(0) as ContinueButton
                btn.x = x + (width / 2)
                btn.y = y
            }

            State.FINISH -> {
                val btn = children.get(0) as ExitButton
                btn.x = x + width / 2
                btn.y = y + 5
                btn.width = width / 2
            }
        }
    }

    override fun updateNarration(narrationElementOutput: NarrationElementOutput) {
    }

    private fun calculateChild(child: ResponseButton): Int {
        child.x = x
        child.y = y + totalHeight
        child.width = (font.width(child.contents.string) + 10).coerceAtMost(width)
        child.height = 8 + font.wordWrapHeight(child.contents.string, width - 10)
        return child.height
    }

    fun clearResponsesAndContinue() {
        clear()
        addChild(ContinueButton(width / 2, 0, parent))
        currentState = State.CONTINUE
    }

    fun clearResponsesAndFinish() {
        clear()
        addChild(ExitButton(x, y, width, 20, this))
        currentState = State.FINISH
    }

    class ResponseButton
        (
        val index: Int,
        x: Int,
        y: Int,
        width: Int,
        height: Int,
        val contents: Component,
        val parent: DialogScreen,
        val font: Font
    ) : AbstractButton(x, y, width, height, contents) {

        override fun renderButton(poseStack: PoseStack, mouseX: Int, mouseY: Int, partialTicks: Float) {
            val color: Int = if (isHoveredOrFocused) 0xFF0096FF.toInt() else 0x960050FF.toInt()

            fill(poseStack, x, y, x + width, y + height, -267386864)

            hLine(poseStack, x, x + width - 1, y, color)
            hLine(poseStack, x, x + width - 1, y + height, color)

            vLine(poseStack, x, y, y + height, color)
            vLine(poseStack, x + width - 1, y, y + height, color)

            GuiComponent.drawString(poseStack, font, contents, x + 5, y + 5, 0xFFFFFF)
        }

        fun setHeight(height: Int) {
            this.height = height
        }

        override fun onPress() {
            parent.onResponse(index, contents)
        }

        override fun updateNarration(narrationElementOutput: NarrationElementOutput) {
        }
    }

    class ContinueButton(x: Int, y: Int, val parent: DialogScreen) : AbstractButton(x, y, 20, 20, Component.empty()) {
        override fun renderButton(poseStack: PoseStack?, mouseX: Int, mouseY: Int, partialTicks: Float) {
            Minecraft.getInstance().textureManager.bindForSetup(TEX)
            blit(poseStack, x, y, 0, 0, 11, 7)
        }

        override fun onPress() {
            parent.onContinue()
        }

        override fun updateNarration(narrationElementOutput: NarrationElementOutput) {
        }
    }

    class ExitButton(x: Int, y: Int, width: Int, height: Int, val parent: ResponsesWidget): AbstractButton(x, y, width, height, Component.literal("End Conversation")) {
        override fun onPress() {
            parent.parent.onClose()
        }

        override fun updateNarration(narrationElementOutput: NarrationElementOutput) {
        }
    }

    enum class State {
        RESPOND, CONTINUE, FINISH
    }
}