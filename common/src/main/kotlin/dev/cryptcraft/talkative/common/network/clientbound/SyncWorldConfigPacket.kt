package dev.cryptcraft.talkative.common.network.clientbound

import dev.architectury.networking.NetworkManager
import dev.cryptcraft.talkative.common.network.NetworkHandler
import dev.cryptcraft.talkative.server.TalkativeWorldConfig
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf

class SyncWorldConfigPacket(private val config: TalkativeWorldConfig) : NetworkHandler.TalkativePacket.ClientboundTalkativePacket {
    constructor(buf: FriendlyByteBuf) : this(TalkativeWorldConfig.deserialize(buf.readNbt() ?: CompoundTag()))

    override fun encode(buf: FriendlyByteBuf) {
        buf.writeNbt(config.serialize())
    }

    override fun onReceived(ctx: NetworkManager.PacketContext) {
        TalkativeWorldConfig.INSTANCE = config
    }
}