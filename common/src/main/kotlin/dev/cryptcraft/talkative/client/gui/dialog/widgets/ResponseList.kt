package dev.cryptcraft.talkative.client.gui.dialog.widgets

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import dev.cryptcraft.talkative.client.ScissorUtil
import dev.cryptcraft.talkative.client.gui.Animatable
import dev.cryptcraft.talkative.client.gui.GuiConstants
import dev.cryptcraft.talkative.client.gui.dialog.DialogScreen
import dev.cryptcraft.talkative.client.gui.widgets.lists.WidgetList
import dev.cryptcraft.talkative.common.network.clientbound.DialogPacket
import dev.cryptcraft.talkative.common.network.serverbound.DialogResponsePacket
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.network.chat.Component

class ResponseList(parent: DialogScreen, x: Int, y: Int, width: Int, height: Int) : WidgetList<DialogScreen>(parent, x, y, width, height), Animatable {
    private var animationDuration = 1.75f
    private var animationProgress = 0f

    init {
        renderBackground = false
        renderEntryBackground = false
    }

    fun addEntry(responseData: DialogPacket.ResponseData) {
        animationProgress = 0f

        val numLabel = "${children.size + 1}. "
        val xOffset = Minecraft.getInstance().font.width(numLabel)
        var entryHeight = 0
        for (line in responseData.contents)
            entryHeight += Minecraft.getInstance().font.wordWrapHeight(line, width - xOffset)
        addChild(ResponseListEntry(this, responseData, width, entryHeight + 5))
    }

    override fun renderButton(poseStack: PoseStack, mouseX: Int, mouseY: Int, partialTicks: Float) {
        animate(partialTicks / 20)

        if (renderBackground)
            fill(poseStack, x, y, x + width, y + height, 0x66000000)
        if (totalHeight > height)
            renderScrollBar(poseStack)

        ScissorUtil.start(x, y, width, height)
        var i = 0
        val size: Int = children.size
        while (i < size) {
            val child: ResponseListEntry = children[i] as ResponseListEntry
            if (isWidgetWithin(child)) {
                val itemDuration = animationDuration / size
                val prog = ((animationProgress - (i * (itemDuration / (size + 1)))) / (itemDuration * (i + 1))).coerceIn(0.01f, 1f)
                child.animate(if (animationProgress >= animationDuration) 1f else 1f - Math.pow((1 - prog).toDouble(), 5.0).toFloat())
                child.render(poseStack, mouseX, mouseY, partialTicks)
            }
            i++
        }
        ScissorUtil.stop()
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, mouseButton: Int): Boolean {
        if (animationProgress < animationDuration) {
            animationProgress = animationDuration
            return false
        }
        else
            return super.mouseClicked(mouseX, mouseY, mouseButton)
    }

    override fun renderScrollBar(poseStack: PoseStack?) {
        val minX = if (scrollBarLeft) x - scrollBarWidth else x + width
        val barHeight = scrollBarHeight
        val barY = scrollBarY
        RenderSystem.setShader(GameRenderer::getPositionTexShader)
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha)
        RenderSystem.setShaderTexture(0, GuiConstants.DIALOG_SCROLL)

        blit(poseStack, minX, barY, scrollBarWidth, 5, 0f, 0f, 9, 5, 9, 21)
        blit(poseStack, minX, barY + 5, scrollBarWidth, scrollBarHeight  - 10, 0f, 5f, 9, 11, 9, 21)
        blit(poseStack, minX, barY + barHeight - 5, scrollBarWidth, 5, 0f, 16f, 9, 5, 9, 21)
    }

    override fun animate(delta: Float) {
        if (isAnimating() && !parent.dialogList.isAnimating())
            animationProgress += delta

        if (animationProgress >= animationDuration)
            onFinishAnimating()
    }

    override fun isAnimating(): Boolean {
        return animationProgress < animationDuration
    }

    override fun getAnimationProgress(): Float {
        return (animationProgress / animationDuration).coerceAtMost(1f)
    }

    override fun onFinishAnimating() {
        for (child in children) {
            (child as Animatable).onFinishAnimating()
        }
    }

    class ResponseListEntry(private val parentList: ResponseList, private val responseData: DialogPacket.ResponseData, width: Int, height: Int, val font: Font = Minecraft.getInstance().font) : AbstractWidget(0, 0, width, height, Component.empty()), Animatable {
        private var animationProgress = 0f

        override fun renderButton(poseStack: PoseStack, mouseX: Int, mouseY: Int, delta: Float) {
            val numLabel = "${parentList.children.indexOf(this) + 1}. "
            val xOffset = font.width(numLabel)
            val col = getColour()

            if (animationProgress != 0f) {
                font.draw(poseStack, numLabel, x.toFloat(), y.toFloat(), col)
                for (line in responseData.contents) {
                    val left = x + (xOffset * animationProgress) - ((1f - animationProgress) * font.width(line))
                    for (subLine in font.split(line, width - xOffset))
                        font.draw(poseStack, subLine, left, y.toFloat(), col)
                }
            }
        }

        override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
            if (!active || !visible || !isValidClickButton(button) || !clicked(mouseX, mouseY))
                return false

            onResponse()
            return true
        }

        fun onResponse() {
            if (animationProgress < 1f) return

            DialogResponsePacket(responseData.responseId).sendToServer()
            if (responseData.type == DialogPacket.ResponseData.Type.Exit)
                parentList.parent.onClose()
            else
                parentList.parent.dialogList.addEntry(responseData.contents, false)
        }

        fun getColour(): Int {
            if (animationProgress < 1f) {
                val col = 0xFFFFFF
                val red = (col shr 16 and 0xFF).toFloat()
                val green = (col shr 8 and 0xFF).toFloat()
                val blue = (col and 0xFF).toFloat()
                return ((animationProgress * 0x9F).toInt() shl 24) or
                        ((red.toInt() and 0xFF) shl 16) or
                        ((green.toInt() and 0xFF) shl 8) or
                        (blue.toInt() and 0xFF)
            }
            else if (isHoveredOrFocused)
                return 0xFFFFFF
            else
                return 0x9FFFFFFF.toInt()
        }

        override fun animate(delta: Float) {
            animationProgress = delta
        }

        override fun isAnimating(): Boolean {
            return animationProgress < 1f
        }

        override fun getAnimationProgress(): Float {
            return animationProgress
        }

        override fun onFinishAnimating() {
            animationProgress = 1f
        }

        override fun updateNarration(narrationElementOutput: NarrationElementOutput) {}
    }
}