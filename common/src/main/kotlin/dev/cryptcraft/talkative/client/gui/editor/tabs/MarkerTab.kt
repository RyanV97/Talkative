package dev.cryptcraft.talkative.client.gui.editor.tabs

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import dev.cryptcraft.talkative.client.MarkerRenderer
import dev.cryptcraft.talkative.client.TalkativeClient
import dev.cryptcraft.talkative.client.gui.GuiConstants
import dev.cryptcraft.talkative.client.gui.editor.MainEditorScreen
import dev.cryptcraft.talkative.client.gui.editor.widgets.ColourPickerWidget
import dev.cryptcraft.talkative.client.gui.widgets.NestedWidget
import dev.cryptcraft.talkative.client.gui.widgets.TalkativeButton
import dev.cryptcraft.talkative.client.gui.widgets.lists.WidgetList
import dev.cryptcraft.talkative.api.actor.markers.Marker
import net.minecraft.ResourceLocationException
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiComponent
import net.minecraft.client.gui.components.EditBox
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.client.resources.model.ModelResourceLocation
import net.minecraft.network.chat.Component
import java.awt.Color

class MarkerTab(x: Int, y: Int, width: Int, height: Int, parentScreen: MainEditorScreen) : EditorTab(x, y, width, height, parentScreen, Component.literal("Markers")) {
    var selectedEntry: MarkerEntry? = null
        set(value) {
            field = value

            if (value != null) {
                markerLocationBox.value = value.marker.modelLocation.toString()
                markerColourPicker.setColour(value.marker.overlayColour)
            }

            markerLocationBox.visible = value != null
            markerColourPicker.visible = value != null
        }

    private var rotation: Float = 0f
    private var idleRotate: Boolean = true
    private val addQueue = ArrayList<MarkerEntry>()
    private val removeQueue = ArrayList<MarkerEntry>()

    private var markerList: WidgetList<MainEditorScreen> = addChild(WidgetList(parentScreen, x, y + 10, width / 3, height - 10))
    private var markerLocationBox: EditBox = addChild(EditBox(Minecraft.getInstance().font, 0, 0, 150, 20, Component.empty()))
    private var markerColourPicker: ColourPickerWidget = addChild(ColourPickerWidget(0, 0))

    init {
        refresh()
        markerLocationBox.visible = false
        markerColourPicker.visible = false

        markerLocationBox.setResponder {
            try {
                val string = if (!it.contains("#")) "$it#marker" else it
                selectedEntry?.marker?.modelLocation = ModelResourceLocation(string)
                markerLocationBox.setTextColor(0xFFFFFF)
            } catch (e: ResourceLocationException) {
                markerLocationBox.setTextColor(0xeb4034)
            }
        }
    }

    override fun renderButton(poseStack: PoseStack, mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.renderButton(poseStack, mouseX, mouseY, partialTicks)

        selectedEntry?.let { entry ->
            val marker = entry.marker

            if (idleRotate)
                rotation += Minecraft.getInstance().deltaFrameTime / 25

            GuiComponent.drawString(poseStack, Minecraft.getInstance().font, "Model Location", x + (width / 3) + 10, y + 100, 0xFFFFFF)
            GuiComponent.drawString(poseStack, Minecraft.getInstance().font, "Overlay Colour", x + width - 80, y + 100, 0xFFFFFF)

            renderMarker(marker, poseStack, (x + width * 0.666).toInt(), y + 50, 4f, rotation, Color.HSBtoRGB(markerColourPicker.hue, markerColourPicker.sat, markerColourPicker.bri))
        }
    }

    override fun mouseDragged(mouseX: Double, mouseY: Double, mouseButton: Int, dragX: Double, dragY: Double): Boolean {
        if (isMouseInMarkerArea(mouseX, mouseY)) {
            if (idleRotate) idleRotate = false
            rotation += dragX.toFloat() / 10 * Minecraft.getInstance().deltaFrameTime
            return true
        }
        return super.mouseDragged(mouseX, mouseY, mouseButton, dragX, dragY)
    }

    private fun isMouseInMarkerArea(mouseX: Double, mouseY: Double): Boolean {
        return mouseX > x + (width / 3) && mouseY > y && mouseY < y + 100
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if ((keyCode == 257 || keyCode == 32 || keyCode == 335)) {
            if (markerLocationBox.isFocused)
                markerLocationBox.keyPressed(keyCode, scanCode, modifiers)
            else
                return false
        }
        return super.keyPressed(keyCode, scanCode, modifiers)
    }

    override fun recalculateChildren() {
        markerList.width = width / 3
        markerList.height = height

        markerLocationBox.x = x + (width / 3) + 10
        markerLocationBox.y = y + 110

        markerColourPicker.x = x + width - 80
        markerColourPicker.y = y + 110
    }

    override fun refresh() {
        markerList.clear()

        TalkativeClient.editingActorData?.let { data ->
            for (marker in data.markers) {
                markerList.addChild(MarkerEntry(this, marker, width / 3, 20))
            }
        }
        markerList.addChild(MarkerEntry(this, Marker(), width / 3, 20))

        markerList.addChild(TalkativeButton(0,0, width / 3, 20, Component.literal("+"), {
            addQueue.add(MarkerEntry(this, Marker(), width / 3, 20))
        }))
    }

    fun tick() {
        if (removeQueue.isNotEmpty()) {
            removeQueue.forEach {
                markerList.remove(it)
                if (it == selectedEntry)
                    selectedEntry = null
            }
            removeQueue.clear()
        }

        if (addQueue.isNotEmpty()) {
            val addBtn = markerList.children.last()
            markerList.remove(addBtn)
            addQueue.forEach {
                markerList.addChild(it)
            }
            addQueue.clear()
            markerList.addChild(addBtn)
        }
    }

    companion object {
        private fun renderMarker(marker: Marker, poseStack: PoseStack, x: Int, y: Int, scale: Float, rotation: Float, colour: Int) {
            val itemRenderer = Minecraft.getInstance().itemRenderer
            val modelManager = itemRenderer.itemModelShaper.modelManager
            val model = modelManager.getModel(marker.modelLocation)

            if (model != modelManager.missingModel) {
                MarkerRenderer.renderGuiModel(model, x, y, scale, rotation, colour)
            }
            else {
                RenderSystem.setShader { GameRenderer.getPositionTexShader() }
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
                RenderSystem.setShaderTexture(0, GuiConstants.EDITOR_WIDGETS)
                blit(poseStack, x - (8 * scale).toInt(), y - (8 * scale).toInt(), (16 * scale).toInt(), (16 * scale).toInt(), 0f, 32f, 32, 32, 256, 256)
                GuiComponent.drawCenteredString(poseStack, Minecraft.getInstance().font, "Missing Model", x, y + 20, 0xFFFFFF)
            }
        }
    }

    class MarkerEntry(val parentTab: MarkerTab, val marker: Marker, width: Int, height: Int) : NestedWidget(0,0, width, height, Component.empty()) {
        val deleteButton = addChild(TalkativeButton(0,0, 20, 20, Component.literal("X"), {
            parentTab.removeQueue.add(this)
        }))

        override fun renderButton(poseStack: PoseStack, mouseX: Int, mouseY: Int, partialTicks: Float) {
            val col = if (parentTab.selectedEntry == this) 0xFFFF00 else 0xFFFFFF
            fill(poseStack, x, y, x + width, y + height, 0x55cccccc.toInt())
            renderMarker(marker, poseStack, x + (width / 2), y + (height / 2), 1f, 0f, 0xFFFFFFFF.toInt())
            deleteButton.render(poseStack, mouseX, mouseY, partialTicks)
        }

        override fun mouseClicked(mouseX: Double, mouseY: Double, mouseButton: Int): Boolean {
            if (deleteButton.mouseClicked(mouseX, mouseY, mouseButton))
                return true
            if (isMouseOver(mouseX, mouseY)) {
                parentTab.selectedEntry = this
                parentTab.rotation = 0f
                parentTab.idleRotate = true
            }
            return false
        }

        override fun recalculateChildren() {
            deleteButton.x = x + width - 20
            deleteButton.y = y
        }
    }
}