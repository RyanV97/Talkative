package dev.cryptcraft.talkative.common.network.clientbound

import it.unimi.dsi.fastutil.ints.Int2ReferenceOpenHashMap
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.chat.Component
import dev.cryptcraft.talkative.common.network.NetworkHandler.TalkativePacket

class DialogPacket(val dialogLine: Component?, val responses: Int2ReferenceOpenHashMap<Component>?, val exitNode: Boolean) : TalkativePacket.ClientboundTalkativePacket {
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
        fun writeResponses(responses: Int2ReferenceOpenHashMap<Component>?, buf: FriendlyByteBuf) {
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

        fun readResponses(buf: FriendlyByteBuf): Int2ReferenceOpenHashMap<Component> {
            val responses = Int2ReferenceOpenHashMap<Component>()
            val count = buf.readInt()

            for (i in 0 until count)
                responses.put(buf.readInt(), buf.readComponent())

            return responses
        }
    }
}