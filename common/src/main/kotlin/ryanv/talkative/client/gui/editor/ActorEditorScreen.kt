package ryanv.talkative.client.gui.editor

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.gui.components.Button
import net.minecraft.network.chat.TextComponent
import ryanv.talkative.client.gui.TalkativeScreen
import ryanv.talkative.client.gui.popup.PopupWidget
import ryanv.talkative.common.data.Actor

class ActorEditorScreen(val actor: Actor? = null): TalkativeScreen(null, TextComponent("Actor Editor")) {

    override fun init() {
        addButton(Button(10, height - 30, 75, 20, TextComponent("Add Branch")) {
            popup = PopupWidget((width / 2) - 200, (height / 2) - 100, 400, 200, this)
                .title(TextComponent("Add Branch"))
                .label(0, 11, "Branch File Location:")
                .button(235, 5, "...", width = 20) {
                    minecraft?.setScreen(BranchDirectoryScreen(this) {
                        popup!!.getAllTextFields()[0].value = it?.value
                    })
                }
                .textField(110, 5)
                .button(0, 180, "Cancel") { closePopup() }
                .button(350, 180, "Confirm") {
                    popup!!.getAllTextFields().forEach {
                        println()
                    }
                }
        })
        populateBranchList()
    }

    fun populateBranchList() {
        if(actor != null) {
            var i = 0
            actor.dialogBranches.forEach {
                addButton(Button(0, i++ * 20, 50, 20, TextComponent("Branch $i")) {
                    println(i)
                })
            }
        }
    }

    override fun render(poseStack: PoseStack?, mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground(poseStack)
        super.render(poseStack, mouseX, mouseY, delta)
    }

    override fun onMouseClick(mouseX: Double, mouseY: Double, mouseButton: Int): Boolean {
        return false
    }

    override fun onMouseDrag(mouseX: Double, mouseY: Double, mouseButton: Int, distanceX: Double, distanceY: Double): Boolean {
        return false
    }

    override fun onMouseRelease(mouseX: Double, mouseY: Double, mouseButton: Int): Boolean {
        return false
    }

    override fun onMouseScroll(mouseX: Double, mouseY: Double, scrollAmount: Double): Boolean {
        return false
    }

    override fun onKeyPressed(keyCode: Int, j: Int, k: Int): Boolean {
        return false
    }

    override fun onCharTyped(char: Char, i: Int): Boolean {
        return false
    }

    override fun renderBackground(poseStack: PoseStack?) {
        fill(poseStack, 0, 0, width, height, -1072689136)
    }

}