package dev.cryptcraft.talkative.client.gui.dialog

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.*
import com.mojang.math.Matrix4f
import dev.cryptcraft.talkative.client.gui.TalkativeScreen
import dev.cryptcraft.talkative.client.gui.dialog.widgets.DialogList
import dev.cryptcraft.talkative.client.gui.dialog.widgets.ResponseList
import dev.cryptcraft.talkative.common.network.clientbound.DialogPacket
import dev.cryptcraft.talkative.common.network.serverbound.ExitConversationPacket
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.network.chat.Component
import org.lwjgl.glfw.GLFW

class DialogScreen : TalkativeScreen(null, Component.literal("Conversation Screen")) {
    private val dialogList = DialogList(this, 0, 0, 0, 0)
    private val responseList = ResponseList(this, 0, 0, 0, 0)

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
        responseList.y = height - responseBoxHeight
        responseList.width = width / 2
        responseList.height = responseBoxHeight
        addRenderableWidget(responseList)
    }

    fun receiveDialog(dialogLine: Component, responses: ArrayList<DialogPacket.ResponseData>?, exitNode: Boolean) {
        dialogList.addEntry(dialogLine)
        responseList.clear()
        if (!responses.isNullOrEmpty())
            responses.forEach(responseList::addEntry)
//        else
//            if (exitNode)
//                ToDo Add Exit Button
//            else
//                ToDo Add Continue Arrow
    }

    override fun render(poseStack: PoseStack?, mouseX: Int, mouseY: Int, delta: Float) {
        fill(poseStack!!, 0, 0, width, height, 0xD91c1c1c.toInt()) //Background

        //Responses Section - Rendered above Entities
        val responseHeight = height - (height * .3).toInt()
        val fadeHeight = height - (height * .35).toInt()

        fillGradient(poseStack, 0, fadeHeight, width, responseHeight, 0x001c1c1c, 0xD9141414.toInt())
        fill(poseStack, 0, responseHeight, width, height, 0xD9141414.toInt())

        val separatorY = responseHeight - 1
        horizontalGradient(poseStack, (width * .1).toInt(), separatorY, (width * .5).toInt(), separatorY + 1, 0x00FFFFFF, 0xFFFFFFFF.toInt())
        horizontalGradient(poseStack, (width * .5).toInt(), separatorY, width - (width * .1).toInt(), separatorY + 1, 0xFFFFFFFF.toInt(), 0x00FFFFFF)

        super.render(poseStack, mouseX, mouseY, delta)
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

    fun horizontalGradient(matrix: Matrix4f, builder: BufferBuilder, x1: Int, y1: Int, x2: Int, y2: Int, blitOffset: Int, colorA: Int, colorB: Int) {
        val f = (colorA shr 24 and 0xFF).toFloat() / 255.0f
        val g = (colorA shr 16 and 0xFF).toFloat() / 255.0f
        val h = (colorA shr 8 and 0xFF).toFloat() / 255.0f
        val i = (colorA and 0xFF).toFloat() / 255.0f
        val j = (colorB shr 24 and 0xFF).toFloat() / 255.0f
        val k = (colorB shr 16 and 0xFF).toFloat() / 255.0f
        val l = (colorB shr 8 and 0xFF).toFloat() / 255.0f
        val m = (colorB and 0xFF).toFloat() / 255.0f
        builder.vertex(matrix, x1.toFloat(), y1.toFloat(), blitOffset.toFloat()).color(g, h, i, f).endVertex()
        builder.vertex(matrix, x1.toFloat(), y2.toFloat(), blitOffset.toFloat()).color(g, h, i, f).endVertex()
        builder.vertex(matrix, x2.toFloat(), y2.toFloat(), blitOffset.toFloat()).color(k, l, m, j).endVertex()
        builder.vertex(matrix, x2.toFloat(), y1.toFloat(), blitOffset.toFloat()).color(k, l, m, j).endVertex()
    }

    override fun onClose() {
        ExitConversationPacket().sendToServer()
        super.onClose()
    }
}