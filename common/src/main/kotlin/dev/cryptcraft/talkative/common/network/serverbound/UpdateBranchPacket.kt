package dev.cryptcraft.talkative.common.network.serverbound

import dev.architectury.networking.NetworkManager
import dev.cryptcraft.talkative.api.tree.DialogBranch
import dev.cryptcraft.talkative.common.network.NetworkHandler.TalkativePacket
import dev.cryptcraft.talkative.common.network.clientbound.SyncBranchListPacket
import dev.cryptcraft.talkative.server.FileUtil
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.server.level.ServerPlayer

class UpdateBranchPacket(private val branchPath: String, private val action: UpdateAction, private val data: CompoundTag?) : TalkativePacket.ServerboundTalkativePacket {
    constructor(path: String, action: UpdateAction, branch: DialogBranch) : this(path, action, branch.serialize())
    constructor(path: String, action: UpdateAction) : this(path, action, null)
    constructor(buf: FriendlyByteBuf) : this(buf.readUtf(), buf.readEnum(UpdateAction::class.java), buf.readNbt()!!)

    override fun permissionCheck(player: ServerPlayer): Boolean {
        return player.hasPermissions(2)
    }

    override fun encode(buf: FriendlyByteBuf) {
        buf.writeUtf(branchPath)
        buf.writeEnum(action)
        buf.writeNbt(data ?: CompoundTag())
    }

    override fun onReceived(ctx: NetworkManager.PacketContext) {
        val player = ctx.player as ServerPlayer

        when (action) {
            UpdateAction.CREATE -> {
                FileUtil.createBranchAtPath(branchPath)
                SyncBranchListPacket(FileUtil.getBranchListCompound()).sendToPlayer(player)
            }

            UpdateAction.MODIFY -> {
                data?.let { FileUtil.saveBranchData(branchPath, it) }
            }

            UpdateAction.DELETE -> {
                FileUtil.deleteBranchAtPath(branchPath)
                SyncBranchListPacket(FileUtil.getBranchListCompound()).sendToPlayer(player)
            }
        }
    }

    enum class UpdateAction {
        CREATE, MODIFY, DELETE
    }
}