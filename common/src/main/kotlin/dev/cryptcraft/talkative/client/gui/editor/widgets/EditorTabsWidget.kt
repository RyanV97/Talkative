package dev.cryptcraft.talkative.client.gui.editor.widgets

import dev.cryptcraft.talkative.client.gui.editor.tabs.EditorTab
import dev.cryptcraft.talkative.client.gui.widgets.IconButton
import dev.cryptcraft.talkative.client.gui.widgets.NestedWidget
import dev.cryptcraft.talkative.client.gui.widgets.TalkativeButton
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.Component

class EditorTabsWidget(x: Int, y: Int, width: Int, height: Int, val onTabChange: (EditorTab?, EditorTab) -> Unit) : NestedWidget(x, y, width, height, Component.empty()) {
    private var activeTab: EditorTab? = null
    private var activeTabIndex: Int = -1

    fun addTab(text: Component, editorTab: EditorTab): Int {
        val btnWidth = Minecraft.getInstance().font.width(text) + 10
        addChild(TabButton(children.size, btnWidth, 20, text, editorTab) {
            setActiveTab((it as TabButton).index, editorTab)
        })
        recalculateChildren()
        return children.size - 1
    }

    fun addTab(icon: IconButton.Icon, editorTab: EditorTab): Int {
        addChild(ImageTabButton(children.size, 20, 20, icon, editorTab, this))
        recalculateChildren()
        return children.size - 1
    }

    fun addShortcut(text: Component, onPress: (Button) -> Unit) {
        val btnWidth = Minecraft.getInstance().font.width(text) + 10
        addChild(TabButton(children.size, btnWidth, 20, text, null, onPress))
        recalculateChildren()
    }

    fun addShortcut(icon: IconButton.Icon, onPress: (Button) -> Unit) {
        addChild(ImageTabButton(children.size, 20, 20, icon, null, this, onPress))
        recalculateChildren()
    }

    fun getActiveTab(): EditorTab? {
        return this.activeTab
    }

    fun getTab(index: Int): EditorTab? {
        if (index < 0 || index >= children.size)
            return null
        return (children[index] as TabWidget).getTab()
    }

    fun setActiveTab(index: Int) {
        if (index < 0 || index >= children.size)
            return
        getTab(index)?.let { setActiveTab(index, it) }
    }

    fun setActiveTab(index: Int, tab: EditorTab?) {
        if (index < 0 || index >= children.size || tab == null)
            return

        this.onTabChange(this.activeTab, tab)

        if (this.activeTabIndex >= 0) children[activeTabIndex].active = true
        children[index].active = false

        this.activeTabIndex = index
        this.activeTab = tab
        this.activeTab!!.recalculateChildren()
    }

    override fun recalculateChildren() {
        var totalWidth = 5
        for (child in children) {
            child.x = totalWidth
            child.y = y + 5
            totalWidth += child.width + 5
            (child as TabWidget).getTab()?.recalculateChildren()
        }
    }

    fun getAllTabs(): ArrayList<EditorTab> {
        val list = ArrayList<EditorTab>()
        children.forEach {
            list.add((it as TabWidget).getTab() ?: return@forEach)
        }
        return list
    }

    fun clear() {
        clearChildren()
    }

    fun empty(): Boolean {
        return children.isEmpty()
    }

    override fun updateNarration(narrationElementOutput: NarrationElementOutput) {}

    interface TabWidget {
        fun getTab(): EditorTab?
    }

    class TabButton(val index: Int, width: Int, height: Int, text: Component, private val tab: EditorTab?, onPress: OnPress) : TalkativeButton(0, 0, width, height, text, onPress), TabWidget {
        override fun getTab(): EditorTab? {
            return tab
        }
    }

    class ImageTabButton(index: Int, width: Int, height: Int, icon: Icon, private val tab: EditorTab?, parentWidget: EditorTabsWidget, onPress: (Button) -> Unit = { parentWidget.setActiveTab(index, tab) }) : IconButton(0, 0, width, height, icon, onPress), TabWidget {
        override fun getTab(): EditorTab? {
            return tab
        }

    }
}