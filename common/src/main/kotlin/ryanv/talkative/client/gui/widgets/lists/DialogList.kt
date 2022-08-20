package ryanv.talkative.client.gui.widgets.lists

import net.minecraft.client.gui.components.AbstractWidget
import ryanv.talkative.client.gui.DialogScreen

class DialogList(parent: DialogScreen, x: Int, y: Int, width: Int, val maxHeight: Int, val bottom: Int): WidgetList<DialogScreen>(parent, x, y, width, 150) {
    override fun addChild(widget: AbstractWidget) {
        super.addChild(widget)
        height = if(totalHeight < maxHeight) totalHeight else maxHeight
        setY(if(height < maxHeight) bottom - height else 0)
        scrollPos = maxScroll
    }

    override fun remove(widget: AbstractWidget): Boolean {
        if (super.remove(widget)) {
            val i = parent.height - bottom
            val maxHeight = parent.height - i
            height = if(totalHeight < maxHeight) totalHeight else maxHeight
            setY(if(height < maxHeight) bottom - height else 0)
            return true
        }
        return false
    }
}