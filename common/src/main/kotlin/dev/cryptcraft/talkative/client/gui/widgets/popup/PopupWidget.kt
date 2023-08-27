package dev.cryptcraft.talkative.client.gui.widgets.popup

import com.mojang.blaze3d.vertex.PoseStack
import dev.cryptcraft.talkative.client.gui.GuiConstants
import dev.cryptcraft.talkative.client.gui.TalkativeScreen
import dev.cryptcraft.talkative.client.gui.widgets.IconButton
import dev.cryptcraft.talkative.client.gui.widgets.NestedWidget
import dev.cryptcraft.talkative.client.gui.widgets.TalkativeButton
import dev.cryptcraft.talkative.client.gui.widgets.lists.WidgetList
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiComponent
import net.minecraft.client.gui.components.Button.*
import net.minecraft.client.gui.components.EditBox
import net.minecraft.network.chat.Component

open class PopupWidget(x: Int, y: Int, width: Int, height: Int, val parent: TalkativeScreen, private val label: String? = null, private val clickThrough: Boolean = false) : NestedWidget(x, y, width, height, Component.literal("Popup Window")) {

    override fun renderButton(poseStack: PoseStack, mouseX: Int, mouseY: Int, delta: Float) {
        fill(poseStack, x, y, x + width, y + height, GuiConstants.COLOR_POPUP_BORDER)
        fill(poseStack, x + 2, y + 2, x + width - 2, y + height - 2, GuiConstants.COLOR_EDITOR_BG_SECONDARY)

        if (!label.isNullOrEmpty()) GuiComponent.drawCenteredString(poseStack, Minecraft.getInstance().font, label, x + (width / 2), y - 9, 0xFFFFFF)

        super.renderButton(poseStack, mouseX, mouseY, delta)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, mouseButton: Int): Boolean {
        if (!isMouseOver(mouseX, mouseY)) {
            return !clickThrough
        }
        return super.mouseClicked(mouseX, mouseY, mouseButton)
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (keyCode == 256) {
            parent.closePopup()
            return true
        }
        return super.keyPressed(keyCode, scanCode, modifiers)
    }

    fun getAllTextFields(): Array<EditBox> {
        return children.filterIsInstance<EditBox>().toTypedArray()
    }

    fun title(title: Component): Component {
        message = title
        return message
    }

    fun label(x: Int, y: Int, label: String): PopupLabel {
        return label(x, y, Component.literal(label))
    }

    fun label(x: Int, y: Int, label: Component): PopupLabel {
        return addChild(PopupLabel(this.x + x, this.y + y, label))
    }

    fun button(x: Int, y: Int, label: String, width: Int = 50, height: Int = 20, action: OnPress): TalkativeButton {
        return button(x, y, label, width, height, action, NO_TOOLTIP)
    }

    fun button(x: Int, y: Int, label: String, width: Int = 50, height: Int = 20, action: OnPress, tooltip: OnTooltip): TalkativeButton {
        return addChild(TalkativeButton(this.x + x, this.y + y, width, height, Component.literal(label), action, tooltip))
    }

    fun iconButton(x: Int, y: Int, width: Int, height: Int, icon: IconButton.Icon, action: OnPress): IconButton {
        return iconButton(x, y, width, height, icon, action, NO_TOOLTIP)
    }

    fun iconButton(x: Int, y: Int, width: Int, height: Int, icon: IconButton.Icon, action: OnPress, tooltip: OnTooltip): IconButton {
        return addChild(IconButton(this.x + x, this.y + y, width, height, icon, action, tooltip))
    }

    fun editBox(x: Int, y: Int, width: Int = 120, height: Int = 20, defaultString: String = ""): EditBox {
        return textField(x, y, width, height, defaultString)
    }

    fun textField(x: Int, y: Int, width: Int = 120, height: Int = 20, defaultString: String = ""): EditBox {
        val field = EditBox(Minecraft.getInstance().font, this.x + x, this.y + y, width, height, Component.literal("Text Field"))
        field.value = defaultString
        return addChild(field)
    }

    fun list(x: Int, y: Int, width: Int, height: Int): WidgetList<TalkativeScreen> {
        return addChild(WidgetList(parent, this.x + x, this.y + y, width, height))
    }

    class Builder(val x: Int, val y: Int, val width: Int, val height: Int, val parent: TalkativeScreen, private val label: String? = null, private val clickThrough: Boolean = false) {
        private var widget: PopupWidget = PopupWidget(x, y, width, height, parent, label, clickThrough)

        fun title(title: Component): Builder {
            widget.title(title)
            return this
        }

        fun label(x: Int, y: Int, label: String): Builder {
            return label(x, y, Component.literal(label))
        }

        fun label(x: Int, y: Int, label: Component): Builder {
            widget.label(x, y, label)
            return this
        }

        fun button(x: Int, y: Int, label: String, width: Int = 50, height: Int = 20, action: OnPress): Builder {
            return button(x, y, label, width, height, action, NO_TOOLTIP)
        }

        fun button(x: Int, y: Int, label: String, width: Int = 50, height: Int = 20, action: OnPress, tooltip: OnTooltip): Builder {
            widget.button(x, y, label, width, height, action, tooltip)
            return this
        }

        fun iconButton(x: Int, y: Int, width: Int, height: Int, icon: IconButton.Icon, action: OnPress): Builder {
            return iconButton(x, y, width, height, icon, action, NO_TOOLTIP)
        }

        fun iconButton(x: Int, y: Int, width: Int, height: Int, icon: IconButton.Icon, action: OnPress, tooltip: OnTooltip): Builder {
            widget.iconButton(x, y, width, height, icon, action, tooltip)
            return this
        }

        fun editBox(x: Int, y: Int, width: Int = 120, height: Int = 20, defaultString: String = ""): Builder {
            return textField(x, y, width, height, defaultString)
        }

        fun textField(x: Int, y: Int, width: Int = 120, height: Int = 20, defaultString: String = ""): Builder {
            widget.textField(x, y, width, height, defaultString)
            return this
        }

        fun build(): PopupWidget {
            return widget
        }
    }

    open fun onClose() {}
    open fun tick() {}

}