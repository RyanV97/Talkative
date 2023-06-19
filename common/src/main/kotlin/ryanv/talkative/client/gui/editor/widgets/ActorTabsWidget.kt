package ryanv.talkative.client.gui.editor.widgets

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.Component
import ryanv.talkative.client.gui.editor.MainEditorScreen
import ryanv.talkative.client.gui.editor.tabs.EditorTab
import ryanv.talkative.client.gui.widgets.NestedWidget

class ActorTabsWidget(x: Int, y: Int, width: Int, height: Int, val onTabChange: (EditorTab?, EditorTab) -> Unit) : NestedWidget(x, y, width, height, Component.empty()) {
    private var activeTab: EditorTab? = null
    private var activeTabIndex: Int = -1

    fun addTab(text: Component, editorTab: EditorTab) {
        val btnWidth = Minecraft.getInstance().font.width(text) + 10
        addChild(TabButton(children.size, btnWidth, 20, text, editorTab, this))
        recalculateChildren()
    }

    fun getActiveTab(): EditorTab? {
        return this.activeTab
    }

    fun getTab(index: Int): EditorTab? {
        if (index < 0 || index >= children.size)
            return null
        return (children[index] as TabButton).tab
    }

    fun setActiveTab(index: Int) {
        if (index < 0 || index >= children.size)
            return
        getTab(index)?.let { setActiveTab(index, it) }
    }

    fun setActiveTab(index: Int, tab: EditorTab) {
        if (index < 0 || index >= children.size)
            return

        this.onTabChange(this.activeTab, tab)

        if (this.activeTabIndex >= 0) children[activeTabIndex].active = true
        children[index].active = false

        this.activeTabIndex = index
        this.activeTab = tab
    }

    override fun recalculateChildren() {
        var totalWidth = 5
        for (child in children) {
            child.x = totalWidth
            child.y = y + 5
            totalWidth += child.width + 5
        }
    }

    fun clear() {
        clearChildren()
    }

    fun empty(): Boolean {
        return children.isEmpty()
    }

    override fun updateNarration(narrationElementOutput: NarrationElementOutput) {}

    class TabButton(index: Int, width: Int, height: Int, text: Component, val tab: EditorTab, parentWidget: ActorTabsWidget) : Button(0, 0, width, height, text, { parentWidget.setActiveTab(index, tab) })
}