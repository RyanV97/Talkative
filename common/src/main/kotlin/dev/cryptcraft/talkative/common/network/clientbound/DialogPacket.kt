package dev.cryptcraft.talkative.common.network.clientbound

import dev.cryptcraft.talkative.common.network.NetworkHandler.TalkativePacket
import it.unimi.dsi.fastutil.ints.Int2ReferenceLinkedOpenHashMap
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.chat.Component

class DialogPacket(val dialogLine: Component?, val responses: Int2ReferenceLinkedOpenHashMap<Component>?, val exitNode: Boolean) : TalkativePacket.ClientboundTalkativePacket {
    constructor(buf: FriendlyByteBuf) : this(buf.readComponent(), readResponses(buf), buf.readBoolean())

    override fun encode(buf: FriendlyByteBuf) {
        if (dialogLine != null)
            buf.writeComponent(this.dialogLine)
        else
            buf.writeComponent(Component.empty())
        writeResponses(responses, buf)
        buf.writeBoolean(exitNode)
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