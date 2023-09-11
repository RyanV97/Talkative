package dev.cryptcraft.talkative.client.gui.dialog.widgets

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import dev.cryptcraft.talkative.client.gui.GuiConstants
import dev.cryptcraft.talkative.client.gui.widgets.lists.WidgetList
import dev.cryptcraft.talkative.common.network.clientbound.DialogPacket
import dev.cryptcraft.talkative.common.network.serverbound.DialogResponsePacket
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.network.chat.Component

class ResponseList(parent: Screen, x: Int, y: Int, width: Int, height: Int) : WidgetList<Screen>(parent, x, y, width, height) {
    init {
        renderBackground = false
        renderEntryBackground = false
    }

    fun addEntry(responseData: DialogPacket.ResponseData) {
        val numLabel = "${children.size + 1}. "
        val xOffset = Minecraft.getInstance().font.width(numLabel)
        val entryHeight = Minecraft.getInstance().font.wordWrapHeight(responseData.contents, width - xOffset)
        addChild(ResponseListEntry(this, responseData, width, entryHeight + 5))
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

    class ResponseListEntry(private val parentList: ResponseList, val responseData: DialogPacket.ResponseData, width: Int, height: Int, val font: Font = Minecraft.getInstance().font) : AbstractWidget(0, 0, width, height, Component.empty()) {
        override fun renderButton(poseStack: PoseStack, mouseX: Int, mouseY: Int, partialTick: Float) {
            val numLabel = "${parentList.children.indexOf(this) + 1}. "
            val xOffset = font.width(numLabel)
            val col = if (isHoveredOrFocused) 0xFFFFFF else 0x9FFFFFFF.toInt()
            font.draw(poseStack, numLabel, x.toFloat(), y.toFloat(), col)
            font.drawWordWrap(responseData.contents, x + xOffset, y, width - xOffset, col)
        }

        override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
            if (!active || !visible || !isValidClickButton(button) || !clicked(mouseX, mouseY))
                return false
            onResponse()
            return true
        }

        fun onResponse() {
            DialogResponsePacket(responseData.responseId).sendToServer()
            if (responseData.type == DialogPacket.ResponseData.Type.Exit)
                parentList.parent.onClose()
        }

        override fun updateNarration(narrationElementOutput: NarrationElementOutput) {}
    }
}