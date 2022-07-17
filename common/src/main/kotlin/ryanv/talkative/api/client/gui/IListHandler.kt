package ryanv.talkative.api.client.gui

import net.minecraft.client.gui.components.ObjectSelectionList

interface IListHandler<E : ObjectSelectionList.Entry<E>> {
    fun onSelectionChange(selection: E)
}