package dev.cryptcraft.talkative.client.gui.dialog

import com.mojang.blaze3d.platform.Lighting
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.*
import com.mojang.math.Matrix4f
import com.mojang.math.Vector3f
import dev.cryptcraft.talkative.api.actor.DisplayData
import dev.cryptcraft.talkative.client.gui.TalkativeScreen
import dev.cryptcraft.talkative.client.gui.dialog.widgets.DialogList
import dev.cryptcraft.talkative.client.gui.dialog.widgets.ResponseList
import dev.cryptcraft.talkative.common.network.clientbound.DialogPacket
import dev.cryptcraft.talkative.common.network.serverbound.ExitConversationPacket
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.client.renderer.entity.EntityRenderDispatcher
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.LivingEntity
import org.lwjgl.glfw.GLFW


class DialogScreen : TalkativeScreen(null, Component.literal("Conversation Screen")) {
    val dialogList = DialogList(this, 0, 0, 0, 0)
    val responseList = ResponseList(this, 0, 0, 0, 0)

    var actorEntity: LivingEntity? = null
    var displayData: DisplayData? = null

    override fun init() {
        super.init()
        val responseBoxHeight = (height * .3).toInt()
        val dialogListHeight = height - responseBoxHeight - 10

        dialogList.x = (width * .25).toInt()
        dialogList.width = width / 2
        dialogList.maxHeight = dialogListHeight
        dialogList.setBottom(dialogListHeight)
        addRenderableWidget(dialogList)

        responseList.x = (width * .25).toInt()
        responseList.y = height - responseBoxHeight + 4
        responseList.width = width / 2
        responseList.height = responseBoxHeight - 3
        addRenderableWidget(responseList)
    }

    fun receiveDialog(dialogLines: List<Component>, responses: ArrayList<DialogPacket.ResponseData>?, exitNode: Boolean) {
        dialogList.addEntry(dialogLines)
        responseList.clear()
        if (!responses.isNullOrEmpty())
            responses.forEach(responseList::addEntry)
    }

    override fun render(poseStack: PoseStack, mouseX: Int, mouseY: Int, delta: Float) {
        fill(poseStack, 0, 0, width, height, 0xD91c1c1c.toInt()) //Background

        renderPlayer()
        renderActor(poseStack)

        //Responses Section - Rendered above Entities
        val responseHeight = height - (height * .3).toInt()
        val fadeHeight = height - (height * .35).toInt()

        fillGradient(poseStack, 0, fadeHeight, width, responseHeight, 0x001c1c1c, 0xD9141414.toInt())
        fill(poseStack, 0, responseHeight, width, height, 0xD9141414.toInt())

        val separatorY = responseHeight - 2
        horizontalGradient(poseStack, (width * .1).toInt(), separatorY, (width * .5).toInt(), separatorY + 1, 0x00FFFFFF, 0xFFFFFFFF.toInt())
        horizontalGradient(poseStack, (width * .5).toInt(), separatorY, width - (width * .1).toInt(), separatorY + 1, 0xFFFFFFFF.toInt(), 0x00FFFFFF)

        super.render(poseStack, mouseX, mouseY, delta)
    }

    private fun renderPlayer() {
        val player = Minecraft.getInstance().player
        renderEntity(player as LivingEntity, (width / 8).toDouble(), (height - (height / 6)).toDouble(), false)
    }

    private fun renderActor(poseStack: PoseStack) {
        if (actorEntity == null || !displayData!!.displayInConversation) return

        if (displayData!!.displayAsEntity) {
            renderEntity(actorEntity!!, (width -  (width / 8)).toDouble(), (height - (height / 6)).toDouble(), true)
        }
        else {
            renderTexture(poseStack, displayData!!.displayTexture, width -  (width / 8), height - (height / 2), displayData!!.textureSize?.get(0) ?: 0, displayData!!.textureSize?.get(1) ?: 0)
        }
    }

    private fun renderEntity(entity: LivingEntity, posX: Double, posY: Double, flip: Boolean) {
        val poseStack = RenderSystem.getModelViewStack()
        poseStack.pushPose()

        poseStack.translate(posX, posY, 1050.0) //z - 1050.0 magic num
        poseStack.scale(1f, 1f, -1f)
        RenderSystem.applyModelViewMatrix()

        val poseStack2 = PoseStack()
        poseStack2.translate(0.0, 0.0, 1000.0)
        poseStack2.scale(height.toFloat() / 3, height.toFloat() / 3, 100f)

        val rot = if (flip) -45f else 0f
        val quaternion = Vector3f.ZP.rotationDegrees(180f)
        val quaternion2 = Vector3f.YP.rotationDegrees(rot)
        quaternion.mul(quaternion2)
        poseStack2.mulPose(quaternion)

        val oldBodyRot: Float = entity.yBodyRot
        val oldYRot: Float = entity.yRot
        val oldXRot: Float = entity.xRot
        val oldYHead0: Float = entity.yHeadRotO
        val oldYHeadRot: Float = entity.yHeadRot

        entity.yBodyRot = 160f
        entity.yRot = 160.0f
        entity.xRot = 0f
        entity.yHeadRot = entity.yRot
        entity.yHeadRotO = entity.yRot

        val light = Vector3f.ZP.copy()
        val lightRot = if (flip) 220f else 120f
        light.transform(Vector3f.YP.rotationDegrees(lightRot))
        light.transform(Vector3f.XN.rotationDegrees(45f))

        val matrix = Matrix4f()
        matrix.setIdentity()

        RenderSystem.setShaderLights(light, Vector3f.ZERO)

        val renderDispatcher: EntityRenderDispatcher = Minecraft.getInstance().entityRenderDispatcher
        val bufferSource = Minecraft.getInstance().renderBuffers().bufferSource()

        quaternion2.conj()
        renderDispatcher.overrideCameraOrientation(quaternion2)
        renderDispatcher.setRenderShadow(false)
        RenderSystem.runAsFancy {
            renderDispatcher.render(entity, 0.0, 0.0, 0.0, 0f, 1f, poseStack2, bufferSource, 0xF000F0)
        }
        bufferSource.endBatch()
        renderDispatcher.setRenderShadow(true)

        entity.yBodyRot = oldBodyRot
        entity.yRot = oldYRot
        entity.xRot = oldXRot
        entity.yHeadRotO = oldYHead0
        entity.yHeadRot = oldYHeadRot

        poseStack.popPose()
        RenderSystem.applyModelViewMatrix()
        Lighting.setupFor3DItems()
    }

    private fun renderTexture(poseStack: PoseStack, locationIn: ResourceLocation?, posX: Int, posY: Int, texWidth: Int, texHeight: Int) {
        val location = locationIn ?: ResourceLocation("")

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
        RenderSystem.setShaderTexture(0, location)
        RenderSystem.setShader(GameRenderer::getPositionTexShader)

        val bufferBuilder = Tesselator.getInstance().builder
        val matrix = poseStack.last().pose()

//        val scale = ((height - (height / 3)) / texHeight) / minecraft!!.window.guiScale //If we want to use GuiScale? Not sure yet
        val scale = ((height - (height / 3)) / texHeight)
        val xOff = (texWidth * scale) / 2
        val yOff = (texHeight * scale) / 2

        val x1 = posX - xOff
        val x2 = posX + xOff
        val y1 = posY + yOff
        val y2 = posY - yOff

        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX)
        bufferBuilder.vertex(matrix, x1.toFloat(), y1.toFloat(), blitOffset.toFloat()).uv(0f, 1f).endVertex()
        bufferBuilder.vertex(matrix, x2.toFloat(), y1.toFloat(), blitOffset.toFloat()).uv(1f, 1f).endVertex()
        bufferBuilder.vertex(matrix, x2.toFloat(), y2.toFloat(), blitOffset.toFloat()).uv(1f, 0f).endVertex()
        bufferBuilder.vertex(matrix, x1.toFloat(), y2.toFloat(), blitOffset.toFloat()).uv(0f, 0f).endVertex()

        BufferUploader.drawWithShader(bufferBuilder.end())
    }

    override fun onKeyPressed(keyCode: Int, j: Int, k: Int): Boolean {
        if (keyCode in GLFW.GLFW_KEY_1..GLFW.GLFW_KEY_9) {
            val num = keyCode - GLFW.GLFW_KEY_1
            if (num < responseList.children.size)
                (responseList.children[num] as ResponseList.ResponseListEntry).onResponse()
            return true
        }
        return false
    }

    fun horizontalGradient(poseStack: PoseStack, x1: Int, y1: Int, x2: Int, y2: Int, colorFrom: Int, colorTo: Int) {
        RenderSystem.disableTexture()
        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()
        RenderSystem.setShader { GameRenderer.getPositionColorShader() }

        val tesselator = Tesselator.getInstance()
        val bufferBuilder = tesselator.builder

        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR)
        horizontalGradient(poseStack.last().pose(), bufferBuilder, x1, y1, x2, y2, 0, colorFrom, colorTo)
        tesselator.end()

        RenderSystem.disableBlend()
        RenderSystem.enableTexture()
    }

    private fun horizontalGradient(matrix: Matrix4f, builder: BufferBuilder, x1: Int, y1: Int, x2: Int, y2: Int, blitOffset: Int, colorA: Int, colorB: Int) {
        val alphaA = (colorA shr 24 and 0xFF).toFloat() / 255.0f
        val redA = (colorA shr 16 and 0xFF).toFloat() / 255.0f
        val greenA = (colorA shr 8 and 0xFF).toFloat() / 255.0f
        val blueA = (colorA and 0xFF).toFloat() / 255.0f

        val alphaB = (colorB shr 24 and 0xFF).toFloat() / 255.0f
        val redB = (colorB shr 16 and 0xFF).toFloat() / 255.0f
        val greenB = (colorB shr 8 and 0xFF).toFloat() / 255.0f
        val blueB = (colorB and 0xFF).toFloat() / 255.0f

        builder.vertex(matrix, x1.toFloat(), y1.toFloat(), blitOffset.toFloat()).color(redA, greenA, blueA, alphaA).endVertex()
        builder.vertex(matrix, x1.toFloat(), y2.toFloat(), blitOffset.toFloat()).color(redA, greenA, blueA, alphaA).endVertex()
        builder.vertex(matrix, x2.toFloat(), y2.toFloat(), blitOffset.toFloat()).color(redB, greenB, blueB, alphaB).endVertex()
        builder.vertex(matrix, x2.toFloat(), y1.toFloat(), blitOffset.toFloat()).color(redB, greenB, blueB, alphaB).endVertex()
    }

    override fun onClose() {
        ExitConversationPacket().sendToServer()
        super.onClose()
    }
}