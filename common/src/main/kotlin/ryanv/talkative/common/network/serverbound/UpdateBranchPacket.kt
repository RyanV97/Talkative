package ryanv.talkative.common.network.serverbound

import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.server.level.ServerPlayer
import ryanv.talkative.common.network.NetworkHandler.TalkativePacket

class UpdateBranchPacket(val path: String, val action: UpdateAction, val data: CompoundTag?) : TalkativePacket.ServerboundTalkativePacket {
    constructor(path: String, action: UpdateAction) : this(path, action, null)
    constructor(buf: FriendlyByteBuf) : this(buf.readUtf(), buf.readEnum(UpdateAction::class.java), buf.readNbt()!!)

    override fun permissionCheck(player: ServerPlayer): Boolean {
        return player.hasPermissions(3)
    }

    override fun encode(buf: FriendlyByteBuf) {
        buf.writeUtf(path)
        buf.writeEnum(action)
        buf.writeNbt(data ?: CompoundTag())
    }

    enum class UpdateAction {
        CREATE, MODIFY, DELETE
    }
}