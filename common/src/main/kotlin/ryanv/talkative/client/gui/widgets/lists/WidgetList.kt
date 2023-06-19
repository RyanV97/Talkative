package ryanv.talkative.client.gui.widgets.lists

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.minecraft.util.Mth
import ryanv.talkative.client.gui.widgets.NestedWidget
import ryanv.talkative.client.util.ScissorUtil
import java.awt.Color

//Original Code provided by DenimRed - https://github.com/DenimRed/
open class WidgetList<T : Screen?> (val parent: T, x: Int, y: Int, width: Int, height: Int, title: Component? = Component.empty()) : NestedWidget(x, y, width, height, title) {
    protected var totalHeight = 0
    protected var scrollPos = 0
        set(pos) {
            if (maxScroll > 0) {
                val last = scrollPos
                field = if (pos < 0) 0 else Math.min(pos, maxScroll)
                if (field != last) {
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
    private var scrollBarWidth = 8
    private var scrollBarLeft = false
    private var scrolling = false

    var renderBackground: Boolean = true
    var renderEntryBackground: Boolean = true

    open fun addChild(widget: AbstractWidget) {
        super.addChild(widget)
        adjustChild(widget)
        totalHeight += widget.height
        if (height <= 0)
            height = totalHeight
    }

    fun remove(i: Int) {
        if (i > 0 && i < children.size) {
            this.remove(children.get(i))
        }
    }

    open fun remove(widget: AbstractWidget): Boolean {
        if (children.contains(widget) && removeChild(widget)) {
            totalHeight -= widget.height
            recalculateChildren()
            return true
        }
        return false
    }

    override fun recalculateChildren() {
        totalHeight = 0
        for (child in children) {
            adjustChild(child)
            totalHeight += child.height
        }
    }

    fun adjustChild(child: AbstractWidget) {
        child.width = width
        if (child is NestedWidget) {
            child.setX(x)
            child.setY((y + totalHeight) - scrollPos)
        } else {
            child.x = x
            child.y = (y + totalHeight) - scrollPos
        }
    }

    fun getSize(): Int {
        return children.size
    }

    fun clear() {
        scrollTo(0.0)
        clearChildren()
        totalHeight = 0
    }

    fun setSize(newWidth: Int, newHeight: Int) {
        width = newWidth
        height = newHeight
        recalculateChildren()
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
                    if (scrollBarLeft) mouseX >= x - scrollBarWidth && mouseX <= x else mouseX >= x + width && mouseX <= x + width + scrollBarWidth
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
        if (renderBackground)
            fill(poseStack, x, y, x + width, y + height, 0x66000000)
        if (totalHeight > height)
            renderScrollBar(poseStack)
        ScissorUtil.start(x, y, width, height)
        var i = 0
        val size: Int = children.size
        while (i < size) {
            val child: AbstractWidget = children.get(i)
            if (isWidgetWithin(child)) {
                val color = Color.HSBtoRGB(0.0f, 0.0f, if (i % 2 == 0) 0.1f else 0.2f) and 0x66FFFFFF
                if (renderEntryBackground)
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
        val minX = if (scrollBarLeft) x - scrollBarWidth else x + width
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