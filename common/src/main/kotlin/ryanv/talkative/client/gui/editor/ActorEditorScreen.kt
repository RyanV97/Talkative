package ryanv.talkative.client.gui.editor

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.network.chat.TextComponent
import net.minecraft.world.entity.LivingEntity
import ryanv.talkative.client.TalkativeClient
import ryanv.talkative.client.gui.ActorDataScreen
import ryanv.talkative.client.gui.TalkativeScreen
import ryanv.talkative.client.gui.editor.tabs.ActorBranchEditorTab
import ryanv.talkative.client.gui.editor.tabs.EditorTab
import ryanv.talkative.client.gui.editor.tabs.GeneralEditorTab
import ryanv.talkative.client.gui.editor.widgets.ActorTabsWidget
import ryanv.talkative.common.data.ActorData

class ActorEditorScreen(val actorEntity: LivingEntity) : TalkativeScreen(null, TextComponent("Actor Editor")), ActorDataScreen {
    private lateinit var tabsWidget: ActorTabsWidget
    private var activeTab: EditorTab? = null

    override fun init() {
        super.init()

        val tabsHeight = 30
        tabsWidget = addButton(ActorTabsWidget(this, 0, 0, width, tabsHeight))
        tabsWidget.addTab(TextComponent("General"), GeneralEditorTab(0, tabsHeight + 5, width, height - tabsHeight + 5, this))
        tabsWidget.addTab(TextComponent("Branches"), ActorBranchEditorTab(0, tabsHeight + 5, width, height - tabsHeight + 5, this))
//        tabsWidget.addTab(TextComponent("Markers"), NestedWidget(0, 20, width, height - 20, TextComponent("Actor Marker Settings")))
//        tabsWidget.addTab(TextComponent("Global"), NestedWidget(0, 20, width, height - 20, TextComponent("Actor Marker Settings")))

        (tabsWidget.children[0] as ActorTabsWidget.TabButton).onPress()
        //ToDo Add a button for when Simple Museum is installed, to open that menu
    }

    override fun render(poseStack: PoseStack?, mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground(poseStack)
        super.render(poseStack, mouseX, mouseY, delta)
    }

    override fun renderBackground(poseStack: PoseStack?) {
        fill(poseStack, 0, 0, width, height, -1072689136)
    }

    override fun refresh() {
        for (i in 0 until tabsWidget.children.size) {
            tabsWidget.getTab(i)?.refresh()
        }
    }

    fun changeTab(newTab: EditorTab) {
        buttons.remove(activeTab)
        children.remove(activeTab)
        addButton(newTab)

        activeTab = newTab
        newTab.setPos(0, 20)
        newTab.width = width
        newTab.height = height - 20
        newTab.refresh()
    }
}