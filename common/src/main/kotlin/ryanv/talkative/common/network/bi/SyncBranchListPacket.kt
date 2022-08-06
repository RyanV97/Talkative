package ryanv.talkative.common.network.bi

import me.shedaniel.architectury.networking.NetworkManager
import me.shedaniel.architectury.utils.NbtType
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.StringTag
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.server.level.ServerPlayer
import ryanv.talkative.client.TalkativeClient
import ryanv.talkative.common.network.NetworkHandler
import ryanv.talkative.common.network.TalkativePacket
import ryanv.talkative.common.util.FileUtil
import java.util.function.Supplier

class SyncBranchListPacket(val tag: CompoundTag?): TalkativePacket {
    constructor(buf: FriendlyByteBuf): this(buf.readNbt())

    override fun encode(buf: FriendlyByteBuf) {
        buf.writeNbt(tag)
    }

    override fun process(ctx: Supplier<NetworkManager.PacketContext>) {
        val isClient = ctx.get().player?.level?.isClientSide
        //Client
        if(isClient == true) {
            val list = tag?.getList("branchList", NbtType.STRING)
            TalkativeClient.loadBranchList(list)
        }
        //Server
        else {
            sendListToClient(ctx.get().player as ServerPlayer)
        }
    }

    companion object {
        fun sendListToClient(player: ServerPlayer) {
            val files = FileUtil.getBranchFilePaths()

            val tag = CompoundTag()
            val list = ListTag()
            files.forEach {
                list.add(StringTag.valueOf(it))
            }
            tag.put("branchList", list)

            NetworkHandler.CHANNEL.sendToPlayer(player, SyncBranchListPacket(tag))
        }
    }
}