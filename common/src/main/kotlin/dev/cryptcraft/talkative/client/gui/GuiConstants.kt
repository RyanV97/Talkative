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
    const val COLOR_BTN_BORDER_HL = 0xFF3ecf7d.toInt()

    //Textures
    val DIALOG_MESSAGE = ResourceLocation(Talkative.MOD_ID, "textures/gui/dialog/dialog_message.png")
    val DIALOG_MESSAGE_SPEAKER = ResourceLocation(Talkative.MOD_ID, "textures/gui/dialog/dialog_message_speaker.png")
    val DIALOG_CONTINUE = ResourceLocation(Talkative.MOD_ID, "textures/gui/dialog/dialog_continue.png")
    val DIALOG_REPLY = ResourceLocation(Talkative.MOD_ID, "textures/gui/dialog/dialog_reply.png")
    val EDITOR_WIDGETS = ResourceLocation(Talkative.MOD_ID, "textures/gui/editor/editor_widgets.png")

    //Icons
    val ATTACH_ICON = IconButton.Icon(ResourceLocation(Talkative.MOD_ID, "textures/gui/icons/attach.png"), 20, 20, 0, 0, 20, 40, textureWidth = 20, textureHeight = 60)
    val CONDITIONAL_ICON = IconButton.Icon(ResourceLocation(Talkative.MOD_ID, "textures/gui/icons/conditional.png"), 20, 20, 0, 0, 20, 40, textureWidth = 20, textureHeight = 60)
    val DELETE_ICON = IconButton.Icon(ResourceLocation(Talkative.MOD_ID, "textures/gui/icons/delete.png"), 20, 20, 0, 0, 20, 40, textureWidth = 20, textureHeight = 60)
    val DETACH_ICON = IconButton.Icon(ResourceLocation(Talkative.MOD_ID, "textures/gui/icons/detach.png"), 20, 20, 0, 0, 20, 40, textureWidth = 20, textureHeight = 60)
    val EDIT_ICON = IconButton.Icon(ResourceLocation(Talkative.MOD_ID, "textures/gui/icons/edit.png"), 20, 20, 0, 0, 20, 40, textureWidth = 20, textureHeight = 60)
    val SM_ICON = IconButton.Icon(ResourceLocation(Talkative.MOD_ID, "textures/gui/icons/simple_museum_icon.png"), 64, 64, textureWidth = 64, textureHeight = 64)
}