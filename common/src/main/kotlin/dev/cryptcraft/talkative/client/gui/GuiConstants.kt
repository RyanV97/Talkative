package dev.cryptcraft.talkative.client.gui

import dev.cryptcraft.talkative.Talkative
import net.minecraft.resources.ResourceLocation

object GuiConstants {
    //Colors
    const val COLOR_BG = 0xFF0e0e0e.toInt()
    const val COLOR_BTN_BG = 0xFF353c48.toInt()
    const val COLOR_BTN_BORDER = 0xFF238b4e.toInt()
    const val COLOR_BTN_BORDER_HL = 0xFF3ecf7d.toInt()

    //Textures
    val BUTTON_ICONS = ResourceLocation(Talkative.MOD_ID, "textures/gui/button_icons.png")
    val DIALOG_WIDGETS = ResourceLocation(Talkative.MOD_ID, "textures/gui/dialog_widgets.png")
    val EDITOR_WIDGETS = ResourceLocation(Talkative.MOD_ID, "textures/gui/editor_widgets.png")
    val SM_ICON = ResourceLocation(Talkative.MOD_ID, "textures/gui/simple_museum_icon.png")
}