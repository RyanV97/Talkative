package ryanv.talkative.client.gui.widgets.popup

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiComponent
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.components.Widget
import net.minecraft.network.chat.TextComponent

class PopupLabel(var x: Int, var y: Int, var label: String) : AbstractWidget(x, y, Minecraft.getInstance().font.width(label), 9, TextComponent.EMPTY) {
    private val font: Font = Minecraft.getInstance().font
    override fun render(poseStack: PoseStack?, i: Int, j: Int, f: Float) {
        GuiComponent.drawString(poseStack, font, label, x, y, 0xFFFFFF.toInt())
    }
}