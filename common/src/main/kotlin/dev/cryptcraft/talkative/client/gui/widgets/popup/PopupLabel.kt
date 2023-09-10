package dev.cryptcraft.talkative.client.gui.widgets.popup

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiComponent
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.Component

class PopupLabel(var x: Int, var y: Int, var contents: Component) : AbstractWidget(x, y, Minecraft.getInstance().font.width(contents), 9, Component.empty()) {
    private val font: Font = Minecraft.getInstance().font

    override fun renderButton(poseStack: PoseStack, mouseX: Int, mouseY: Int, partialTick: Float) {
        GuiComponent.drawString(poseStack, font, contents, x, y, 0xFFFFFF)
    }

    override fun updateNarration(narrationElementOutput: NarrationElementOutput) {}
}