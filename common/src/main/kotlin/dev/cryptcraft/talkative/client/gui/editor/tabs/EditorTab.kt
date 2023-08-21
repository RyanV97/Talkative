package dev.cryptcraft.talkative.client.gui.editor.tabs

import com.mojang.blaze3d.vertex.PoseStack
import dev.cryptcraft.talkative.client.gui.editor.MainEditorScreen
import dev.cryptcraft.talkative.client.gui.widgets.NestedWidget
import net.minecraft.network.chat.Component

abstract class EditorTab(x: Int, y: Int, width: Int, height: Int, val parentScreen: MainEditorScreen, title: Component) : NestedWidget(x, y, width, height, title) {
    abstract fun onClose()
    abstract fun refresh()

    override fun renderButton(poseStack: PoseStack, mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.renderButton(poseStack, mouseX, mouseY, partialTicks)
    }
}