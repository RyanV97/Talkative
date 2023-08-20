package dev.cryptcraft.talkative.client.gui.dialog.widgets

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import dev.cryptcraft.talkative.client.gui.GuiConstants
import dev.cryptcraft.talkative.client.gui.widgets.lists.WidgetList
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiComponent
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.network.chat.Component

class DialogList(parent: Screen, x: Int, width: Int, var maxHeight: Int, private var bottom: Int) : WidgetList<Screen>(parent, x, bottom, width, 0) {
    init {
        renderBackground = false
        renderEntryBackground = false
    }

    //ToDo Change Scrollbar style n stuffs

    fun setBottom(newBottom: Int) {
        bottom = newBottom
        setY(bottom - height)
    }

    fun addEntry(dialogLine: Component) {
        //ToDo Add Speaker to packet
        addChild(DialogListEntry(Component.literal("Speaker"), dialogLine, width))
    }

    override fun addChild(widget: AbstractWidget) {
        super.addChild(widget)
        height = if (totalHeight < maxHeight) totalHeight else maxHeight
        setY(if (height < maxHeight) bottom - height else 0)
        scrollPos = maxScroll
    }

    override fun remove(widget: AbstractWidget): Boolean {
        if (super.remove(widget)) {
            val i = parent.height - bottom
            val maxHeight = parent.height - i
            height = if (totalHeight < maxHeight) totalHeight else maxHeight
            setY(if (height < maxHeight) bottom - height else 0)
            return true
        }
        return false
    }

    override fun updateNarration(narrationElementOutput: NarrationElementOutput) {
    }

    class DialogListEntry(private val speaker: Component, private val contents: Component, width: Int, val font: Font = Minecraft.getInstance().font)
    : AbstractWidget(0, 0, width, calculateHeight(font, contents, width), Component.empty()) {

        override fun renderButton(poseStack: PoseStack, mouseX: Int, mouseY: Int, partialTick: Float) {
            val top = y + 16
            RenderSystem.setShader(GameRenderer::getPositionTexShader)
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha)

            drawMessageBackground(poseStack, top)
            drawSpeakerLabel(poseStack, x + 6, top - 13)

            font.drawWordWrap(contents, x + 11, top + 9, width - 16, 0xFFFFFF)
        }

        private fun drawMessageBackground(poseStack: PoseStack, top: Int) {
            val bottom = y + height - 16
            RenderSystem.setShaderTexture(0, GuiConstants.DIALOG_MESSAGE)

            drawMessageTexture(poseStack, x, top, 16, 16, 0, 0) //TopLeft
            drawMessageTexture(poseStack, x + width - 16, top, 16, 16, 32, 0) //TopRight
            drawMessageTexture(poseStack, x, bottom, 16, 16, 0, 32) //BottomLeft
            drawMessageTexture(poseStack, x + width - 16, bottom, 16, 16, 32, 32) //BottomRight

            drawMessageTexture(poseStack, x + 16, top, width - 32, 16, 16, 0) //Top
            drawMessageTexture(poseStack, x + 16, bottom, width - 32, 16, 16, 32) //Bottom

            if (height > 32) {
                drawMessageTexture(poseStack, x, top + 16, 16, height - 48, 0, 16) //Left Side
                drawMessageTexture(poseStack, x + width - 16, top + 16, 16, height - 48, 32, 16)//Right Side
                drawMessageTexture(poseStack, x + 16, top + 16, width - 32, height - 48, 16, 16) //Background Fill
            }
        }

        private fun drawSpeakerLabel(poseStack: PoseStack, left: Int, top: Int) {
            val textWidth = font.width(speaker)
            RenderSystem.setShaderTexture(0, GuiConstants.DIALOG_MESSAGE_SPEAKER)

            drawSpeakerTexture(poseStack, left, top, 8, 8, 0, 0) //TopLeft
            drawSpeakerTexture(poseStack, left + textWidth + 8, top, 8, 8, 16, 0) //TopRight
            drawSpeakerTexture(poseStack, left, top + 8, 8, 8, 0, 16) //BottomLeft
            drawSpeakerTexture(poseStack, left + textWidth + 8, top + 8, 8, 8, 16, 16) //BottomRight

            drawSpeakerTexture(poseStack, left + 8, top, textWidth, 8, 8, 0) //Top
            drawSpeakerTexture(poseStack, left + 8, top + 8, textWidth, 8, 8, 16) //Bottom

            font.draw(poseStack, speaker, left + 8f, top + 4f, 0xFFFFFF)
        }

        override fun setWidth(width: Int) {
            super.setWidth(width)
            height = calculateHeight(font, contents, width)
        }

        override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
            return false
        }

        override fun updateNarration(narrationElementOutput: NarrationElementOutput) {}

        companion object {
            fun drawMessageTexture(poseStack: PoseStack, x: Int, y: Int, width: Int, height: Int, u: Int, v: Int) {
                GuiComponent.blit(poseStack, x, y, width, height, u.toFloat(), v.toFloat(), 16, 16, 48, 48)
            }

            fun drawSpeakerTexture(poseStack: PoseStack, x: Int, y: Int, width: Int, height: Int, u: Int, v: Int) {
                GuiComponent.blit(poseStack, x, y, width, height, u.toFloat(), v.toFloat(), 8, 8, 24, 24)
            }

            fun calculateHeight(font: Font, contents: Component, width: Int): Int {
                val textHeight = font.wordWrapHeight(contents, width - 16)
                return textHeight + 32
            }
        }
    }
}