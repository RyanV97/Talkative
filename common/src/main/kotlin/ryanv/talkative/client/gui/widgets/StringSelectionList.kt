package ryanv.talkative.client.gui.widgets

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiComponent
import net.minecraft.client.gui.components.ObjectSelectionList
import ryanv.talkative.api.client.gui.IListHandler

class StringSelectionList(val minecraft: Minecraft?, val parent: IListHandler<Entry>, top: Int, bottom: Int, width: Int) : ObjectSelectionList<StringSelectionList.Entry>(minecraft, width, bottom - top, top, bottom, minecraft!!.font.lineHeight + 7) {

    init {
        setRenderBackground(false)
    }

    fun addEntry(value: String) {
        addEntry(Entry(minecraft, parent, value))
    }

    override fun getScrollbarPosition(): Int {
        return rowRight - 6
    }

    override fun renderBackground(poseStack: PoseStack?) {
        fillGradient(poseStack, 0, 0, width, height, -1072689136, -804253680)
    }

    class Entry(val minecraft: Minecraft, val parent: IListHandler<Entry>, val value: String): ObjectSelectionList.Entry<Entry>() {
        override fun render(poseStack: PoseStack?, id: Int, top: Int, left: Int, width: Int, height: Int, mouseX: Int, mouseY: Int, hover: Boolean, delta: Float) {
            fill(poseStack, left, top, left + width, top + height, 0x5500FF00)
            GuiComponent.drawString(poseStack, minecraft.font, value, left, top, if(hover) 0xFFFFFF else 0x555555)
        }

        override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
            parent.onSelectionChange(this)
            return false
        }
    }

}