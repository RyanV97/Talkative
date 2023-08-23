package dev.cryptcraft.talkative.client

import com.mojang.blaze3d.vertex.PoseStack
import dev.cryptcraft.talkative.api.actor.markers.MarkerEntity
import net.minecraft.client.Minecraft
import net.minecraft.client.model.EntityModel
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.Sheets
import net.minecraft.client.renderer.block.model.ItemTransforms
import net.minecraft.client.renderer.entity.LivingEntityRenderer
import net.minecraft.client.renderer.entity.layers.RenderLayer
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.world.entity.LivingEntity

class MarkerLayer<T: LivingEntity, M: EntityModel<T>>(livingEntityRenderer: LivingEntityRenderer<T, M>) : RenderLayer<T, M>(livingEntityRenderer) {
    override fun render(poseStack: PoseStack, buffer: MultiBufferSource, packedLight: Int, entity: T, limbSwing: Float, limbSwingAmount: Float, partialTick: Float, ageInTicks: Float, netHeadYaw: Float, headPitch: Float) {
        val marker = (entity as MarkerEntity).getMarker() ?: return
        val itemRenderer = Minecraft.getInstance().itemRenderer
        val modelManager = itemRenderer.itemModelShaper.modelManager
        val model = modelManager.getModel(marker.modelLocation)
        val buf = buffer.getBuffer(Sheets.translucentItemSheet())

        poseStack.pushPose()
        poseStack.translate(0.0, 1.501, 0.0) //Magic Number to reset to feet
        poseStack.scale(1.0f, -1.0f, -1.0f) //Reverse some scales to make things normal

        poseStack.translate(0.0, entity.bbHeight.toDouble(), 0.0) //Adjust to top of bounding box height
        poseStack.translate(0.0, .25, 0.0) //Small adjustment just 'cus

        poseStack.scale(.5f, .5f, .5f) //Scale down a touch
        if (marker.positionOffset != null) //If an offset has been specified, apply that
            poseStack.translate(marker.positionOffset!!.x().toDouble(), marker.positionOffset!!.y().toDouble(), marker.positionOffset!!.z().toDouble())

        MarkerRenderer.renderModel(model, ItemTransforms.TransformType.NONE, poseStack, buf, null, marker.overlayColour, packedLight, OverlayTexture.NO_OVERLAY) //Render Marker

        poseStack.popPose()
    }
}