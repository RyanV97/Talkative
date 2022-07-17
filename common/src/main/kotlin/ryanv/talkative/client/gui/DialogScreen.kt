package ryanv.talkative.client.gui

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.TextComponent

class DialogScreen: Screen(TextComponent("NPC Dialog")) {

    override fun init() {
        super.init()
    }

    override fun render(poseStack: PoseStack?, mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground(poseStack)
        super.render(poseStack, mouseX, mouseY, delta)
    }

    override fun renderBackground(poseStack: PoseStack?) {
        fill(poseStack, 0, 0,width,height, -1072689136)
        fill(poseStack, 0, (height / 3) * 2, width, height, 0x33FFFFFF.toInt())
        fill(poseStack, width / 5, 0, width - (width / 5), height, 0x33FFFFFF.toInt())
    }

}