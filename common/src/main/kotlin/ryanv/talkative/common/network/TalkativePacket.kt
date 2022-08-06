package ryanv.talkative.common.network

import me.shedaniel.architectury.networking.NetworkManager.PacketContext
import net.minecraft.network.FriendlyByteBuf
import java.util.function.Supplier

interface TalkativePacket {
    fun encode(buf: FriendlyByteBuf)
    fun process(ctx: Supplier<PacketContext>)
}