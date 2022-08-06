package ryanv.talkative.client.gui.widgets.lists

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiComponent

class StringSelectionList(minecraft: Minecraft?, top: Int, height: Int, left: Int, width: Int, private var onSelectionChange: ((selection: TalkativeListEntry) -> Unit?)? = null): TalkativeList<StringSelectionList.StringEntry>(minecraft, top, height, left, width, minecraft!!.font.lineHeight + 7) {

    val mc: Minecraft = minecraft!!

    fun addEntry(value: String) {
        addEntry(StringEntry(mc, this, value, onSelectionChange))
    }

    class StringEntry(minecraft: Minecraft, parent: StringSelectionList, val value: String, onSelectionChange: ((selection: TalkativeListEntry) -> Unit?)?): TalkativeListEntry(minecraft, parent, onSelectionChange) {
        override fun render(poseStack: PoseStack?, id: Int, top: Int, left: Int, width: Int, height: Int, mouseX: Int, mouseY: Int, hover: Boolean, delta: Float) {
            fill(poseStack, left, top, left + width, top + height, 0x55777777)
            GuiComponent.drawString(poseStack, minecraft.font, value, left, top + 2, if(hover) 0xFFFFFF else 0xCCCCCC)
        }
    }

}