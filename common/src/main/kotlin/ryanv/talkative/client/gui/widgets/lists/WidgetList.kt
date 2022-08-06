package ryanv.talkative.client.gui.widgets.lists

import com.mojang.blaze3d.vertex.PoseStack
import ryanv.talkative.client.util.ScissorUtil
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.TextComponent
import net.minecraft.util.Mth
import ryanv.talkative.client.gui.widgets.NestedWidget
import java.awt.Color

//Original Code provided by DenimRed - https://github.com/DenimRed/
class WidgetList<T : Screen?> (val parent: T, x: Int, y: Int, width: Int, height: Int, title: Component? = TextComponent.EMPTY) : NestedWidget(x, y, width, height, title) {
    protected var totalHeight = 0
    protected var scrollPos = 0
        set(pos) {
            field = pos
            if (maxScroll > 0) {
                val last = scrollPos
                scrollPos = if (pos < 0) 0 else Math.min(pos, maxScroll)
                if (scrollPos != last) {
                    val diff = scrollPos - last
                    for (child in children) {
                        if (child is NestedWidget) {
                            child.setY(child.y - diff)
                        } else {
                            child.y -= diff
                        }
                        child.visible = child.y <= y + height && child.y + child.height >= y
                    }
                }
            }
        }
    protected var scrollBarWidth = 8
        set(value) {
            field = value
            for (child in children) {
                child.width = width - scrollBarWidth
                if (scrollBarLeft) {
                    if (child is NestedWidget) {
                        child.setX(x + scrollBarWidth)
                    } else {
                        child.x = x + scrollBarWidth
                    }
                }
            }
        }
    protected var scrollBarLeft = false
        set(value) {
            field = value
            for (child in children) {
                val childX = if (scrollBarLeft) x + scrollBarWidth else x
                if (child is NestedWidget) {
                    child.setX(childX)
                } else {
                    child.x = childX
                }
            }
        }
    protected var scrolling = false

    fun add(widget: AbstractWidget) {
        this.addChild<AbstractWidget>(widget)
        val realWidth = width - scrollBarWidth
        widget.width = realWidth
        val widgetX = x + realWidth / 2 - widget.width / 2
        if (widget is NestedWidget) {
            widget.setX(widgetX)
            widget.setY(y + totalHeight)
        } else {
            widget.x = widgetX
            widget.y = y + totalHeight
        }
        totalHeight += widget.height
    }

    fun remove(i: Int) {
        if (i > 0 && i < children.size) {
            this.remove(children.get(i))
        }
    }

    fun remove(widget: AbstractWidget) {
        if (children.contains(widget)) {
            removeChild(widget)
            totalHeight -= widget.height
        }
    }

    fun clear() {
        scrollTo(0.0)
        clearChildren()
        totalHeight = 0
    }

    fun scrollTo(pos: Double) {
        val percent = (pos - y) / height
        scrollPos = (maxScroll * percent).toInt()
    }

    val maxScroll: Int
        get() = totalHeight - height

    fun isScrollBarLeft(): Boolean {
        return scrollBarLeft
    }

    val scrollBarHeight: Int
        get() = if (totalHeight > height) {
            val factor = height.toDouble() / totalHeight
            Mth.clamp((factor * height).toInt(), 40, height)
        } else {
            height
        }
    val scrollBarY: Int
        get() {
            val adjustedHeight = (height - scrollBarHeight).toFloat()
            val scrollPercent = scrollPos.toFloat() / maxScroll.toFloat()
            return (y + scrollPercent * adjustedHeight).toInt()
        }

    override fun mouseClicked(mouseX: Double, mouseY: Double, mouseButton: Int): Boolean {
        if (visible && active) {
            if (isValidClickButton(mouseButton)) {
                scrolling =
                    if (scrollBarLeft) mouseX >= x && mouseX <= x + scrollBarWidth else mouseX >= x + width - scrollBarWidth && mouseX <= x + width
                val barY = scrollBarY
                if (scrolling && (mouseY < barY || mouseY > barY + scrollBarHeight)) {
                    scrollTo(mouseY)
                }
            }
            return scrolling || super.mouseClicked(mouseX, mouseY, mouseButton)
        }
        scrolling = false
        return false
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, mouseButton: Int): Boolean {
        if (scrolling) {
            scrolling = false
        }
        return super.mouseReleased(mouseX, mouseY, mouseButton) || !scrolling
    }

    override fun mouseDragged(
        mouseX: Double, mouseY: Double, button: Int, dragX: Double, dragY: Double
    ): Boolean {
        if (visible && active && scrolling) {
            val maxScroll = maxScroll
            if (mouseY < y || maxScroll <= 0) {
                scrollPos = 0
            } else if (mouseY > y + height) {
                scrollPos = maxScroll
            } else {
                scrollTo(mouseY)
            }
            return true
        }
        return false
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, delta: Double): Boolean {
        val averageHeight: Double = totalHeight.toDouble() / children.size
        scrollPos = (scrollPos - delta * averageHeight / 3.0).toInt()
        return true
    }

    override fun renderButton(poseStack: PoseStack, mouseX: Int, mouseY: Int, partialTicks: Float) {
        fill(poseStack, x, y, x + width, y + height, 0x66000000)
        renderScrollBar(poseStack)
        ScissorUtil.start(
            if (scrollBarLeft) x + scrollBarWidth else x, y, width - scrollBarWidth, height
        )
        var i = 0
        val size: Int = children.size
        while (i < size) {
            val child: AbstractWidget = children.get(i)
            if (isWidgetWithin(child)) {
                val color = Color.HSBtoRGB(0.0f, 0.0f, if (i % 2 == 0) 0.1f else 0.2f) and 0x66FFFFFF
                fill(poseStack, x, child.y, x + width, child.y + child.height, color)
            }
            i++
        }
        super.renderButton(poseStack, mouseX, mouseY, partialTicks)
        ScissorUtil.stop()
    }

    protected fun isWidgetWithin(widget: AbstractWidget): Boolean {
        return (((widget.x <= x + width) && (widget.x + widget.width >= x)) && widget.y <= y + height) && widget.y + widget.height >= y
    }

    protected fun renderScrollBar(poseStack: PoseStack?) {
        val minX = if (scrollBarLeft) x else x + width - scrollBarWidth
        val maxX = minX + scrollBarWidth
        val minY = y
        val maxY = y + height
        fill(poseStack, minX, minY, maxX, maxY, 0x55000000)
        val barHeight = scrollBarHeight
        val barY = scrollBarY
        fill(poseStack, minX, barY, maxX, barY + barHeight, -0x222223)
        fill(poseStack, minX + 1, barY + 1, maxX, barY + barHeight, -0x99999a)
        fill(poseStack, minX + 1, barY + 1, maxX - 1, barY + barHeight - 1, -0x555556)
        val lines = barHeight / 6
        for (i in 0 until lines) {
            val y = barY + barHeight / 2 - lines + 2 * i
            hLine(poseStack, minX, maxX - 1, y, 0x44000000)
        }
    }
}