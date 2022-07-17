package ryanv.talkative.client.gui

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.gui.components.events.GuiEventListener
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import ryanv.talkative.client.gui.popup.PopupWidget
import ryanv.talkative.client.gui.widgets.SubmenuWidget

abstract class TalkativeScreen(var parent: Screen?, title: Component?) : Screen(title) {

    var popup: PopupWidget? = null
    var submenu: SubmenuWidget? = null

    init {
        isDragging = true
    }

    override fun render(poseStack: PoseStack?, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(poseStack, mouseX, mouseY, delta)
        submenu?.render(poseStack, mouseX, mouseY, delta)
        if(popup != null) {
            renderBackground(poseStack)
            popup!!.render(poseStack, mouseX, mouseY, delta)
        }
    }

    override fun keyPressed(keyCode: Int, j: Int, k: Int): Boolean {
        if(popup == null || !popup!!.keyPressed(keyCode, j, k))
            if(submenu == null || !submenu!!.keyPressed(keyCode, j, k))
                    if(!onKeyPressed(keyCode, j, k))
                        return super.keyPressed(keyCode, j, k)
        return true
    }

    override fun charTyped(char: Char, i: Int): Boolean {
        if(popup == null || !popup!!.charTyped(char, i))
            if(submenu == null || !submenu!!.charTyped(char, i))
                if(!onCharTyped(char, i))
                    return super.charTyped(char, i)
        return true
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, mouseButton: Int): Boolean {
        if(popup == null || !popup!!.mouseClicked(mouseX, mouseY, mouseButton))
            if(submenu == null || !submenu!!.mouseClicked(mouseX, mouseY, mouseButton)) {
                submenu = null
                if (!onMouseClick(mouseX, mouseY, mouseButton))
                    if (!super.mouseClicked(mouseX, mouseY, mouseButton))
                        return false
            }
        return true
    }

    override fun mouseDragged(mouseX: Double, mouseY: Double, mouseButton: Int, distanceX: Double, distanceY: Double): Boolean {
        //Check Popup
        onMouseDrag(mouseX, mouseY, mouseButton, distanceX, distanceY)
        return super.mouseDragged(mouseX, mouseY, mouseButton, distanceX, distanceY)
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, mouseButton: Int): Boolean {
        //Check Popup
        onMouseRelease(mouseX, mouseY, mouseButton)
        getChildAt(mouseX, mouseY).filter { guiEventListener: GuiEventListener ->
            guiEventListener.mouseReleased(mouseX, mouseY, mouseButton)
        }.isPresent
        isDragging = false
        return true
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, scrollAmount: Double): Boolean {
        //Check Popup
        onMouseScroll(mouseX, mouseY, scrollAmount)
        return super.mouseScrolled(mouseX, mouseY, scrollAmount)
    }

    fun closePopup() {
        popup = null
    }

    abstract fun onKeyPressed(keyCode: Int, j: Int, k: Int): Boolean
    abstract fun onCharTyped(char: Char, i: Int): Boolean
    abstract fun onMouseClick(mouseX: Double, mouseY: Double, mouseButton: Int): Boolean
    abstract fun onMouseDrag(mouseX: Double, mouseY: Double, mouseButton: Int, distanceX: Double, distanceY: Double): Boolean
    abstract fun onMouseRelease(mouseX: Double, mouseY: Double, mouseButton: Int): Boolean
    abstract fun onMouseScroll(mouseX: Double, mouseY: Double, scrollAmount: Double): Boolean

}