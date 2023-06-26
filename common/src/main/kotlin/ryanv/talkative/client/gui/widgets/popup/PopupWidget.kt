package ryanv.talkative.client.gui.widgets.popup

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiComponent
import net.minecraft.client.gui.components.*
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import ryanv.talkative.client.gui.TalkativeScreen
import ryanv.talkative.client.gui.widgets.NestedWidget
import ryanv.talkative.client.gui.widgets.lists.WidgetList

open class PopupWidget(x: Int, y: Int, width: Int, height: Int, val parent: TalkativeScreen, val label: String? = null, private val clickThrough: Boolean = false) : NestedWidget(x, y, width, height, Component.literal("Popup Window")) {

    override fun renderButton(poseStack: PoseStack, mouseX: Int, mouseY: Int, delta: Float) {
        fill(poseStack, x, y - 10, x + width, y, 0xFF666666.toInt())
        fill(poseStack, x, y, x + width, y + height, 0xFF222222.toInt())

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
        return addChild(PopupLabel(this.x + x, this.y + y, label))
    }

    fun button(x: Int, y: Int, label: String, width: Int = 50, height: Int = 20, action: (btn: Button) -> Unit): Button {
        return addChild(Button(this.x + x, this.y + y, width, height, Component.literal(label), action))
    }

    fun imageButton(x: Int, y: Int, iconTexture: ResourceLocation, textureX: Int = 0, textureY: Int = 0, width: Int = 50, height: Int = 50, texWidth: Int = 256, texHeight: Int = 256, diffTexY: Int = 0, action: Button.OnPress, tooltip: Button.OnTooltip = Button.NO_TOOLTIP): ImageButton {
        return addChild(ImageButton(x, y, width, height, textureX, textureY, diffTexY, iconTexture, texWidth, texHeight, action, tooltip, Component.empty()))
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

    class Builder(val x: Int, val y: Int, val width: Int, val height: Int, val parent: TalkativeScreen, val label: String? = null, private val clickThrough: Boolean = false) {
        private var widget: PopupWidget = PopupWidget(x, y, width, height, parent, label, clickThrough)

        fun title(title: Component): Builder {
            widget.title(title)
            return this
        }

        fun label(x: Int, y: Int, label: String): Builder {
            widget.label(x, y, label)
            return this
        }

        fun button(x: Int, y: Int, label: String, width: Int = 50, height: Int = 20, action: (btn: Button) -> Unit): Builder {
            widget.button(x, y, label, width, height, action)
            return this
        }

        fun imageButton(x: Int, y: Int, iconTexture: ResourceLocation, textureX: Int = 0, textureY: Int = 0, width: Int = 50, height: Int = 50, texWidth: Int = 256, texHeight: Int = 256, diffTexY: Int = 0, action: Button.OnPress, tooltip: Button.OnTooltip = Button.NO_TOOLTIP): Builder {
            widget.imageButton(x, y, iconTexture, textureX, textureY, width, height, texWidth, texHeight, diffTexY, action, tooltip)
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

    open fun tick() {}

}