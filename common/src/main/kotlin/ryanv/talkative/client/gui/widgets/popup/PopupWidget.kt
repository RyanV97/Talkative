package ryanv.talkative.client.gui.widgets.popup

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiComponent
import net.minecraft.client.gui.components.*
import net.minecraft.client.gui.components.events.GuiEventListener
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.TextComponent
import net.minecraft.resources.ResourceLocation
import ryanv.talkative.client.gui.TalkativeScreen

class PopupWidget(x: Int, y: Int, width: Int, height: Int, val parent: TalkativeScreen, val label: String? = null, private val clickThrough: Boolean = false) : AbstractWidget(x, y, width, height, TextComponent("Popup Window")) {

    private val components: ArrayList<Widget> = ArrayList()

    override fun renderButton(poseStack: PoseStack?, mouseX: Int, mouseY: Int, delta: Float) {
        fill(poseStack, x, y - 10, x + width, y, 0xFF666666.toInt())
        fill(poseStack, x, y, x + width, y + height, 0xFF222222.toInt())

        //ToDo: Window Label not working for some reason :hmm:
        if(!label.isNullOrEmpty())
            GuiComponent.drawCenteredString(poseStack, Minecraft.getInstance().font, label, x + (width / 2), y - 9, 0xFFFFFF)

        components.forEach {
            it.render(poseStack, mouseX, mouseY, delta)
        }
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, mouseButton: Int): Boolean {
        if(!isMouseOver(mouseX, mouseY)) {
            return !clickThrough
        }
        components.filterIsInstance<GuiEventListener>().forEach {
            if(it.mouseClicked(mouseX, mouseY, mouseButton))
                return true
        }
        return false
    }

    override fun keyPressed(keyCode: Int, j: Int, k: Int): Boolean {
        if(keyCode == 256) {
            parent.onClose()
            return true
        }
        components.filterIsInstance<GuiEventListener>().forEach {
            if(it is EditBox && (keyCode == 32 || keyCode == 257 || keyCode == 335))
                if(it.isFocused)
                    return true
            if(it.keyPressed(keyCode, j, k))
                return true
        }
        return false
    }

    override fun charTyped(char: Char, i: Int): Boolean {
        components.filterIsInstance<GuiEventListener>().forEach {
            if(it.charTyped(char, i))
                return true
        }
        return false
    }

    fun getAllTextFields(): Array<EditBox> {
        return components.filterIsInstance<EditBox>().toTypedArray()
    }

    fun title(title: Component): PopupWidget {
        message = title
        return this
    }

    fun label(x: Int, y: Int, label: String): PopupWidget {
        components.add(PopupLabel(this.x + x, this.y + y, label))
        return this
    }

    fun button(x: Int, y: Int, label: String, width: Int = 50, height: Int = 20, action: (btn: Button) -> Unit): PopupWidget {
        components.add(Button(this.x + x, this.y + y, width, height, TextComponent(label), action))
        return this
    }

    fun imageButton(x: Int, y: Int, iconTexture: ResourceLocation, textureX: Int = 0, textureY: Int = 0, width: Int = 50, height: Int = 50, texWidth: Int = 256, texHeight: Int = 256, diffTexY: Int = 0, action: Button.OnPress, tooltip: Button.OnTooltip = Button.NO_TOOLTIP): PopupWidget {
        components.add(ImageButton(x, y, width, height, textureX, textureY, diffTexY, iconTexture, texWidth, texHeight, action, tooltip, TextComponent.EMPTY))
        return this
    }

    fun textField(x: Int, y: Int, width: Int = 120, height: Int = 20, defaultString: String = ""): PopupWidget {
        val field = EditBox(Minecraft.getInstance().font, this.x + x, this.y + y, width, height, TextComponent("Text Field"))
        field.value = defaultString
        components.add(field)
        return this
    }

}