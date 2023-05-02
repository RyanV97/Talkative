package ryanv.talkative.client.gui

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import ryanv.talkative.client.gui.widgets.popup.PopupWidget
import ryanv.talkative.client.gui.widgets.SubmenuWidget

abstract class TalkativeScreen(var parent: Screen?, title: Component?) : Screen(title) {
    var popup: PopupWidget? = null
    var submenu: SubmenuWidget? = null

    private var centerX: Int = 0
    private var centerY: Int = 0

    init {
        isDragging = true
    }

    override fun init() {
        super.init()
        centerX = width / 2
        centerY = height /2
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
        if (popup == null || !popup!!.keyPressed(keyCode, j, k))
            if (submenu == null || !submenu!!.keyPressed(keyCode, j, k))
                if (!onKeyPressed(keyCode, j, k))
                    return super.keyPressed(keyCode, j, k)
        return true
    }

    override fun charTyped(char: Char, i: Int): Boolean {
        if (popup == null || !popup!!.charTyped(char, i))
            if (submenu == null || !submenu!!.charTyped(char, i))
                if (!onCharTyped(char, i))
                    return super.charTyped(char, i)
        return true
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, mouseButton: Int): Boolean {
        if (popup == null || !popup!!.mouseClicked(mouseX, mouseY, mouseButton))
            if (submenu == null || !submenu!!.mouseClicked(mouseX, mouseY, mouseButton)) {
                closeSubmenu()
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
        if (popup == null || !popup!!.mouseReleased(mouseX, mouseY, mouseButton))
            if (submenu == null || !submenu!!.mouseReleased(mouseX, mouseY, mouseButton)) {
                if (!onMouseRelease(mouseX, mouseY, mouseButton))
                    getChildAt(mouseX, mouseY).filter { guiEventListener ->
                        guiEventListener.mouseReleased(mouseX, mouseY, mouseButton)
                    }
            }
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

    fun closeSubmenu() {
        submenu = null
    }

    open fun onKeyPressed(keyCode: Int, j: Int, k: Int): Boolean {
        return false
    }

    open fun onCharTyped(char: Char, i: Int): Boolean {
        return false
    }

    open fun onMouseClick(mouseX: Double, mouseY: Double, mouseButton: Int): Boolean {
        return false
    }

    open fun onMouseDrag(mouseX: Double, mouseY: Double, mouseButton: Int, distanceX: Double, distanceY: Double): Boolean {
        return false
    }

    open fun onMouseRelease(mouseX: Double, mouseY: Double, mouseButton: Int): Boolean {
        return false
    }

    open fun onMouseScroll(mouseX: Double, mouseY: Double, scrollAmount: Double): Boolean {
        return false
    }
}