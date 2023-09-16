package dev.cryptcraft.talkative.client.gui.dialog.widgets

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.*
import dev.cryptcraft.talkative.api.actor.DisplayData
import dev.cryptcraft.talkative.client.ScissorUtil
import dev.cryptcraft.talkative.client.gui.Animatable
import dev.cryptcraft.talkative.client.gui.GuiConstants
import dev.cryptcraft.talkative.client.gui.dialog.DialogScreen
import dev.cryptcraft.talkative.client.gui.widgets.lists.WidgetList
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.network.chat.Component
import net.minecraft.util.FormattedCharSequence

class DialogList(parent: DialogScreen, x: Int, width: Int, var maxHeight: Int, private var bottom: Int) : WidgetList<Screen>(parent, x, bottom, width, 0), Animatable {
    private val addQueue = ArrayList<DialogListEntry>()
    private var currentAnimatingIndex = -1
    private var targetY = y
    private var targetScroll = maxScroll

    fun setBottom(newBottom: Int) {
        bottom = newBottom
        setY(bottom - height)
    }

    fun addEntry(dialogLines: List<Component>, actorSpeaking: Boolean = true) {
        val displayData = (parent as DialogScreen).displayData ?: DisplayData()
        val speaker =
            when (actorSpeaking) {
                true -> if (displayData.overrideDisplayName) Component.literal(displayData.displayName) else parent.actorEntity?.displayName
                    ?: Component.literal("Actor")
                false -> Minecraft.getInstance().player!!.displayName
            }

        val entry = DialogListEntry(this, speaker, dialogLines, width, actorSpeaking)

        if (children.isEmpty())
            addChild(entry)
        else
            addQueue.add(entry)
    }

    override fun addChild(widget: AbstractWidget) {
        super.addChild(widget)
        height = if (totalHeight < maxHeight) totalHeight else maxHeight
        targetY = if (height < maxHeight) bottom - height else 0
        targetScroll = maxScroll
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

    override fun mouseScrolled(mouseX: Double, mouseY: Double, delta: Double): Boolean {
        super.mouseScrolled(mouseX, mouseY, delta)
        targetScroll = scrollPos
        return true
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, mouseButton: Int): Boolean {
        if (isAnimating()) {
            for (i in currentAnimatingIndex until children.size) {
                if (i == -1) continue
                (children[i] as DialogListEntry).animationProgress = DialogListEntry.animationDuration
            }
            currentAnimatingIndex = -1
            return false
        }
        return super.mouseClicked(mouseX, mouseY, mouseButton)
    }

    override fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, dragX: Double, dragY: Double): Boolean {
        if (super.mouseDragged(mouseX, mouseY, button, dragX, dragY)) {
            targetScroll = scrollPos
            return true
        }
        return false
    }

    override fun renderButton(poseStack: PoseStack, mouseX: Int, mouseY: Int, partialTicks: Float) {
        animate(partialTicks / 20)
        if (totalHeight > height)
            renderScrollBar(poseStack)
        val offset = parent.width / 8
        ScissorUtil.start(offset, y, parent.width - offset, height)
        var i = 0
        val size: Int = children.size
        while (i < size) {
            val child: DialogListEntry = children[i] as DialogListEntry
            val startNewAnim = currentAnimatingIndex == -1 && child.animationProgress < 1f && y == targetY
            if (currentAnimatingIndex == i || startNewAnim) {
                if (startNewAnim) currentAnimatingIndex = i

                child.animationProgress += partialTicks / 20

                if (child.animationProgress >= 1f)
                    currentAnimatingIndex = if (i < size - 1) i + 1 else -1
            }

            if (isWidgetWithin(child)) {
                child.render(poseStack, mouseX, mouseY, partialTicks)
            }
            i++
        }
        ScissorUtil.stop()

        if (addQueue.isNotEmpty() && currentAnimatingIndex == -1 && y == targetY) {
            addChild(addQueue[0])
            addQueue.removeAt(0)
        }
    }

    override fun renderScrollBar(poseStack: PoseStack?) {
        val minX = if (scrollBarLeft) x - scrollBarWidth else x + width
        val maxX = minX + scrollBarWidth
        val minY = y
        val maxY = y + height
        fill(poseStack!!, minX, minY, maxX, maxY, 0x55000000)

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
        if (y != targetY) {
            val step = (250 * delta).toInt()
            setY(y + ((targetY - y).coerceIn(-step, step)))
        }
        if (scrollPos != targetScroll) {
            val step = (200 * delta).toInt()
            scrollPos += (targetScroll - scrollPos).coerceIn(-step, step)
        }
    }

    override fun isAnimating(): Boolean {
        return children.isNotEmpty() && currentAnimatingIndex != -1 || y != targetY
    }

    override fun getAnimationProgress(): Float {
        return 0f
    }

    override fun updateNarration(narrationElementOutput: NarrationElementOutput) {}

    class DialogListEntry(private val parentList: DialogList, private val speaker: Component, private val contentsIn: List<Component>, width: Int, val actorSpeaking: Boolean, val font: Font = Minecraft.getInstance().font) : AbstractWidget(0, 0, width, 0, Component.empty()) {
        private var speakerX = if (actorSpeaking) width - font.width(speaker) - 6 else 6
        private var fittedContents = setContents(contentsIn)

        var animationProgress = 0f

        init {
            height = (fittedContents.size * 9) + 32
        }

        private fun setContents(contents: List<Component>): List<FormattedCharSequence> {
            val list = ArrayList<FormattedCharSequence>()

            for (line in contents) {
                for (subLine in font.split(line, width - 17))
                    list.add(subLine)
            }
            return list
        }

        override fun renderButton(poseStack: PoseStack, mouseX: Int, mouseY: Int, partialTick: Float) {
            if (animationProgress == 0f) return

            val i = (animationProgress / animationDuration).coerceAtMost(1f)
            val prog = Math.pow((1 - i).toDouble(), 5.0).toFloat()
            var offset = width * prog
            if (!actorSpeaking)
                offset = -offset
            val left = parentList.x + offset
            val top = y + 16

            RenderSystem.setShader(GameRenderer::getPositionTexShader)
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha)

            poseStack.pushPose()
            poseStack.translate(0.0, 0.0, 100.0)
            drawMessageBackground(poseStack, left, top)
            drawSpeakerLabel(poseStack, left + speakerX, top - 12)

            for ((i, line) in fittedContents.withIndex()) {
                font.draw(poseStack, line, left + 11f, top + ((i + 1) * 9f), 0xFFFFFF)
            }
            poseStack.popPose()
        }

        private fun drawMessageBackground(poseStack: PoseStack, left: Float, top: Int) {
            val bottom = y + height - 16
            RenderSystem.setShaderTexture(0, GuiConstants.DIALOG_MESSAGE)

            drawMessageTexture(poseStack, left, top, 16, 16, 0, 0) //TopLeft
            drawMessageTexture(poseStack, left + width - 17, top, 16, 16, 32, 0) //TopRight
            drawMessageTexture(poseStack, left, bottom, 16, 16, 0, 32) //BottomLeft
            drawMessageTexture(poseStack, left + width - 17, bottom, 16, 16, 32, 32) //BottomRight

            drawMessageTexture(poseStack, left + 16, top, width - 32, 16, 16, 0) //Top
            drawMessageTexture(poseStack, left + 16, bottom, width - 32, 16, 16, 32) //Bottom

            if (height > 32) {
                drawMessageTexture(poseStack, left, top + 16, 16, height - 48, 0, 16) //Left Side
                drawMessageTexture(poseStack, left + width - 17, top + 16, 16, height - 48, 32, 16)//Right Side
                drawMessageTexture(poseStack, left + 16, top + 16, width - 32, height - 48, 16, 16) //Background Fill
            }
        }

        private fun drawSpeakerLabel(poseStack: PoseStack, left: Float, top: Int) {
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
            setContents(contentsIn)
            speakerX = if (actorSpeaking) width - font.width(speaker) - 20 else 6
            height = (fittedContents.size * 9) + 32
        }

        override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
            return false
        }

        override fun updateNarration(narrationElementOutput: NarrationElementOutput) {}

        companion object {
            const val animationDuration = 1f

            fun drawMessageTexture(poseStack: PoseStack, x: Float, y: Int, width: Int, height: Int, u: Int, v: Int) {
                drawTexture(poseStack, x, y.toFloat(), width.toFloat(), height.toFloat(), u.toFloat(), v.toFloat(), 16f, 16f, 48f, 48f)
            }

            fun drawSpeakerTexture(poseStack: PoseStack, x: Float, y: Int, width: Int, height: Int, u: Int, v: Int) {
                drawTexture(poseStack, x, y.toFloat(), width.toFloat(), height.toFloat(), u.toFloat(), v.toFloat(), 8f, 8f, 24f, 24f)
            }

            fun drawTexture(poseStack: PoseStack, x: Float, y: Float, width: Float, height: Float, uOffset: Float, vOffset: Float, uWidth: Float, vHeight: Float, textureWidth: Float, textureHeight: Float) {
                RenderSystem.setShader(GameRenderer::getPositionTexShader)

                val bufferBuilder = Tesselator.getInstance().builder
                val matrix = poseStack.last().pose()

                val x2 = x + width
                val y2 = y + height
                val minU = (uOffset + 0.0f) / textureWidth
                val maxU = (uOffset + uWidth) / textureWidth
                val minV = (vOffset + 0.0f) / textureHeight
                val maxV = (vOffset + vHeight) / textureHeight

                bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX)
                bufferBuilder.vertex(matrix, x, y2, 0f).uv(minU, maxV).endVertex()
                bufferBuilder.vertex(matrix, x2, y2, 0f).uv(maxU, maxV).endVertex()
                bufferBuilder.vertex(matrix, x2, y, 0f).uv(maxU, minV).endVertex()
                bufferBuilder.vertex(matrix, x, y, 0f).uv(minU, minV).endVertex()

                BufferUploader.drawWithShader(bufferBuilder.end())
            }
        }
    }
}