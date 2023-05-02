package ryanv.talkative.client.gui.dialog.widgets

import net.minecraft.client.gui.components.AbstractWidget
import ryanv.talkative.client.gui.dialog.DialogScreen
import ryanv.talkative.client.gui.widgets.lists.WidgetList

class DialogList(parent: DialogScreen, x: Int, y: Int, width: Int, var maxHeight: Int, private var bottom: Int) : WidgetList<DialogScreen>(parent, x, y, width, 0) {
    init {
        renderBackground = false
        renderEntryBackground = false
    }

    //ToDo Change Scrollbar style n stuffs

    fun setBottom(newBottom: Int) {
        bottom = newBottom
        setY(bottom - height)
    }

    override fun addChild(widget: AbstractWidget) {
        super.addChild(widget)
        height = if (totalHeight < maxHeight) totalHeight else maxHeight
        setY(if (height < maxHeight) bottom - height else 0)
        scrollPos = maxScroll
    }

    override fun remove(widget: AbstractWidget): Boolean {
        if (super.remove(widget)) {
            val i = parent.height - bottom
            val maxHeight = parent.height - i
            height = if (totalHeight < maxHeight) totalHeight else maxHeight
            setY(if (height < maxHeight) bottom - height else 0)
            return true
        }
        return false
    }
}