package ryanv.talkative.client.gui.widgets

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.components.EditBox
import net.minecraft.client.sounds.SoundManager
import net.minecraft.network.chat.Component

//Original Code provided by DenimRed - https://github.com/DenimRed/
open class NestedWidget(x: Int, y: Int, width: Int, height: Int, title: Component?) :
    AbstractWidget(x, y, width, height, title) {
    val children: MutableList<AbstractWidget> = ArrayList()
    protected var maxWidth = Int.MAX_VALUE
        set(value) {
            field = value
            setWidth(width)
        }

    protected fun <T : AbstractWidget> addChild(child: T): T {
        return this.addChild(child, false)
    }

    protected fun <T : AbstractWidget> addChild(child: T, reverseOrder: Boolean): T {
        if (reverseOrder) {
            children.add(0, child)
        } else {
            children.add(child)
        }
        if (child is NestedWidget)
            child.recalculateChildren()
        return child
    }

    protected fun removeChild(child: AbstractWidget): Boolean {
        return children.remove(child)
    }

    protected fun clearChildren() {
        children.clear()
    }

    protected fun swapChild(oldChild: AbstractWidget, newChild: AbstractWidget) {
        children.remove(oldChild)
        children.add(newChild)
    }

    fun setPos(x: Int, y: Int) {
        this.x = x
        this.y = y
        recalculateChildren()
    }

    fun setX(x: Int) {
        this.x = x
        recalculateChildren()
    }

    fun setY(y: Int) {
        this.y = y
        recalculateChildren()
    }

    override fun setWidth(width: Int) {
        this.width = Math.min(width, maxWidth)
        recalculateChildren()
    }

    fun setHeight(height: Int) {
        this.height = height
        recalculateChildren()
    }

    open fun recalculateChildren() {}

    override fun playDownSound(manager: SoundManager) {}

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (visible && active) {
            var b = false
            for (child in children) {
                b = child.keyPressed(keyCode, scanCode, modifiers)
            }
            return b || super.keyPressed(keyCode, scanCode, modifiers)
        }
        return false
    }

    override fun charTyped(codePoint: Char, modifiers: Int): Boolean {
        if (visible && active) {
            for (child in children) {
                if (child.charTyped(codePoint, modifiers)) {
                    return true
                }
            }
            return super.charTyped(codePoint, modifiers)
        }
        return false
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, mouseButton: Int): Boolean {
        for (child in children) {
            if (child is EditBox) {
                child.setFocus(false)
            }
        }
        if (isMouseOver(mouseX, mouseY)) {
            var b = false
            for (child in children) {
                if (child.mouseClicked(mouseX, mouseY, mouseButton)) {
                    b = true
                }
            }
            return b || super.mouseClicked(mouseX, mouseY, mouseButton)
        }
        return false
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, mouseButton: Int): Boolean {
        if (isMouseOver(mouseX, mouseY)) {
            var b = false
            for (child in children) {
                if (child.mouseReleased(mouseX, mouseY, mouseButton)) {
                    b = true
                }
            }
            return b || super.mouseReleased(mouseX, mouseY, mouseButton)
        }
        return false
    }

    override fun changeFocus(focus: Boolean): Boolean {
        if (visible && active) {
            for (child in children) {
                if (child.changeFocus(focus)) {
                    return true
                }
            }
            return super.changeFocus(focus)
        }
        return false
    }

    override fun isMouseOver(mouseX: Double, mouseY: Double): Boolean {
        if (super.isMouseOver(mouseX, mouseY)) {
            for (child in children) {
                if (child.isMouseOver(mouseX, mouseY)) {
                    return true
                }
            }
            return true
        }
        return false
    }

    override fun renderButton(poseStack: PoseStack, mouseX: Int, mouseY: Int, partialTicks: Float) {
        for (child in children) {
            child.render(poseStack, mouseX, mouseY, partialTicks)
        }
    }

//    fun tick() {
//        for (child in children) {
//            if (child is ITickingWidget) {
//                (child as ITickingWidget).tick()
//            }
//        }
//    }
}