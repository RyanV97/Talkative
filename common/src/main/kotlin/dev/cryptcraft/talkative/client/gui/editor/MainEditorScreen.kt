package dev.cryptcraft.talkative.client.gui.editor

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.Tesselator
import com.mojang.blaze3d.vertex.VertexFormat
import dev.cryptcraft.talkative.client.TalkativeClient
import dev.cryptcraft.talkative.client.gui.EditorScreen
import dev.cryptcraft.talkative.client.gui.GuiConstants
import dev.cryptcraft.talkative.client.gui.TalkativeScreen
import dev.cryptcraft.talkative.client.gui.editor.tabs.ActorTab
import dev.cryptcraft.talkative.client.gui.editor.tabs.EditorTab
import dev.cryptcraft.talkative.client.gui.editor.tabs.GlobalTab
import dev.cryptcraft.talkative.client.gui.editor.tabs.MarkerTab
import dev.cryptcraft.talkative.client.gui.editor.widgets.EditorTabsWidget
import dev.cryptcraft.talkative.client.gui.widgets.popup.PopupWidget
import dev.cryptcraft.talkative.common.network.serverbound.UpdateActorData
import net.minecraft.ChatFormatting
import net.minecraft.client.gui.GuiComponent
import net.minecraft.client.renderer.GameRenderer
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
            refresh()
        }
    }

    override fun render(poseStack: PoseStack, mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground(poseStack)
        if (tabsWidget.getActiveTab() != null)
            GuiComponent.drawCenteredString(poseStack, minecraft!!.font, tabsWidget.getActiveTab()!!.message, width / 2, 4, 0xFFFFFF)
        super.render(poseStack, mouseX, mouseY, delta)
    }

    override fun renderBackground(poseStack: PoseStack) {
        val tesselator = Tesselator.getInstance()
        val bufferBuilder = tesselator.builder
        RenderSystem.setShader { GameRenderer.getPositionTexColorShader() }
        RenderSystem.setShaderTexture(0, GuiConstants.EDITOR_BACKGROUND)
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
        val scale = 48f
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR)
        bufferBuilder.vertex(0.0, height.toDouble(), 0.0).uv(0.0f, height.toFloat() / scale).color(80, 100, 80, 255).endVertex()
        bufferBuilder.vertex(width.toDouble(), height.toDouble(), 0.0).uv(width.toFloat() / scale, height.toFloat() / scale).color(64, 85, 64, 255).endVertex()
        bufferBuilder.vertex(width.toDouble(), 0.0, 0.0).uv(width.toFloat() / scale, 0f).color(64, 69, 64, 255).endVertex()
        bufferBuilder.vertex(0.0, 0.0, 0.0).uv(0f, 0f).color(64, 64, 64, 255).endVertex()
        tesselator.end()

        //Tabs Bar
        fill(poseStack, 0, 0, width, tabsHeight, 0xA50F0F0F.toInt())
        fill(poseStack, 0, tabsHeight, width, tabsHeight + 5, GuiConstants.COLOR_EDITOR_BG_SECONDARY)
    }

    private fun generateTabs() {
        val tabY = tabsHeight

        var globalTabIndex = 0
        var actorTabIndex = 0

        //Global Settings
        globalTabIndex = tabsWidget.addTab(
            Component.literal("Global"),
            GlobalTab(5, tabY, width - 10, height - tabsHeight, this)
        )

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

            //ToDo Simple Museum compat
//            if (Platform.isModLoaded("simplemuseum") || Platform.isDevelopmentEnvironment()) {
//                tabsWidget.addShortcut(GuiConstants.SM_ICON) {
//                    //Tell SM to open Screen
//                }
//            }
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
        val popupX = width / 2 - 127
        val popupY = height / 2 - 25
        openPopup(PopupWidget.Builder(popupX, popupY, 255, 50, this)
            .label(6, 7, Component.translatable("talkative.gui.editor.close_confirm"))
            .button(5, 25, "Save", 50, 20,
                {
                    tabsWidget.getAllTabs().forEach {
                         it.onClose()
                    }
                    UpdateActorData(TalkativeClient.editingActorEntity?.id ?: -1, TalkativeClient.editingActorData).sendToServer()
                    super.onClose()
                },
                { _, poseStack, _, _ ->
                    val label = Component.translatable("talkative.gui.editor.save").withStyle(ChatFormatting.GREEN)
                    val labelWidth = font.width(label)
                    renderTooltip(poseStack, label, (popupX + 127) - (labelWidth / 2) - 15, popupY + 65)
                }
            )
            .button(103, 25, "Discard", 50, 20,
                {
                    super.onClose()
                },
                { _, poseStack, _, _ ->
                    val label = Component.translatable("talkative.gui.editor.discard").withStyle(ChatFormatting.RED)
                    val labelWidth = font.width(label)
                    renderTooltip(poseStack, label, (popupX + 127) - (labelWidth / 2) - 15, popupY + 65)
                }
            )
            .button(200, 25, "Cancel", 50, 20,
                {
                    closePopup()
                },
                { _, poseStack, _, _ ->
                    val label = Component.translatable("talkative.gui.editor.cancel")
                    val labelWidth = font.width(label)
                    renderTooltip(poseStack, label, (popupX + 127) - (labelWidth / 2) - 8, popupY + 65)
                }
            )
            .build())
    }

    override fun tick() {
        tabsWidget.getActiveTab()?.tick()
        super.tick()
    }
}