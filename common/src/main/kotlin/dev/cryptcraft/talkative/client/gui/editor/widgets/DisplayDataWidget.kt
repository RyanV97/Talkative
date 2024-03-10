package dev.cryptcraft.talkative.client.gui.editor.widgets

import com.mojang.blaze3d.vertex.PoseStack
import dev.cryptcraft.talkative.api.actor.DisplayData
import dev.cryptcraft.talkative.client.TalkativeClient
import dev.cryptcraft.talkative.client.gui.widgets.NestedWidget
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiComponent
import net.minecraft.client.gui.components.EditBox
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.resources.ResourceLocation

class DisplayDataWidget(x: Int, y: Int, private var displayData: DisplayData?) : NestedWidget(x, y, 150, 170, Component.empty()) {
    private val font = Minecraft.getInstance().font

    private val overrideDisplayName = addChild(CallbackCheckbox(0,0, 20, 20, Component.literal("Override Entity Name"), TalkativeClient.editingActorData?.displayData?.overrideDisplayName ?: false, ::changeNameOverride))
    private val actorDisplayName = addChild(EditBox(Minecraft.getInstance().font, 0,0, 150, 20, Component.literal("Actor Display Name")))
    
    private val displayInConversation = addChild(CallbackCheckbox(0, 0, 20, 20, Component.literal("Display in Conversations"), TalkativeClient.editingActorData?.displayData?.displayInConversation ?: true, ::changeShouldDisplay))
    private val displayAsEntity = addChild(CallbackCheckbox(0, 0, 20, 20, Component.literal("Display as Entity"), TalkativeClient.editingActorData?.displayData?.displayAsEntity ?: true, ::changeDisplayEntity))
    
    private val displayTexture = addChild(EditBox(Minecraft.getInstance().font, 0,0, 150, 20, Component.literal("Texture Location")))
    private val textureWidth = addChild(EditBox(Minecraft.getInstance().font, 0, 0, 40, 14, Component.literal("Texture Width")))
    private val textureHeight = addChild(EditBox(Minecraft.getInstance().font, 0, 0, 40, 14, Component.literal("Texture Height")))
    
    init {
        actorDisplayName.setResponder {
            displayData?.displayName = it
            if (it.isEmpty())
                actorDisplayName.setSuggestion(TalkativeClient.editingActorEntity!!.displayName.string)
            else
                actorDisplayName.setSuggestion(null)
        }
        actorDisplayName.visible = overrideDisplayName.selected()

        displayTexture.setResponder {
            val resourceLocation = ResourceLocation.tryParse(it)
            if (resourceLocation == null) {
                displayTexture.setTextColor(16711680)
            }
            else {
                displayTexture.setTextColor(14737632)
                displayData?.displayTexture = resourceLocation
            }
        }
        displayTexture.setMaxLength(256)

        textureWidth.setFilter { return@setFilter it.isEmpty() || it.toIntOrNull() != null }
        textureHeight.setFilter { return@setFilter it.isEmpty() || it.toIntOrNull() != null }
        textureWidth.setResponder {
            if(displayData?.textureSize == null)
                displayData?.textureSize = IntArray(2)
            displayData?.textureSize!![0] = it.toIntOrNull() ?: 0
        }
        textureHeight.setResponder {
            if(displayData?.textureSize == null)
                displayData?.textureSize = IntArray(2)
            displayData?.textureSize!![1] = it.toIntOrNull() ?: 0
        }

        displayAsEntity.visible = displayInConversation.selected()
        displayTexture.visible = displayInConversation.selected() && !displayAsEntity.selected()
        textureWidth.visible = displayTexture.visible
        textureHeight.visible = displayTexture.visible
    }

    override fun renderButton(poseStack: PoseStack, mouseX: Int, mouseY: Int, partialTicks: Float) {
        if (displayTexture.visible) {
            val style = Style.EMPTY.withItalic(true)
            GuiComponent.drawString(poseStack, font, Component.literal("Texture Location").withStyle(style), displayTexture.x, displayTexture.y - 10, 0xFFFFFF)
            GuiComponent.drawString(poseStack, font, Component.literal("Width").withStyle(style), textureWidth.x, textureWidth.y - 10, 0xFFFFFF)
            GuiComponent.drawString(poseStack, font, Component.literal("Height").withStyle(style), textureHeight.x, textureHeight.y - 10, 0xFFFFFF)
        }
        this.actorDisplayName.setEditable(this.overrideDisplayName.selected())
        super.renderButton(poseStack, mouseX, mouseY, partialTicks)
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (actorDisplayName.isFocused)
            return actorDisplayName.keyPressed(keyCode, scanCode, modifiers)
        return super.keyPressed(keyCode, scanCode, modifiers)
    }
    
    fun refresh(displayData: DisplayData?) {
        this.displayData = displayData
        actorDisplayName.value = displayData?.displayName.toString()
        displayTexture.value = displayData?.displayTexture.toString()
        textureWidth.value = (displayData?.textureSize?.get(0) ?: 0).toString()
        textureHeight.value = (displayData?.textureSize?.get(1) ?: 0).toString()
    }
    
    private fun changeNameOverride() {
        TalkativeClient.editingActorData?.displayData?.overrideDisplayName = overrideDisplayName.selected()
        checkWidgetVisibility()
    }

    private fun changeShouldDisplay() {
        TalkativeClient.editingActorData?.displayData?.displayInConversation = displayInConversation.selected()
        checkWidgetVisibility()
    }

    private fun changeDisplayEntity() {
        TalkativeClient.editingActorData?.displayData?.displayAsEntity = displayAsEntity.selected()
        checkWidgetVisibility()
    }

    private fun checkWidgetVisibility() {
        actorDisplayName.visible = overrideDisplayName.selected()
        displayAsEntity.visible = displayInConversation.selected()
        displayTexture.visible = displayAsEntity.visible && !displayAsEntity.selected()
        textureWidth.visible = displayTexture.visible
        textureHeight.visible = displayTexture.visible
    }

    override fun recalculateChildren() {
        //Checkbox - Override Display Name
        overrideDisplayName.x = x
        overrideDisplayName.y = y

        //Checkbox - Name to Override Display Name with
        actorDisplayName.x = x
        actorDisplayName.y = y + 25

        displayInConversation.x = x
        displayInConversation.y = y + 55

        displayAsEntity.x = x
        displayAsEntity.y = y + 80

        displayTexture.x = x
        displayTexture.y = y + 120

        textureWidth.x = x
        textureWidth.y = y + 155
        textureHeight.x = x + 45
        textureHeight.y = y + 155
    }
}