package ryanv.talkative.common.network.s2c

import me.shedaniel.architectury.networking.NetworkManager
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.chat.TextComponent
import ryanv.talkative.client.TalkativeClient
import ryanv.talkative.common.network.TalkativePacket
import java.util.function.Supplier

class DialogPacket(val speaker: TextComponent, val contents: String, val responses: ListTag? = null): TalkativePacket {
    constructor(buf: FriendlyByteBuf): this(buf.readComponent() as TextComponent, buf.readUtf(), if(buf.isReadable) buf.readNbt()?.getList("responses", 8) else null)

    override fun encode(buf: FriendlyByteBuf) {
        buf.writeComponent(speaker)
        buf.writeUtf(contents)
        if (responses != null) {
            val tag = CompoundTag()
            tag.put("responses", ListTag())
            buf.writeNbt(tag)
        }
    }

    override fun process(ctx: Supplier<NetworkManager.PacketContext>) {
        TalkativeClient.processDialogPacket(this)
    }

}