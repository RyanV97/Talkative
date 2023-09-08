package dev.cryptcraft.talkative.client.gui.dialog.widgets

import com.mojang.blaze3d.vertex.PoseStack
import dev.cryptcraft.talkative.client.gui.widgets.lists.WidgetList
import dev.cryptcraft.talkative.common.network.clientbound.DialogPacket
import dev.cryptcraft.talkative.common.network.serverbound.DialogResponsePacket
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component

class ResponseList(parent: Screen, x: Int, y: Int, width: Int, height: Int, val entryHeight: Int = 20) : WidgetList<Screen>(parent, x, y, width, height) {
    init {
        renderBackground = false
        renderEntryBackground = false
    }

    fun addEntry(responseData: DialogPacket.ResponseData) {
        addChild(ResponseListEntry(this, responseData, width, entryHeight))
    }

    class ResponseListEntry(private val parentList: ResponseList, val responseData: DialogPacket.ResponseData, width: Int, height: Int, val font: Font = Minecraft.getInstance().font) : AbstractWidget(0, 0, width, height, Component.empty()) {
        override fun renderButton(poseStack: PoseStack, mouseX: Int, mouseY: Int, partialTick: Float) {
            font.drawWordWrap(Component.literal("${parentList.children.indexOf(this) + 1}. ").append(responseData.contents), x + 5, y + 6, width - 10, if (isHoveredOrFocused) 0xFFFFFF else 0x9FFFFFFF.toInt())
        }

        override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
            if (!active || !visible || !isValidClickButton(button) || !clicked(mouseX, mouseY))
                return false
            onResponse()
            return true
        }

        fun onResponse() {
            if (responseData.type == DialogPacket.ResponseData.Type.Exit) {
                if (responseData.responseId > 0)
                    DialogResponsePacket(responseData.responseId).sendToServer()
                parentList.parent.onClose()
            }
        }

        override fun updateNarration(narrationElementOutput: NarrationElementOutput) {}
    }
}