package ryanv.talkative.client.gui.widgets

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiComponent
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.Component

class SubmenuWidget(x: Int, y: Int, private val label: String = "...", private val actions: Map<String, () -> Unit> = HashMap()) : AbstractWidget(x, y, 100, 150, Component.literal("Submenu")) {
    private val font: Font = Minecraft.getInstance().font

    init {
        height = 13 + (actions.size * 15)
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
        fill(poseStack, x, y + 13, x + width, y + height, 0xFF333333.toInt())
        GuiComponent.drawString(poseStack, font, label, x + 2, y + 2, 0xFFFFFF)
        var i = 1
        actions.onEachIndexed { index, action ->
            val color = if(isMouseOver(index, mouseX.toDouble(), mouseY.toDouble())) 0x55FF55 else 0xFFFFFF
            GuiComponent.drawString(poseStack, font, action.key, x + 2, y + 2 + (i++ * 15), color)
        }
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, mouseButton: Int): Boolean {
        if(mouseX >= x && mouseY >= y + 13 && mouseX < x + width && mouseY < y + height) {
            val relativeMouseY = (mouseY - y).toInt()
            val i = (relativeMouseY - 13) / 15
            if(i >= 0 && i < actions.size) {
                actions.values.elementAt(i).invoke()
                return true
            }
        }
        return false
    }

    fun isMouseOver(index: Int, mouseX: Double, mouseY: Double): Boolean {
        val actionY = y + 13 + (index * 15)
        return active && visible && mouseX >= x.toDouble() && mouseY >= actionY.toDouble() && mouseX < (x + width).toDouble() && mouseY < (actionY + 15).toDouble()
    }

    override fun updateNarration(narrationElementOutput: NarrationElementOutput) {
    }
}