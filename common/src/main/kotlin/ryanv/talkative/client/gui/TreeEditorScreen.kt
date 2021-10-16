package ryanv.talkative.client.gui

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.TextComponent

class TreeEditorScreen: Screen(TextComponent("Dialog Tree Editor")) {

    var guiLeft: Int = 0
    var guiTop: Int = 0

    var guiWidth = 150
    var guiHeight = 150

    override fun init() {
        guiLeft = (width / 2) - (guiWidth / 2)
        guiTop = (height / 2) - (guiHeight / 2)
    }

    override fun render(poseStack: PoseStack?, i: Int, j: Int, f: Float) {
        fill(poseStack, guiLeft, guiTop, guiLeft + guiWidth, guiTop + guiHeight, 0xFFFFFFFF.toInt())
    }

}