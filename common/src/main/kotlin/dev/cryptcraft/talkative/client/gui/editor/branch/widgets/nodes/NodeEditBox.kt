package dev.cryptcraft.talkative.client.gui.editor.branch.widgets.nodes

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.MultiLineEditBox
import net.minecraft.network.chat.Component
import dev.cryptcraft.talkative.mixin.client.AbstractScrollWidgetAccessor

class NodeEditBox(val parentWidget: NodeWidget, x: Int, y: Int, width: Int, height: Int, placeholder: Component = Component.empty(), message: Component = Component.literal("Edit Box")) : MultiLineEditBox(Minecraft.getInstance().font, x, y, width, height, placeholder, message) {
    override fun isMouseOver(mouseX: Double, mouseY: Double): Boolean {
        return active && visible && withinContentAreaPoint(mouseX, mouseY)
    }

    override fun renderButton(poseStack: PoseStack, mouseX: Int, mouseY: Int, partialTick: Float) {
        if (!visible)
            return

        val zoom = parentWidget.parentScreen.zoomScale
        enableScissor(((x + 1) * zoom).toInt(), ((y + 1 ) * zoom).toInt(), ((x + width - 1) * zoom).toInt(), ((y + height - 1) * zoom).toInt())
        poseStack.pushPose()
        poseStack.translate(0.0, -(this as AbstractScrollWidgetAccessor).scrollAmount, 0.0)
        renderContents(poseStack, mouseX, mouseY, partialTick)
        poseStack.popPose()
        disableScissor()

        //ToDo: This doesn't scale
//        if (parentWidget.parentScreen.selectedNode == parentWidget)
//            renderDecorations(poseStack)
    }

    override fun withinContentAreaTopBottom(top: Int, bottom: Int): Boolean {
        val scrollAmount = (this as AbstractScrollWidgetAccessor).scrollAmount
        return bottom.toDouble() - scrollAmount >= y.toDouble() && top.toDouble() - scrollAmount <= (y + height).toDouble()
    }

    override fun withinContentAreaPoint(x: Double, y: Double): Boolean {
        val scaledX = x / parentWidget.parentScreen.zoomScale
        val scaledY = y / parentWidget.parentScreen.zoomScale

        val posX = this.x + parentWidget.parentScreen.offsetX
        val posY = this.y + parentWidget.parentScreen.offsetY

        return scaledX > posX - 1 && scaledY > posY - 1 && scaledX < posX + width + 1 && scaledY < posY + height + 1
    }
}