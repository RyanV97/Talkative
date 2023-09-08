package dev.cryptcraft.talkative.common.network.clientbound

import dev.architectury.networking.NetworkManager
import dev.cryptcraft.talkative.api.actor.DisplayData
import dev.cryptcraft.talkative.client.TalkativeClient
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.chat.Component

class StartDialogPacket(private val actorEntityId: Int, private val displayData: DisplayData?, dialogLine: Component, responses: ArrayList<ResponseData>?, isExitNode: Boolean = false) : DialogPacket(dialogLine, responses, isExitNode) {
    constructor(buf: FriendlyByteBuf) : this(buf.readInt(), DisplayData.deserialize(buf.readNbt()), buf.readComponent(), readResponses(buf), buf.readBoolean())

    override fun encode(buf: FriendlyByteBuf) {
        buf.writeInt(actorEntityId)
        buf.writeNbt(displayData?.serialize())
        super.encode(buf)
    }

    override fun onReceived(ctx: NetworkManager.PacketContext) {
        TalkativeClient.startDialog(actorEntityId, displayData)
        super.onReceived(ctx)
    }
}