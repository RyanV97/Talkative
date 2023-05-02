package ryanv.talkative.client.gui.widgets.lists

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.ObjectSelectionList

open class TalkativeList<out E : TalkativeList.TalkativeListEntry>(
    val minecraft: Minecraft?,
    top: Int,
    height: Int,
    left: Int,
    width: Int,
    entryHeight: Int = minecraft!!.font.lineHeight + 7,
    private val entryWidth: Int = width - 10
) : ObjectSelectionList<TalkativeList.TalkativeListEntry>(minecraft, width, height, top, top + height, entryHeight) {

    var selectedEntry: TalkativeListEntry? = null
    var enableBackground: Boolean = true

    init {
        setRenderBackground(false)
        setRenderTopAndBottom(false)
        setLeftPos(left)
    }

    override fun renderBackground(poseStack: PoseStack?) {
        if(enableBackground)
            fillGradient(poseStack, 0, 0, width, height, -1072689136, -804253680)
    }

    public override fun addEntry(entry: TalkativeListEntry?): Int {
        return super.addEntry(entry)
    }

    fun indexOf(entry: TalkativeListEntry?): Int {
        children().forEachIndexed { index, child ->
            if(child.equals(entry))
                return index
        }
        return -1
    }

    override fun getScrollbarPosition(): Int {
        return rowRight
    }

    override fun getRowLeft(): Int {
        return x0 + width / 2 - this.rowWidth / 2
    }

    override fun getRowWidth(): Int {
        return entryWidth
    }

    abstract class TalkativeListEntry(val minecraft: Minecraft, val parent: TalkativeList<TalkativeListEntry>, private var onSelectionChange: ((selection: TalkativeListEntry) -> Unit?)?): Entry<TalkativeListEntry>() {
        override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
            parent.selectedEntry = this
            onSelectionChange?.let { it(this) }
            return false
        }
    }

}