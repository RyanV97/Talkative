package dev.cryptcraft.talkative.client

import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.platform.Lighting
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.math.Quaternion
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.Sheets
import net.minecraft.client.renderer.block.model.ItemTransforms
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.client.resources.model.BakedModel
import net.minecraft.core.Direction
import net.minecraft.util.RandomSource
import net.minecraft.world.inventory.InventoryMenu

object MarkerRenderer {
    private val RAND = RandomSource.create()

    fun renderGuiModel(model: BakedModel, x: Int, y: Int, scale: Float, yRot: Float, colour: Int, blitOffset: Float = Minecraft.getInstance().itemRenderer.blitOffset) {
        Minecraft.getInstance().textureManager.getTexture(InventoryMenu.BLOCK_ATLAS).setFilter(false, false)
        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS)
        RenderSystem.enableBlend()
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA)
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
        val modelView = RenderSystem.getModelViewStack()
        modelView.pushPose()
        modelView.translate(x.toDouble(), y.toDouble(), (100.0f + blitOffset).toDouble())
        modelView.scale(1.0f, -1.0f, 1.0f)
        modelView.scale(16.0f, 16.0f, 16.0f)
        modelView.scale(scale, scale, scale)
        modelView.mulPose(Quaternion.fromXYZ(0f, yRot, 0f))
        RenderSystem.applyModelViewMatrix()

        val poseStack = PoseStack()
        val buffers = Minecraft.getInstance().renderBuffers().bufferSource()
        val nonBlockLight = !model.usesBlockLight()
        if (nonBlockLight) Lighting.setupForFlatItems()

        val buffer = buffers.getBuffer(Sheets.translucentItemSheet())
        renderModel(model, ItemTransforms.TransformType.GUI, poseStack, buffer, null, colour, 15728880, OverlayTexture.NO_OVERLAY)
        model.transforms.getTransform(ItemTransforms.TransformType.GUI).apply(false, poseStack)

        buffers.endBatch()
        RenderSystem.enableDepthTest()
        if (nonBlockLight) Lighting.setupFor3DItems()

        modelView.popPose()
        RenderSystem.applyModelViewMatrix()
    }

    fun renderModel(model: BakedModel, transformType: ItemTransforms.TransformType, poseStack: PoseStack, buffer: VertexConsumer, directions: Array<Direction>?, colour: Int, light: Int, overlay: Int, ) {
        if (model.isCustomRenderer) return

        poseStack.pushPose()
        model.transforms.getTransform(transformType).apply(false, poseStack)
        poseStack.translate(-0.5, -0.5, -0.5)

        val r: Float = (colour shr 16 and 0xFF).toFloat() / 255.0f
        val g: Float = (colour shr 8 and 0xFF).toFloat() / 255.0f
        val b: Float = (colour and 0xFF).toFloat() / 255.0f

        val pose = poseStack.last()
        if (directions != null) {
            for (direction in directions) {
                val quads = model.getQuads(null, direction, RAND)
                for (bakedQuad in quads) buffer.putBulkData(pose, bakedQuad, r, g, b, light, overlay)
            }
        }
        else {
            val quads = model.getQuads(null, null, RAND)
            for (bakedQuad in quads) buffer.putBulkData(pose, bakedQuad, r, g, b, light, overlay)
        }

        poseStack.popPose()
    }
}