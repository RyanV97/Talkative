package dev.cryptcraft.talkative.common.network.clientbound

import dev.architectury.networking.NetworkManager
import dev.cryptcraft.talkative.client.TalkativeClient
import dev.cryptcraft.talkative.common.network.NetworkHandler.TalkativePacket
import it.unimi.dsi.fastutil.ints.Int2ReferenceLinkedOpenHashMap
import net.minecraft.ChatFormatting
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.HoverEvent

class DialogPacket(private val dialogLine: Component?, private val responses: Int2ReferenceLinkedOpenHashMap<Component>?, private val isExitNode: Boolean) : TalkativePacket.ClientboundTalkativePacket {
    constructor(buf: FriendlyByteBuf) : this(buf.readComponent(), readResponses(buf), buf.readBoolean())

    override fun encode(buf: FriendlyByteBuf) {
        if (dialogLine != null)
            buf.writeComponent(this.dialogLine)
        else
            buf.writeComponent(Component.empty())
        writeResponses(responses, buf)
        buf.writeBoolean(isExitNode)
    }

    override fun onReceived(ctx: NetworkManager.PacketContext) {
        TalkativeClient.onReceiveDialog(dialogLine, responses, isExitNode)


        val label = Component.literal("[Perception]").withStyle { style ->
            return@withStyle style.withColor(ChatFormatting.DARK_GRAY).withHoverEvent(
                HoverEvent()
            )
        }
        val baseText = Component.literal("Blah blah this is my text")


    }

    companion object {
        fun writeResponses(responses: Int2ReferenceLinkedOpenHashMap<Component>?, buf: FriendlyByteBuf) {
            if (responses == null) {
                buf.writeInt(0)
                return
            }
            buf.writeInt(responses.size)
            responses.forEach {
                buf.writeInt(it.key)
                buf.writeComponent(it.value)
            }
        }

        fun readResponses(buf: FriendlyByteBuf): Int2ReferenceLinkedOpenHashMap<Component> {
            val responses = Int2ReferenceLinkedOpenHashMap<Component>()
            val count = buf.readInt()

            for (i in 0 until count)
                responses.put(buf.readInt(), buf.readComponent())

            return responses
        }
    }
}