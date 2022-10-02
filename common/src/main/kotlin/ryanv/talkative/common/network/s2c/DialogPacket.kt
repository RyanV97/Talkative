package ryanv.talkative.common.network.s2c

import me.shedaniel.architectury.networking.NetworkManager
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.StringTag
import net.minecraft.network.FriendlyByteBuf
import ryanv.talkative.client.TalkativeClient
import ryanv.talkative.common.data.Response
import ryanv.talkative.common.data.tree.DialogNode
import ryanv.talkative.common.network.TalkativePacket
import java.util.function.Supplier

class DialogPacket(val node: DialogNode, var responses: List<Response>?): TalkativePacket {
    constructor(buf: FriendlyByteBuf): this(DialogNode.deserialize(buf.readNbt() as CompoundTag), decodeResponses(buf.readNbt()!!))

    override fun encode(buf: FriendlyByteBuf) {
        buf.writeNbt(node.serialize(CompoundTag()))
        buf.writeNbt(encodeResponses(responses))
    }

    override fun process(ctx: Supplier<NetworkManager.PacketContext>) {
        TalkativeClient.processDialogPacket(this)
    }

    companion object {
        private fun encodeResponses(responses: List<Response>?): CompoundTag {
            if(responses.isNullOrEmpty())
                return CompoundTag()

            val tag = CompoundTag()
            val list = ListTag()
            responses.forEach {
                list.add(it.serialize(CompoundTag()))
            }
            tag.put("list", list)
            return tag
        }

        private fun decodeResponses(tag: CompoundTag): List<Response>? {
            var responses = ArrayList<Response>()
            if(tag.contains("list")) {
                val list = tag.getList("list", 10)
                list.forEach {
                    responses.add(Response.deserialize(it as CompoundTag))
                }
            }
            return responses
        }
    }

}