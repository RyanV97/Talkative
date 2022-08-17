package ryanv.talkative.client.gui.widgets.lists

import net.minecraft.client.gui.components.AbstractWidget
import ryanv.talkative.client.gui.DialogScreen

class DialogList(parent: DialogScreen, x: Int, y: Int, width: Int, height: Int): WidgetList<DialogScreen>(parent, x, y, width, height) {
    var bottom: Int = y
        set(value) {
            field = value
            setY(bottom - height)
            recalculateChildren()
        }

    override fun addChild(widget: AbstractWidget) {
        super.addChild(widget)
        val i = parent.height - bottom
        val maxHeight = parent.height - i
        height = if(totalHeight < maxHeight) totalHeight else maxHeight
        setY(if(height < maxHeight) bottom - height else 0)
        scrollPos = maxScroll
    }

    override fun remove(widget: AbstractWidget) {
        if (children.contains(widget)) {
            removeChild(widget)
            totalHeight -= widget.height

            val i = parent.height - bottom
            val maxHeight = parent.height - i
            height = if(totalHeight < maxHeight) totalHeight else maxHeight
            setY(if(height < maxHeight) bottom - height else 0)
        }
    }
}