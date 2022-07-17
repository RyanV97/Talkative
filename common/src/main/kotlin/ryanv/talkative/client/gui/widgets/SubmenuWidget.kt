package ryanv.talkative.client.gui.widgets

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiComponent
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.components.Button
import net.minecraft.network.chat.TextComponent

class SubmenuWidget(x: Int, y: Int, private val label: String = "...", val actions: Map<String, (DialogNodeWidget) -> Unit> = HashMap()) : AbstractWidget(x, y, 100, 150, TextComponent("Submenu")) {

    val font: Font

    init {
        font = Minecraft.getInstance().font
        height = (actions.size) * 15
        var maxWidth: Int = width
        actions.forEach {
            val i = font.width(it.key) + 3
            if(i > maxWidth)
                maxWidth = i
        }
        width = maxWidth
    }

    override fun render(poseStack: PoseStack?, mouseX: Int, mouseY: Int, delta: Float) {
        fill(poseStack, x, y, x + width, y + 13, 0xFF222222.toInt())
        fill(poseStack, x, y + 13, x + width, y + 15 + height, 0xFF333333.toInt())
        GuiComponent.drawString(poseStack, font, label, x + 2, y + 2, 0xFFFFFF)
        var i = 1
        actions.forEach {
            GuiComponent.drawString(poseStack, font, it.key, x + 2, y + 2 + (i++ * 15), 0xFFFFFF)
        }
    }

    override fun mouseClicked(d: Double, e: Double, i: Int): Boolean {
        return false
    }

}