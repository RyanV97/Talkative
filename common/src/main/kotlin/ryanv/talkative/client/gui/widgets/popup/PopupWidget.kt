package ryanv.talkative.client.gui.widgets.popup

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiComponent
import net.minecraft.client.gui.components.*
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import ryanv.talkative.client.gui.TalkativeScreen
import ryanv.talkative.client.gui.widgets.NestedWidget

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

    fun title(title: Component): PopupWidget {
        message = title
        return this
    }

    fun label(x: Int, y: Int, label: String): PopupWidget {
        addChild(PopupLabel(this.x + x, this.y + y, label))
        return this
    }

    fun button(x: Int, y: Int, label: String, width: Int = 50, height: Int = 20, action: (btn: Button) -> Unit): PopupWidget {
        addChild(Button(this.x + x, this.y + y, width, height, Component.literal(label), action))
        return this
    }

    fun imageButton(x: Int, y: Int, iconTexture: ResourceLocation, textureX: Int = 0, textureY: Int = 0, width: Int = 50, height: Int = 50, texWidth: Int = 256, texHeight: Int = 256, diffTexY: Int = 0, action: Button.OnPress, tooltip: Button.OnTooltip = Button.NO_TOOLTIP): PopupWidget {
        addChild(ImageButton(x, y, width, height, textureX, textureY, diffTexY, iconTexture, texWidth, texHeight, action, tooltip, Component.empty()))
        return this
    }

    fun textField(x: Int, y: Int, width: Int = 120, height: Int = 20, defaultString: String = ""): PopupWidget {
        val field = EditBox(Minecraft.getInstance().font, this.x + x, this.y + y, width, height, Component.literal("Text Field"))
        field.value = defaultString
        addChild(field)
        return this
    }

    open fun tick() {}

}