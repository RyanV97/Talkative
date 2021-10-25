package ryanv.talkative.client.gui

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.TextComponent

class ActorEditorScreen: Screen(TextComponent("Actor Editor")) {

    override fun init() {

    }

    override fun render(poseStack: PoseStack?, i: Int, j: Int, f: Float) {
        renderBackground(poseStack)
        super.render(poseStack, i, j, f)
    }

    override fun renderBackground(poseStack: PoseStack?) {
        fill(poseStack, 0, 0, width, height, -1072689136)
    }

}