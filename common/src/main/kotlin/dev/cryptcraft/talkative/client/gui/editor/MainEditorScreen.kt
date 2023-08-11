package dev.cryptcraft.talkative.client.gui.editor

import com.mojang.blaze3d.vertex.PoseStack
import dev.architectury.platform.Platform
import dev.cryptcraft.talkative.client.gui.DataScreen
import dev.cryptcraft.talkative.client.gui.TalkativeScreen
import dev.cryptcraft.talkative.client.gui.GuiConstants
import dev.cryptcraft.talkative.client.gui.editor.tabs.ActorTab
import dev.cryptcraft.talkative.client.gui.editor.tabs.EditorTab
import dev.cryptcraft.talkative.client.gui.editor.tabs.GlobalTab
import dev.cryptcraft.talkative.client.gui.editor.tabs.MarkerTab
import dev.cryptcraft.talkative.client.gui.editor.widgets.ActorTabsWidget
import dev.cryptcraft.talkative.client.gui.widgets.IconButton
import net.minecraft.client.gui.GuiComponent
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.LivingEntity

class MainEditorScreen(val actorEntity: LivingEntity?) : TalkativeScreen(null, Component.literal("Actor Editor")), DataScreen {
    constructor() : this(null)

    private val tabsHeight = 30
    private val tabsWidget: ActorTabsWidget = ActorTabsWidget(0, 10, 0, tabsHeight, ::onTabChange)

    override fun init() {
        super.init()

        if (tabsWidget.empty())
            generateTabs()
        addRenderableWidget(tabsWidget)
        tabsWidget.width = width - 20

        if (tabsWidget.getActiveTab() != null) {
            val tab = addRenderableWidget(tabsWidget.getActiveTab()!!)
            tab.width = width
        }
    }

    override fun render(poseStack: PoseStack?, mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground(poseStack!!)
        super.render(poseStack, mouseX, mouseY, delta)

        if (tabsWidget.getActiveTab() != null)
            GuiComponent.drawCenteredString(poseStack, minecraft!!.font, tabsWidget.getActiveTab()!!.message, width / 2, 4, 0xFFFFFF)
    }

    override fun renderBackground(poseStack: PoseStack) {
        //ToDo: Make all backgrounds consistent - Near Opaque
        fill(poseStack, 0, 0, width, height, GuiConstants.COLOR_BG)
        GuiComponent.fill(poseStack, 0, tabsHeight + 10, width, height, 0x55000000)
    }

    private fun generateTabs() {
        val tabY = 10 + tabsHeight

        //Global Settings
        tabsWidget.addTab(
            Component.literal("Global"),
            GlobalTab(0, tabY, width, height - tabsHeight, this)
        )

        //Actor Specific Settings
        if (actorEntity != null) {
            //General Settings
            tabsWidget.addTab(
                Component.literal("Actor"),
                ActorTab(0, tabY, width, height - tabsHeight + 5, this)
            )

            //Marker Settings
            tabsWidget.addTab(
                Component.literal("Markers"),
                MarkerTab(0, tabY, width, height - tabsHeight, this)
            )

            if (Platform.isModLoaded("simplemuseum") || Platform.isDevelopmentEnvironment()) {
                tabsWidget.addShortcut(
                    IconButton.Icon(GuiConstants.SM_ICON, 20, 20, textureWidth = 64, textureHeight = 64)
                ) {
                    //ToDo: Tell SM to open Screen
                }
            }
        }

        if (actorEntity != null)
            tabsWidget.setActiveTab(1)
        else
            tabsWidget.setActiveTab(0)
    }

    private fun onTabChange(oldTab: EditorTab?, newTab: EditorTab) {
        if (oldTab != null) removeWidget(oldTab)
        newTab.refresh()
        addRenderableWidget(newTab)
    }

    override fun refresh() {
        tabsWidget.children.forEachIndexed { index, _ ->
            tabsWidget.getTab(index)?.refresh()
        }
    }

    override fun tick() {
        if (tabsWidget.getActiveTab() is MarkerTab) {
            (tabsWidget.getActiveTab() as MarkerTab).tick()
        }
    }
}