package ryanv.talkative.client.gui.editor.tabs

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiComponent
import net.minecraft.network.chat.Component
import ryanv.talkative.api.ActorData
import ryanv.talkative.client.gui.editor.ActorEditorScreen
import ryanv.talkative.client.gui.widgets.NestedWidget
import ryanv.talkative.common.data.ServerActorData

abstract class EditorTab(x: Int, y: Int, width: Int, height: Int, var actorData: ActorData, val parentScreen: ActorEditorScreen, title: Component) : NestedWidget(x, y, width, height, title) {

    override fun renderButton(poseStack: PoseStack, mouseX: Int, mouseY: Int, partialTicks: Float) {
        GuiComponent.drawCenteredString(poseStack, Minecraft.getInstance().font, message, x + (width / 2), y, 0xFFFFFF)
        super.renderButton(poseStack, mouseX, mouseY, partialTicks)
    }

    open fun refresh(newData: ServerActorData) {
        actorData = parentScreen.actorData
    }

}