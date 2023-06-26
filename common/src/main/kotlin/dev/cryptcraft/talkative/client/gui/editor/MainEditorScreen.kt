package dev.cryptcraft.talkative.client.gui.editor

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.LivingEntity
import dev.cryptcraft.talkative.client.gui.DataScreen
import dev.cryptcraft.talkative.client.gui.TalkativeScreen
import dev.cryptcraft.talkative.client.gui.editor.tabs.GlobalEditorTab
import dev.cryptcraft.talkative.client.gui.editor.tabs.EditorTab
import dev.cryptcraft.talkative.client.gui.editor.tabs.ActorGeneralEditorTab
import dev.cryptcraft.talkative.client.gui.editor.widgets.ActorTabsWidget

class MainEditorScreen(val actorEntity: LivingEntity?) : TalkativeScreen(null, Component.literal("Actor Editor")), DataScreen {
    constructor() : this(null)

    private val tabsHeight = 30
    private val tabsWidget: ActorTabsWidget = ActorTabsWidget(0, 0, 0, tabsHeight, ::onTabChange)

    override fun init() {
        super.init()

        tabsWidget.width = width

        addRenderableWidget(tabsWidget)
        if (tabsWidget.getActiveTab() != null) addRenderableWidget(tabsWidget.getActiveTab())

        if (tabsWidget.empty()) {
            //Global Settings
            tabsWidget.addTab(
                Component.literal("Global"),
                GlobalEditorTab(0, tabsHeight + 5, width, height - tabsHeight + 5, this)
            )

            //Actor Specific Settings
            if (actorEntity != null) {
                //General Actor Settings
                tabsWidget.addTab(
                    Component.literal("Actor"),
                    ActorGeneralEditorTab(0, tabsHeight + 5, width, height - tabsHeight + 5, this)
                )
                //ToDo Add a button for when Simple Museum is installed, to open that menu
//            tabsWidget.addTab(Component.literal("Markers"), NestedWidget(0, 20, width, height - 20, Component.literal("Actor Marker Settings")))
            }

            tabsWidget.setActiveTab(0)
        }
    }

    override fun render(poseStack: PoseStack?, mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground(poseStack!!)
        super.render(poseStack, mouseX, mouseY, delta)
    }

    override fun renderBackground(poseStack: PoseStack) {
        fill(poseStack, 0, 0, width, height, -1072689136)
    }

    private fun onTabChange(oldTab: EditorTab?, newTab: EditorTab) {
        if (oldTab != null) removeWidget(oldTab)
        addRenderableWidget(newTab)
    }

    override fun refresh() {
        tabsWidget.children.forEachIndexed { index, _ ->
            tabsWidget.getTab(index)?.refresh()
        }
    }
}