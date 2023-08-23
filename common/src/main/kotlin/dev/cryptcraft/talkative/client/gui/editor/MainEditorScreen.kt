package dev.cryptcraft.talkative.client.gui.editor

import com.mojang.blaze3d.vertex.PoseStack
import dev.architectury.platform.Platform
import dev.cryptcraft.talkative.client.TalkativeClient
import dev.cryptcraft.talkative.client.gui.EditorScreen
import dev.cryptcraft.talkative.client.gui.GuiConstants
import dev.cryptcraft.talkative.client.gui.TalkativeScreen
import dev.cryptcraft.talkative.client.gui.editor.tabs.ActorTab
import dev.cryptcraft.talkative.client.gui.editor.tabs.EditorTab
import dev.cryptcraft.talkative.client.gui.editor.tabs.MarkerTab
import dev.cryptcraft.talkative.client.gui.editor.widgets.EditorTabsWidget
import net.minecraft.client.gui.GuiComponent
import net.minecraft.network.chat.Component

class MainEditorScreen : TalkativeScreen(null, Component.literal("Actor Editor")), EditorScreen {
    private var actorEntity = TalkativeClient.editingActorEntity

    private val tabsHeight = 40
    private val tabsWidget: EditorTabsWidget = EditorTabsWidget(0, 10, 0, tabsHeight, ::onTabChange)

    override fun init() {
        super.init()

        tabsWidget.width = width - 20
        addRenderableWidget(tabsWidget)

        if (tabsWidget.empty())
            generateTabs()
        else {
            val tab = addRenderableWidget(tabsWidget.getActiveTab()!!)
            tab.width = width - 10
            tab.height = height - tabsHeight - 10
        }
    }

    override fun render(poseStack: PoseStack, mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground(poseStack)
        super.render(poseStack, mouseX, mouseY, delta)

        if (tabsWidget.getActiveTab() != null)
            GuiComponent.drawCenteredString(poseStack, minecraft!!.font, tabsWidget.getActiveTab()!!.message, width / 2, 4, 0xFFFFFF)
    }

    override fun renderBackground(poseStack: PoseStack) {
        fill(poseStack, 0, 0, width, height, GuiConstants.COLOR_EDITOR_BG_PRIMARY)
        fill(poseStack, 5, tabsHeight, width - 5, height - 5, GuiConstants.COLOR_EDITOR_BG_SECONDARY)
    }

    private fun generateTabs() {
        val tabY = tabsHeight

        var globalTabIndex = 0
        var actorTabIndex = 0

        //Global Settings
//        globalTabIndex = tabsWidget.addTab(
//            Component.literal("Global"),
//            GlobalTab(5, tabY, width - 10, height - tabsHeight, this)
//        )

        //Actor Specific Settings
        if (actorEntity != null) {
            //General Settings
            actorTabIndex = tabsWidget.addTab(
                Component.literal("Actor"),
                ActorTab(5, tabY, width - 10, height - tabsHeight + 5, this)
            )

            //Marker Settings
            tabsWidget.addTab(
                Component.literal("Markers"),
                MarkerTab(5, tabY, width - 10, height - tabsHeight, this)
            )

            if (Platform.isModLoaded("simplemuseum") || Platform.isDevelopmentEnvironment()) {
                tabsWidget.addShortcut(GuiConstants.SM_ICON) {
                    //ToDo: Tell SM to open Screen
                }
            }
        }

        if (actorEntity != null)
            tabsWidget.setActiveTab(actorTabIndex)
        else
            tabsWidget.setActiveTab(globalTabIndex)
    }

    private fun onTabChange(oldTab: EditorTab?, newTab: EditorTab) {
        if (oldTab != null) removeWidget(oldTab)
        newTab.refresh()
        newTab.width = width - 10
        newTab.height = height - tabsHeight - 10
        addRenderableWidget(newTab)
    }

    override fun refresh() {
        actorEntity = TalkativeClient.editingActorEntity
        tabsWidget.children.forEachIndexed { index, _ ->
            tabsWidget.getTab(index)?.refresh()
        }
    }

    override fun onClose() {
        tabsWidget.getAllTabs().forEach {
            it.onClose()
        }
        super.onClose()
    }

    override fun tick() {
        if (tabsWidget.getActiveTab() is MarkerTab) {
            (tabsWidget.getActiveTab() as MarkerTab).tick()
        }
        super.tick()
    }
}