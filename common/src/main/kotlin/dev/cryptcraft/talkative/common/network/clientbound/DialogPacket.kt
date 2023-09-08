package dev.cryptcraft.talkative.common.network.clientbound

import dev.architectury.networking.NetworkManager
import dev.cryptcraft.talkative.client.TalkativeClient
import dev.cryptcraft.talkative.common.network.NetworkHandler.TalkativePacket
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.chat.Component

open class DialogPacket(private val dialogLine: Component, private val responses: ArrayList<ResponseData>?, private val isExitNode: Boolean = false) : TalkativePacket.ClientboundTalkativePacket {
    constructor(buf: FriendlyByteBuf) : this(buf.readComponent(), readResponses(buf), buf.readBoolean())

    override fun encode(buf: FriendlyByteBuf) {
        buf.writeComponent(this.dialogLine)
        writeResponses(responses, buf)
        buf.writeBoolean(isExitNode)
    }

    override fun onReceived(ctx: NetworkManager.PacketContext) {
        TalkativeClient.onReceiveDialog(dialogLine, responses, isExitNode)
    }

    companion object {
        fun writeResponses(responses: ArrayList<ResponseData>?, buf: FriendlyByteBuf) {
            if (responses == null) {
                buf.writeInt(0)
            }
            else {
                buf.writeInt(responses.size)
                responses.forEach {
                    it.encode(buf)
                }
            }
        }

        fun readResponses(buf: FriendlyByteBuf): ArrayList<ResponseData> {
            val responses = ArrayList<ResponseData>()
            val count = buf.readInt()

            for (i in 0 until count)
                responses.add(ResponseData(buf))

            return responses
        }
    }

    data class ResponseData(val responseId: Int, val contents: Component, val type: Type) {
        constructor(buf: FriendlyByteBuf) : this(buf.readInt(), buf.readComponent(), buf.readEnum(Type::class.java))

        fun encode(buf: FriendlyByteBuf) {
            buf.writeInt(responseId)
            buf.writeComponent(contents)
            buf.writeEnum(type)
        }

        enum class Type {
            Response, Continue, Exit
        }
    }
}