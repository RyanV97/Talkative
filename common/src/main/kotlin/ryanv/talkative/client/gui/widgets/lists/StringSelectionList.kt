package ryanv.talkative.client.gui.widgets.lists

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiComponent
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.network.chat.TextComponent
import ryanv.talkative.client.gui.TalkativeScreen

class StringSelectionList(parentScreen: TalkativeScreen, x: Int, y: Int, width: Int, height: Int, private var onSelectionChange: ((selection: StringEntry) -> Unit?)? = null) : WidgetList<TalkativeScreen>(parentScreen, x, y, width, height) {
    var selectedEntry: StringEntry? = null

    init {
        renderBackground = false
    }

    fun addEntry(value: String) {
        addChild(StringEntry(this, value))
    }

    fun onSelectionChange(entry: StringEntry) {
        selectedEntry = entry
        onSelectionChange?.invoke(entry)
    }

    override fun recalculateChildren() {
        children.forEachIndexed { index, child ->
            child.x = x
            child.y = index * 15
            child.width = width
        }
    }

    class StringEntry(val parentList: StringSelectionList, val value: String) : AbstractWidget(0, 0, parentList.width, 15, TextComponent.EMPTY) {
        override fun renderButton(poseStack: PoseStack?, mouseX: Int, mouseY: Int, partialTicks: Float) {
            fill(poseStack, x, y, x + width, y + height, 0x55777777)
            GuiComponent.drawString(poseStack, Minecraft.getInstance().font, value, x + 2, y + 2, if (parentList.selectedEntry == this) 0x33d4ff else if (isMouseOver(mouseX.toDouble(), mouseY.toDouble())) 0xFFFFFF else 0xCCCCCC)
        }

        override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
            if (!clicked(mouseX, mouseY))
                return false

            parentList.onSelectionChange(this)
            return true
        }
    }
}