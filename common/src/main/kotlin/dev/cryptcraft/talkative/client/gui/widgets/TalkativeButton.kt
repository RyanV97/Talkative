package dev.cryptcraft.talkative.client.gui.widgets

import com.mojang.blaze3d.vertex.PoseStack
import dev.cryptcraft.talkative.client.gui.GuiConstants
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiComponent
import net.minecraft.client.gui.components.AbstractButton
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.Component

open class TalkativeButton(x: Int, y: Int, width: Int, height: Int, message: Component = Component.literal("Button"), val onPress: (TalkativeButton) -> Unit, val onTooltip: ((TalkativeButton, PoseStack, Int, Int) -> Unit)? = null) : AbstractButton(x, y, width, height, message) {

    override fun renderButton(poseStack: PoseStack, mouseX: Int, mouseY: Int, partialTick: Float) {
        fill(poseStack, x, y, x + width, y + height, if (!active) 0xFF306b52.toInt() else if (isHoveredOrFocused) GuiConstants.COLOR_BTN_BORDER_HL else GuiConstants.COLOR_BTN_BORDER)
        fill(poseStack, x + 2, y + 2, x + width - 2, y + height - 2, GuiConstants.COLOR_BTN_BG)
        val offsetX = if (width % 2 == 0) 0 else 1
        val offsetY = if (height % 2 == 0) 4 else 3
        GuiComponent.drawCenteredString(poseStack, Minecraft.getInstance().font, message, x + (width / 2) + offsetX, y + (height / 2) - offsetY, if (!active) 0xcccccc else 0xFFFFFF)

        if (isHoveredOrFocused && onTooltip != null) onTooltip.invoke(this, poseStack, mouseX, mouseY)
    }

    override fun updateNarration(narrationElementOutput: NarrationElementOutput) {
    }

    override fun onPress() {
        onPress.invoke(this)
    }
}