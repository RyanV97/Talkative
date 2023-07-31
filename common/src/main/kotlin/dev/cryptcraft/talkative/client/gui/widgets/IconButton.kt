package dev.cryptcraft.talkative.client.gui.widgets

import net.minecraft.client.gui.components.ImageButton
import net.minecraft.resources.ResourceLocation

open class IconButton(x: Int, y: Int, width: Int, height: Int, icon: Icon, onPress: OnPress) : ImageButton(x, y, width, height, icon.offsetX, icon.offsetY, icon.height, icon.location, icon.textureWidth, icon.textureHeight, onPress) {
    data class Icon(val location: ResourceLocation, val width: Int, val height: Int, val offsetX: Int = 0, val offsetY: Int = 0, val textureWidth: Int = 256, val textureHeight: Int = 256)
}