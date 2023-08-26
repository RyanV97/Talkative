package dev.cryptcraft.talkative.client.gui.editor.tabs

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Vector3f
import dev.cryptcraft.talkative.api.actor.markers.Marker
import dev.cryptcraft.talkative.client.MarkerRenderer
import dev.cryptcraft.talkative.client.ScissorUtil
import dev.cryptcraft.talkative.client.TalkativeClient
import dev.cryptcraft.talkative.client.data.ConditionalContext
import dev.cryptcraft.talkative.client.gui.GuiConstants
import dev.cryptcraft.talkative.client.gui.editor.ConditionalEditorPopup
import dev.cryptcraft.talkative.client.gui.editor.MainEditorScreen
import dev.cryptcraft.talkative.client.gui.editor.widgets.ColourPickerWidget
import dev.cryptcraft.talkative.client.gui.widgets.IconButton
import dev.cryptcraft.talkative.client.gui.widgets.NestedWidget
import dev.cryptcraft.talkative.client.gui.widgets.TalkativeButton
import dev.cryptcraft.talkative.client.gui.widgets.lists.WidgetList
import net.minecraft.ResourceLocationException
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiComponent
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.EditBox
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.client.resources.model.ModelResourceLocation
import net.minecraft.network.chat.Component
import java.awt.Color

class MarkerTab(x: Int, y: Int, width: Int, height: Int, parentScreen: MainEditorScreen) : EditorTab(x, y, width, height, parentScreen, Component.literal("Markers")) {
    private var markerList: WidgetList<MainEditorScreen> = addChild(WidgetList(parentScreen, x, y, width / 3, height))
    private var positionOffsetX: EditBox = addChild(EditBox(Minecraft.getInstance().font, 0, y + 13, 65, 14, Component.empty()))
    private var positionOffsetY: EditBox = addChild(EditBox(Minecraft.getInstance().font, 0, y + 32, 65, 14, Component.empty()))
    private var positionOffsetZ: EditBox = addChild(EditBox(Minecraft.getInstance().font, 0, y + 51, 65, 14, Component.empty()))
    private var markerLocationBox: EditBox = addChild(EditBox(Minecraft.getInstance().font, 0, 0, 0, 20, Component.empty()))
    private var markerColourPicker: ColourPickerWidget = addChild(ColourPickerWidget(0, 0) { col ->
        selectedEntry!!.marker.overlayColour = col
    })

    var selectedEntry: MarkerEntry? = null
        set(value) {
            field = value

            if (value != null) {
                val string = value.marker.modelLocation.toString().replace("#marker", "")
                markerLocationBox.value = string
                markerColourPicker.setColour(value.marker.overlayColour)
            }

            positionOffsetX.visible = value != null
            positionOffsetY.visible = value != null
            positionOffsetZ.visible = value != null
            markerLocationBox.visible = value != null
            markerColourPicker.visible = value != null

            val posOffset = value?.marker?.positionOffset ?: Vector3f.ZERO
            positionOffsetX.value = posOffset.x().toString()
            positionOffsetY.value = posOffset.y().toString()
            positionOffsetZ.value = posOffset.z().toString()
        }

    private var rotation: Float = 0f
    private var zoom: Float = 4f
    private var idleRotate: Boolean = true
    private val addQueue = ArrayList<MarkerEntry>()
    private val removeQueue = ArrayList<MarkerEntry>()

    init {
        fun validatePosition(s: String?): Boolean {
            return s?.toFloatOrNull() != null || s?.isEmpty() ?: false || s == "-"
        }

        fun onPositionChange(s: String?) {
            val posX = positionOffsetX.value.toFloatOrNull() ?: 0f
            val posY = positionOffsetY.value.toFloatOrNull() ?: 0f
            val posZ = positionOffsetZ.value.toFloatOrNull() ?: 0f
            selectedEntry?.marker?.positionOffset = Vector3f(posX, posY, posZ)
        }

        positionOffsetX.visible = false
        positionOffsetY.visible = false
        positionOffsetZ.visible = false
        markerLocationBox.visible = false
        markerColourPicker.visible = false

        positionOffsetX.setMaxLength(10)
        positionOffsetY.setMaxLength(10)
        positionOffsetZ.setMaxLength(10)

        positionOffsetX.setFilter(::validatePosition)
        positionOffsetY.setFilter(::validatePosition)
        positionOffsetZ.setFilter(::validatePosition)

        positionOffsetX.setResponder(::onPositionChange)
        positionOffsetY.setResponder(::onPositionChange)
        positionOffsetZ.setResponder(::onPositionChange)

        markerLocationBox.setMaxLength(64)
        markerLocationBox.setResponder {
            try {
                val string = if (!it.contains("#")) "$it#marker" else it
                selectedEntry?.marker?.modelLocation = ModelResourceLocation(string)
                markerLocationBox.setTextColor(0xFFFFFF)
            } catch (e: ResourceLocationException) {
                markerLocationBox.setTextColor(0xeb4034)
            }
        }

        refresh()
    }

    override fun recalculateChildren() {
        markerList.x = x + 5
        markerList.y = y + 15
        markerList.width = (width / 3) - 5
        markerList.height = height - 15

        positionOffsetX.x = x + width - 82
        positionOffsetY.x = x + width - 82
        positionOffsetZ.x = x + width - 82

        markerLocationBox.x = x + (width / 3) + 10
        markerLocationBox.y = y + height - 23
        markerLocationBox.width = (width * .66).toInt() - 20

        markerColourPicker.x = x + width - 90
        markerColourPicker.y = y + height - 122
        markerColourPicker.recalculateChildren()
    }

    override fun renderButton(poseStack: PoseStack, mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.renderButton(poseStack, mouseX, mouseY, partialTicks)
        GuiComponent.drawString(poseStack, Minecraft.getInstance().font, "Attached Markers", x + 5, y + 5, 0xFFFFFF)

        selectedEntry?.let { entry ->
            val marker = entry.marker

            if (idleRotate)
                rotation -= Minecraft.getInstance().deltaFrameTime / 25

            GuiComponent.drawString(poseStack, Minecraft.getInstance().font, "Position Offset", x + width - 90, y + 2, 0xFFFFFF)
            GuiComponent.drawString(poseStack, Minecraft.getInstance().font, "X", x + width - 91, y + 16, 0xFF8c8b)
            GuiComponent.drawString(poseStack, Minecraft.getInstance().font, "Y", x + width - 91, y + 35, 0x90FF8b)
            GuiComponent.drawString(poseStack, Minecraft.getInstance().font, "Z", x + width - 91, y + 54, 0x8bbaFF)
//            GuiComponent.drawString(poseStack, Minecraft.getInstance().font, "Overlay Colour", x + width - 90, y + height - 135, 0xFFFFFF)
            GuiComponent.drawString(poseStack, Minecraft.getInstance().font, "Model Location", x + (width / 3) + 10, y + height - 33, 0xFFFFFF)

            val markerAreaX = x + (width / 3) + 5
            val markerAreaY = y + 5
            val markerAreaX2 = x + width - 100
            val markerAreaY2 = y + height - 45

            if (parentScreen.popup == null) {
                fill(poseStack, markerAreaX, markerAreaY, markerAreaX2, markerAreaY2, GuiConstants.COLOR_EDITOR_BG_PRIMARY)
                ScissorUtil.start(markerAreaX, markerAreaY, markerAreaX2 - markerAreaX, markerAreaY2 - markerAreaY)
                renderMarker(marker, poseStack, markerAreaX + ((markerAreaX2 - markerAreaX) / 2), markerAreaY + ((markerAreaY2 - markerAreaY) / 2), zoom, rotation, Color.HSBtoRGB(markerColourPicker.hue, markerColourPicker.sat, markerColourPicker.bri))
                ScissorUtil.stop()
            }
        }
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, delta: Double): Boolean {
        if (isMouseInMarkerArea(mouseX, mouseY)) {
            zoom = (zoom + delta.toFloat()).coerceIn(1f, 25f)
        }
        return markerList.mouseScrolled(mouseX, mouseY, delta)
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
        return mouseX > x + (width / 3) + 5 && mouseX < x + width - 100 && mouseY > y + 5 && mouseY < y + height - 45
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

    override fun onClose() {
        val list = TalkativeClient.editingActorData?.markers
        list?.clear()
        markerList.children.forEach {
            if (it !is MarkerEntry) return@forEach
            list?.add(it.marker)
        }
    }

    override fun refresh() {
        selectedEntry = null
        markerList.clear()
        val entryHeight = (markerList.height / 10).coerceAtLeast(22)

        TalkativeClient.editingActorData?.let { data ->
            for (marker in data.markers) {
                markerList.addChild(MarkerEntry(this, marker, width / 3, entryHeight))
            }
        }

        markerList.addChild(TalkativeButton(0,0, width / 3, 20, Component.literal("+")) {
            addQueue.add(MarkerEntry(this, Marker(), width / 3, entryHeight))
        })
    }

    override fun tick() {
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

    class MarkerEntry(private val parentTab: MarkerTab, val marker: Marker, width: Int, height: Int) : NestedWidget(0,0, width, height, Component.empty()) {
        private val conditionalButton = addChild(IconButton(0, 0, 20, 20, GuiConstants.CONDITIONAL_ICON, ::conditionalEditor))

        private val deleteButton = addChild(IconButton(0,0, 20, 20, GuiConstants.DELETE_ICON) {
            parentTab.removeQueue.add(this)
        })

        override fun renderButton(poseStack: PoseStack, mouseX: Int, mouseY: Int, partialTicks: Float) {
            val col = if (parentTab.selectedEntry == this) GuiConstants.COLOR_BTN_BORDER_HL else if (isHoveredOrFocused) GuiConstants.COLOR_BTN_BORDER else 0x55cccccc
            fill(poseStack, x, y, x + width, y + height, col)
            fill(poseStack, x + 2, y + 2, x + width - 46, y + height - 2, GuiConstants.COLOR_BTN_BG)
            renderMarker(marker, poseStack, x + 2 + ((width - 46) / 2), y + (height / 2), height / 20f, 0f, marker.overlayColour)
            super.renderButton(poseStack, mouseX, mouseY, partialTicks)
        }

        override fun mouseClicked(mouseX: Double, mouseY: Double, mouseButton: Int): Boolean {
            if (parentTab.parentScreen.popup != null)
                return false
            if (isMouseOver(mouseX, mouseY)) {
                parentTab.selectedEntry = this
                parentTab.rotation = 0f
                parentTab.idleRotate = true
            }
            return super.mouseClicked(mouseX, mouseY, mouseButton)
        }

        private fun conditionalEditor(button: Button?) {
            val parentScreen = parentTab.parentScreen
            val context = ConditionalContext.MarkerContext(marker.conditional)

            val popupWidth = (parentScreen.width / 1.75).toInt()
            val popupHeight = parentScreen.height - 25
            val popupX = (parentTab.width / 2) - (popupWidth / 2)

            parentScreen.popup = ConditionalEditorPopup(parentScreen, popupX, 15, popupWidth, popupHeight, context) {
                val newContext = it as ConditionalContext.MarkerContext
                marker.conditional = newContext.conditional
                parentTab.parentScreen.closePopup()
            }
        }

        override fun recalculateChildren() {
            conditionalButton.x = x + width - 44
            conditionalButton.y = y + (height / 2) - 10

            deleteButton.x = x + width - 22
            deleteButton.y = y + (height / 2) - 10
        }
    }
}