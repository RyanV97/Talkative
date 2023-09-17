package dev.cryptcraft.talkative.client.gui

import dev.cryptcraft.talkative.Talkative
import dev.cryptcraft.talkative.client.gui.widgets.IconButton
import net.minecraft.resources.ResourceLocation

object GuiConstants {
    //Colors
    const val COLOR_EDITOR_BG_PRIMARY = 0xFF0F0F0F.toInt()
    const val COLOR_EDITOR_BG_SECONDARY = 0xFF212121.toInt()
    const val COLOR_BTN_BG = 0xFF353c48.toInt()
    const val COLOR_BTN_BORDER = 0xFF238b4e.toInt()
    const val COLOR_BTN_BORDER_HL = 0xFF36D97A.toInt()
    const val COLOR_POPUP_BORDER = 0xFF165932.toInt()

    //Textures
    val DIALOG_MESSAGE_ACTOR = ResourceLocation(Talkative.MOD_ID, "textures/gui/dialog/dialog_message_actor.png")
    val DIALOG_MESSAGE_PLAYER = ResourceLocation(Talkative.MOD_ID, "textures/gui/dialog/dialog_message_player.png")
    val DIALOG_MESSAGE_ACTOR_SPEAKER = ResourceLocation(Talkative.MOD_ID, "textures/gui/dialog/dialog_message_actor_speaker.png")
    val DIALOG_MESSAGE_PLAYER_SPEAKER = ResourceLocation(Talkative.MOD_ID, "textures/gui/dialog/dialog_message_player_speaker.png")
    val DIALOG_SCROLL = ResourceLocation(Talkative.MOD_ID, "textures/gui/dialog/dialog_scroll.png")
    val EDITOR_WIDGETS = ResourceLocation(Talkative.MOD_ID, "textures/gui/editor/editor_widgets.png")
    val EDITOR_BACKGROUND = ResourceLocation(Talkative.MOD_ID, "textures/gui/editor/editor_background.png")
    val BRANCH_EDITOR_BACKGROUND = ResourceLocation(Talkative.MOD_ID, "textures/gui/editor/branch_editor_background.png")

    //Icons
    val ATTACH_ICON = IconButton.Icon(ResourceLocation(Talkative.MOD_ID, "textures/gui/icons/attach.png"), 20, 20, 0, 0, 20, 40, textureWidth = 20, textureHeight = 60)
    val CONDITIONAL_ICON = IconButton.Icon(ResourceLocation(Talkative.MOD_ID, "textures/gui/icons/conditional.png"), 20, 20, 0, 0, 20, 40, textureWidth = 20, textureHeight = 60)
    val DELETE_ICON = IconButton.Icon(ResourceLocation(Talkative.MOD_ID, "textures/gui/icons/delete.png"), 20, 20, 0, 0, 20, 40, textureWidth = 20, textureHeight = 60)
    val DETACH_ICON = IconButton.Icon(ResourceLocation(Talkative.MOD_ID, "textures/gui/icons/detach.png"), 20, 20, 0, 0, 20, 40, textureWidth = 20, textureHeight = 60)
    val EDIT_ICON = IconButton.Icon(ResourceLocation(Talkative.MOD_ID, "textures/gui/icons/edit.png"), 20, 20, 0, 0, 20, 40, textureWidth = 20, textureHeight = 60)
    val SAVE_ICON = IconButton.Icon(ResourceLocation(Talkative.MOD_ID, "textures/gui/icons/save.png"), 20, 20, 0, 0, 20, 40, textureWidth = 20, textureHeight = 60)
    val SM_ICON = IconButton.Icon(ResourceLocation(Talkative.MOD_ID, "textures/gui/icons/simple_museum_icon.png"), 64, 64, textureWidth = 64, textureHeight = 64)
}