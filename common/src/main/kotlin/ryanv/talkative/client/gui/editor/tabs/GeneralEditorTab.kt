package ryanv.talkative.client.gui.editor.tabs

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.Checkbox
import net.minecraft.client.gui.components.EditBox
import net.minecraft.network.chat.TextComponent
import ryanv.talkative.api.ActorData
import ryanv.talkative.client.gui.editor.ActorEditorScreen

class GeneralEditorTab(x: Int, y: Int, width: Int, height: Int, actor: ActorData, parent: ActorEditorScreen) :
    EditorTab(x, y, width, height, actor, parent, TextComponent("General Actor Settings")) {
    val overrideDisplayName: Checkbox = addChild(Checkbox(0,0, 20, 20, TextComponent("Override Entity Name"), actor.shouldOverrideDisplayName()))
    val actorDisplayName: EditBox = addChild(EditBox(Minecraft.getInstance().font, 0,0, 150, 20, TextComponent("Actor Display Name")))

    init {
        recalculateChildren()
    }

    override fun renderButton(poseStack: PoseStack, mouseX: Int, mouseY: Int, partialTicks: Float) {
        actorDisplayName.setEditable(overrideDisplayName.selected())
        super.renderButton(poseStack, mouseX, mouseY, partialTicks)
    }

    override fun recalculateChildren() {
        overrideDisplayName.x = x + 9
        overrideDisplayName.y = y + 15

        actorDisplayName.x = x + 10
        actorDisplayName.y = y + 40

        super.recalculateChildren()
    }
}