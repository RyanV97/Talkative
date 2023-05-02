package ryanv.talkative.client.gui.widgets.lists

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiComponent

class StringSelectionList(top: Int, height: Int, left: Int, width: Int, private var onSelectionChange: ((selection: TalkativeListEntry) -> Unit?)? = null) : TalkativeList<StringSelectionList.StringEntry>(Minecraft.getInstance(), top, height, left, width, Minecraft.getInstance().font.lineHeight + 7) {
    fun addEntry(value: String) {
        addEntry(StringEntry(Minecraft.getInstance(), this, value, onSelectionChange))
    }

    //ToDo Make this all tidier/nicer
    class StringEntry(minecraft: Minecraft, parent: StringSelectionList, val value: String, onSelectionChange: ((selection: TalkativeListEntry) -> Unit?)?) : TalkativeListEntry(minecraft, parent, onSelectionChange) {
        override fun render(poseStack: PoseStack?, id: Int, top: Int, left: Int, width: Int, height: Int, mouseX: Int, mouseY: Int, hover: Boolean, delta: Float) {
            fill(poseStack, left, top, left + width, top + height, 0x55777777)
            GuiComponent.drawString(poseStack, minecraft.font, value, left + 2, top + 2, if (parent.selectedEntry == this) 0x33d4ff else if (hover) 0xFFFFFF else 0xCCCCCC)
        }
    }
}