package dev.cryptcraft.talkative.client.gui.editor.widgets

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiComponent
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.Component
import java.awt.Color
import java.text.DecimalFormat

class ColourPickerWidget(x: Int, y: Int) : AbstractWidget(x, y, 75, 75, Component.empty()) {
    private val decimalFormat = DecimalFormat("#.##")
    //ToDo: Add Hex code editbox

    var hue: Float = 0f
        set(value) {
            field = value.coerceIn(0f, 1f)
        }
    var sat: Float = 1f
        set(value) {
            field = value.coerceIn(0f, 1f)
        }
    var bri: Float = 1f
        set(value) {
            field = value.coerceIn(0f, 1f)
        }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (isMouseInArea(mouseX.toInt(), mouseY.toInt(), x + 4, y, width - 4, height))
            return setSatBriAtMouse(mouseX, mouseY)
        return setHueAtMouse(mouseX, mouseY)
    }

    override fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, dragX: Double, dragY: Double): Boolean {
        if (isMouseInArea(mouseX.toInt(), mouseY.toInt(), x + 4, y, width - 4, height))
            return setSatBriAtMouse(mouseX, mouseY)
        return setHueAtMouse(mouseX, mouseY)
    }

    override fun renderButton(poseStack: PoseStack, mouseX: Int, mouseY: Int, partialTick: Float) {
        for (i in 4 until width) {
            val sat = (i - 4).toFloat() / (width - 4).toFloat()
            val startColor = Color.HSBtoRGB(this.hue, sat, 1f)
            val endColor = Color.HSBtoRGB(this.hue, sat, 0f)
            fillGradient(poseStack, x + i, y, x + i + 1, y + height, startColor, endColor)
        }

        val posX = ((x + 4) + (this.sat * (width - 4))).toInt()
        val posY = (y + ((1 - this.bri) * height)).toInt()

        fill(poseStack, posX - 2, posY - 2, posX + 2, posY + 2, Color.HSBtoRGB(0f, 0f, 1f - this.bri))
        fill(poseStack, posX - 1, posY - 1, posX + 1, posY + 1, Color.HSBtoRGB(this.hue, this.sat, this.bri))

        renderHueBar(poseStack, mouseX, mouseY)
    }

    private fun renderHueBar(poseStack: PoseStack, mouseX: Int, mouseY: Int) {
        val r = Color.HSBtoRGB(0f, 1f, 1f)
        val g = Color.HSBtoRGB(.33f, 1f, 1f)
        val b = Color.HSBtoRGB(.66f, 1f, 1f)
        val r2 = Color.HSBtoRGB(1f, 1f, 1f)

        val segmentHeight = (height * .33).toInt()

        fillGradient(poseStack, x, y, x + 4, y + segmentHeight, r, g)
        fillGradient(poseStack, x, y + segmentHeight, x + 4, y + (segmentHeight * 2), g, b)
        fillGradient(poseStack, x, y + (segmentHeight * 2), x + 4, y + height, b, r2)

        if (isMouseInArea(mouseX, mouseY, x, y, 4, height))
            hLine(poseStack, x, x + 3, mouseY, 0xccFFFFFF.toInt())
        hLine(poseStack, x, x + 3, (y + (height * hue)).toInt(), 0xFFFFFFFF.toInt())
    }

    fun setColour(colour: Int) {
        val color = Color(colour)
        val hsv = FloatArray(3)
        Color.RGBtoHSB(color.red, color.green, color.blue, hsv)
        hue = hsv[0]
        sat = hsv[1]
        bri = hsv[2]
    }

    private fun setSatBriAtMouse(mouseX: Double, mouseY: Double): Boolean {
        if (!active || !visible || !this.clicked(mouseX, mouseY))
            return false

        this.sat = ((mouseX - (x + 4)) / (width - 4)).toFloat()
        this.bri = 1f - ((mouseY - y) / height).toFloat()
        return true
    }

    private fun setHueAtMouse(mouseX: Double, mouseY: Double): Boolean {
        if (!active || !visible || !this.clicked(mouseX, mouseY))
            return false

        if (isMouseInArea(mouseX.toInt(), mouseY.toInt(), x, y, 4, height + 1))
            this.hue = ((mouseY - y) / height).toFloat().coerceIn(0f, 1f)

        return true
    }

    private fun isMouseInArea(mouseX: Int, mouseY: Int, areaX: Int, areaY: Int, areaWidth: Int, areaHeight: Int): Boolean {
        return mouseX >= areaX && mouseX <= areaX + areaWidth && mouseY >= areaY && mouseY < areaY + areaHeight
    }

    override fun updateNarration(narrationElementOutput: NarrationElementOutput) {
    }
}