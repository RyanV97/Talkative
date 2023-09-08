package dev.cryptcraft.talkative.client.gui.editor.tabs

import com.mojang.blaze3d.vertex.PoseStack
import dev.cryptcraft.talkative.client.TalkativeClient
import dev.cryptcraft.talkative.client.gui.editor.MainEditorScreen
import dev.cryptcraft.talkative.client.gui.editor.branch.BranchSelectionScreen
import dev.cryptcraft.talkative.client.gui.editor.widgets.ActorBranchList
import dev.cryptcraft.talkative.client.gui.editor.widgets.CallbackCheckbox
import dev.cryptcraft.talkative.client.gui.widgets.TalkativeButton
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiComponent
import net.minecraft.client.gui.components.EditBox
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.resources.ResourceLocation

class ActorTab(x: Int, y: Int, width: Int, height: Int, val parent: MainEditorScreen) : EditorTab(x, y, width, height, parent, Component.literal("General Actor Settings")) {
    private val overrideDisplayName = addChild(CallbackCheckbox(0,0, 20, 20, Component.literal("Override Entity Name"), TalkativeClient.editingActorData?.displayData?.overrideDisplayName ?: false, ::changeNameOverride))
    private val actorDisplayName = addChild(EditBox(Minecraft.getInstance().font, 0,0, 150, 20, Component.literal("Actor Display Name")))

    private val displayInConversation = addChild(CallbackCheckbox(0, 0, 20, 20, Component.literal("Display in Conversations"), TalkativeClient.editingActorData?.displayData?.displayInConversation ?: true, ::changeShouldDisplay))
    private val displayAsEntity = addChild(CallbackCheckbox(0, 0, 20, 20, Component.literal("Display as Entity"), TalkativeClient.editingActorData?.displayData?.displayAsEntity ?: true, ::changeDisplayEntity))

    private val displayTexture = addChild(EditBox(Minecraft.getInstance().font, 0,0, 150, 20, Component.literal("Texture Location")))
    private val textureWidth = addChild(EditBox(Minecraft.getInstance().font, 0, 0, 40, 14, Component.literal("Texture Width")))
    private val textureHeight = addChild(EditBox(Minecraft.getInstance().font, 0, 0, 40, 14, Component.literal("Texture Height")))

    private val branchList = addChild(ActorBranchList(this, 0, 0, width / 2, height - 40, Component.literal("Attached Branches List")))
    private val attachButton = addChild(TalkativeButton(0, 0, 15, 15, Component.literal("+"), { openAttachBranchScreen() }))

    init {
        recalculateChildren()
        refresh()

        val displayData = TalkativeClient.editingActorData?.displayData

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
        fill(poseStack, branchList.x - 5, branchList.y - 20, branchList.x + branchList.width + 5, branchList.y + branchList.height, 0x600F0F0F) //Background

        val font = Minecraft.getInstance().font
        GuiComponent.drawString(poseStack, font, Component.literal("Attached Branches").withStyle { it.withBold(true).withUnderlined(true) }, x + (width / 2) - 5, y + 20, 0xFFFFFF)

        if (displayTexture.visible) {
            val style = Style.EMPTY.withItalic(true)
            GuiComponent.drawString(poseStack, font, Component.literal("Texture Location").withStyle(style), displayTexture.x, displayTexture.y - 10, 0xFFFFFF)
            GuiComponent.drawString(poseStack, font, Component.literal("Width").withStyle(style), textureWidth.x, textureWidth.y - 10, 0xFFFFFF)
            GuiComponent.drawString(poseStack, font, Component.literal("Height").withStyle(style), textureHeight.x, textureHeight.y - 10, 0xFFFFFF)
        }
        this.actorDisplayName.setEditable(this.overrideDisplayName.selected())

        super.renderButton(poseStack, mouseX, mouseY, partialTicks)
    }

    override fun onClose() {}

    override fun refresh() {
        val displayData = TalkativeClient.editingActorData?.displayData
        actorDisplayName.value = displayData?.displayName.toString()
        displayTexture.value = displayData?.displayTexture.toString()
        textureWidth.value = (displayData?.textureSize?.get(0) ?: 0).toString()
        textureHeight.value = (displayData?.textureSize?.get(1) ?: 0).toString()

        this.branchList.clear()
        var index = 0
        TalkativeClient.editingActorData?.dialogBranches?.forEach {
            this.branchList.addEntry(index++, it)
        }
    }

    override fun recalculateChildren() {
        //Checkbox - Override Display Name
        overrideDisplayName.x = x + 9
        overrideDisplayName.y = y + 15

        //Checkbox - Name to Override Display Name with
        actorDisplayName.x = x + 10
        actorDisplayName.y = y + 40

        displayInConversation.x = x  + 10
        displayInConversation.y = y + 70

        displayAsEntity.x = x + 10
        displayAsEntity.y = y + 95

        displayTexture.x = x + 10
        displayTexture.y = y + 135

        textureWidth.x = x + 10
        textureWidth.y = y + 170
        textureHeight.x = x + 60
        textureHeight.y = y + 170

        //List - Attached Branch List
        branchList.x = width / 2
        branchList.y = y + 35
        branchList.width = (width / 2) - 5
        branchList.height = height - 35
        branchList.recalculateChildren()
        branchList.renderBackground = false

        //Button - Attach Branch Button
        attachButton.x = x + width - 26
        attachButton.y = y + 17

        super.recalculateChildren()
    }

    override fun tick() {
        branchList.tick()
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

    private fun openAttachBranchScreen() {
        Minecraft.getInstance().setScreen(BranchSelectionScreen(parent, BranchSelectionScreen.ListMode.ATTACH))
    }

    override fun updateNarration(narrationElementOutput: NarrationElementOutput) {}
}