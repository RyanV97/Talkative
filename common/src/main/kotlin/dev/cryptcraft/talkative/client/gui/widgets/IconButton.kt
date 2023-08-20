package dev.cryptcraft.talkative.client.gui.widgets

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.gui.components.Button
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation

open class IconButton(x: Int, y: Int, width: Int, height: Int, private val icon: Icon, onPress: OnPress, onTooltip: OnTooltip) : Button(x, y, width, height, Component.empty(), onPress, onTooltip) {
    constructor(x: Int, y: Int, width: Int, height: Int, icon: Icon, onPress: OnPress) : this(x, y, width, height, icon, onPress, NO_TOOLTIP)

    override fun renderButton(poseStack: PoseStack, mouseX: Int, mouseY: Int, partialTick: Float) {
        RenderSystem.setShader { GameRenderer.getPositionTexShader() }
        RenderSystem.setShaderTexture(0, icon.location)
        val offsetY = if (!isActive) icon.disabledOffsetY else if (isHoveredOrFocused) icon.hoverOffsetY else icon.offsetY
        RenderSystem.enableDepthTest()
        blit(poseStack, x, y, width, height, icon.offsetX.toFloat(), offsetY.toFloat(), icon.width, icon.height, icon.textureWidth, icon.textureHeight)
        if (isHovered)
            renderToolTip(poseStack, mouseX, mouseY)
    }

    fun setHeight(height: Int) {
        this.height = height
    }

    data class Icon(val location: ResourceLocation, val width: Int, val height: Int, val offsetX: Int = 0, val offsetY: Int = 0, val hoverOffsetY: Int = offsetY, val disabledOffsetY: Int = offsetY, val textureWidth: Int = 256, val textureHeight: Int = 256)
}