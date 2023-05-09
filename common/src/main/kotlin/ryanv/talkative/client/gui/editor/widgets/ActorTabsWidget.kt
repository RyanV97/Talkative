package ryanv.talkative.client.gui.editor.widgets

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.Button
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.TextComponent
import ryanv.talkative.client.gui.editor.ActorEditorScreen
import ryanv.talkative.client.gui.editor.tabs.EditorTab
import ryanv.talkative.client.gui.widgets.NestedWidget

class ActorTabsWidget(val parentScreen: ActorEditorScreen, x: Int, y: Int, width: Int, height: Int) : NestedWidget(x, y, width, height, TextComponent.EMPTY) {
    private var activeTab: Button? = null

    fun addTab(text: Component, editorTab: EditorTab) {
        val btnWidth = Minecraft.getInstance().font.width(text) + 10
        addChild(TabButton(btnWidth, 20, text, editorTab, this))
        recalculateChildren()
    }

    fun getTab(index: Int): EditorTab? {
        if (index >= children.size)
            return null
        return (children[index] as TabButton).tab
    }

    override fun recalculateChildren() {
        var totalWidth = 5
        for (child in children) {
            child.x = totalWidth
            child.y = y + 5
            totalWidth += child.width + 5
        }
    }

    class TabButton(width: Int, height: Int, text: Component, val tab: EditorTab, parentWidget: ActorTabsWidget) : Button(0, 0, width, height, text, {
        parentWidget.activeTab?.active = true
        parentWidget.parentScreen.changeTab(tab)
        parentWidget.activeTab = it
        it.active = false
    })
}