package dev.cryptcraft.talkative.client.gui.editor.widgets

import com.mojang.blaze3d.vertex.PoseStack
import dev.cryptcraft.talkative.client.gui.widgets.NestedWidget
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.EditBox
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.Component
import java.awt.Color
import java.text.DecimalFormat

class ColourPickerWidget(x: Int, y: Int, private val callback: ((Int) -> Unit)?) : NestedWidget(x, y, 75, 100, Component.empty()) {
    constructor(x: Int, y: Int) : this(x, y, null)

    private val decimalFormat = DecimalFormat("#.##")
    private val hexEntry = addChild(EditBox(Minecraft.getInstance().font, 0, 0, width, 20, Component.empty()))

    var hue: Float = 0f
        set(value) {
            field = value.coerceIn(0f, 1f)
            callback?.invoke(Color.HSBtoRGB(hue, sat, bri))
        }
    var sat: Float = 1f
        set(value) {
            field = value.coerceIn(0f, 1f)
            callback?.invoke(Color.HSBtoRGB(hue, sat, bri))
        }
    var bri: Float = 1f
        set(value) {
            field = value.coerceIn(0f, 1f)
            callback?.invoke(Color.HSBtoRGB(hue, sat, bri))
        }

    init {
        hexEntry.setResponder {
            if (!hexEntry.canConsumeInput())
                return@setResponder
            try {
                setColour(Color.decode(it))
            }
            catch (e: NumberFormatException) {
                return@setResponder
            }
        }
    }

    override fun recalculateChildren() {
        hexEntry.x = x
        hexEntry.y = y + height - 22
        hexEntry.width = width
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (isMouseInArea(mouseX.toInt(), mouseY.toInt(), x + 4, y, width - 4, width))
            return setSatBriAtMouse(mouseX, mouseY)
        if (setHueAtMouse(mouseX, mouseY))
            return true
        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, dragX: Double, dragY: Double): Boolean {
        if (isMouseInArea(mouseX.toInt(), mouseY.toInt(), x + 4, y, width - 4, width))
            return setSatBriAtMouse(mouseX, mouseY)
        return setHueAtMouse(mouseX, mouseY)
    }

    override fun renderButton(poseStack: PoseStack, mouseX: Int, mouseY: Int, partialTick: Float) {
        super.renderButton(poseStack, mouseX, mouseY, partialTick)

        for (i in 4 until width) {
            val sat = (i - 4).toFloat() / (width - 4).toFloat()
            val startColor = Color.HSBtoRGB(this.hue, sat, 1f)
            val endColor = Color.HSBtoRGB(this.hue, sat, 0f)
            fillGradient(poseStack, x + i, y, x + i + 1, y + width, startColor, endColor)
        }

        val posX = ((x + 4) + (this.sat * (width - 4))).toInt()
        val posY = (y + ((1 - this.bri) * width)).toInt()

        fill(poseStack, posX - 2, posY - 2, posX + 2, posY + 2, Color.HSBtoRGB(0f, 0f, 1f - this.bri))
        fill(poseStack, posX - 1, posY - 1, posX + 1, posY + 1, Color.HSBtoRGB(this.hue, this.sat, this.bri))

        renderHueBar(poseStack, mouseX, mouseY)
    }

    private fun renderHueBar(poseStack: PoseStack, mouseX: Int, mouseY: Int) {
        val r = Color.HSBtoRGB(0f, 1f, 1f)
        val g = Color.HSBtoRGB(.33f, 1f, 1f)
        val b = Color.HSBtoRGB(.66f, 1f, 1f)
        val r2 = Color.HSBtoRGB(1f, 1f, 1f)

        val segmentHeight = (width * .33).toInt()

        fillGradient(poseStack, x, y, x + 4, y + segmentHeight, r, g)
        fillGradient(poseStack, x, y + segmentHeight, x + 4, y + (segmentHeight * 2), g, b)
        fillGradient(poseStack, x, y + (segmentHeight * 2), x + 4, y + width, b, r2)

        if (isMouseInArea(mouseX, mouseY, x, y, 4, width))
            hLine(poseStack, x, x + 3, mouseY, 0xccFFFFFF.toInt())
        hLine(poseStack, x, x + 3, (y + (width * hue)).toInt(), 0xFFFFFFFF.toInt())
    }

    fun setColour(colour: Int) {
        setColour(Color(colour))
        updateHexText()
    }

    fun setColour(colour: Color) {
        val hsv = FloatArray(3)
        Color.RGBtoHSB(colour.red, colour.green, colour.blue, hsv)
        hue = hsv[0]
        sat = hsv[1]
        bri = hsv[2]
        callback?.invoke(Color.HSBtoRGB(hue, sat, bri))
    }

    private fun updateHexText() {
        val col = Color.getHSBColor(hue, sat, bri)
        hexEntry.value = String.format("#%02x%02x%02x", col.red, col.green, col.blue)
    }

    private fun setSatBriAtMouse(mouseX: Double, mouseY: Double): Boolean {
        if (!active || !visible || !this.clicked(mouseX, mouseY))
            return false

        this.sat = ((mouseX - (x + 4)) / (width - 4)).toFloat()
        this.bri = 1f - ((mouseY - y) / width).toFloat()
        updateHexText()
        return true
    }

    private fun setHueAtMouse(mouseX: Double, mouseY: Double): Boolean {
        if (!active || !visible || !this.clicked(mouseX, mouseY))
            return false

        if (isMouseInArea(mouseX.toInt(), mouseY.toInt(), x, y, 4, width + 1)) {
            this.hue = ((mouseY - y) / width).toFloat().coerceIn(0f, 1f)
            updateHexText()
            return true
        }

        return false
    }

    private fun isMouseInArea(mouseX: Int, mouseY: Int, areaX: Int, areaY: Int, areaWidth: Int, areaHeight: Int): Boolean {
        return mouseX >= areaX && mouseX <= areaX + areaWidth && mouseY >= areaY && mouseY < areaY + areaHeight
    }

    override fun updateNarration(narrationElementOutput: NarrationElementOutput) {
    }
}