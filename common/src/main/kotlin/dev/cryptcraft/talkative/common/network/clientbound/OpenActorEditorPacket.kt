package dev.cryptcraft.talkative.common.network.clientbound

import dev.architectury.networking.NetworkManager
import net.minecraft.network.FriendlyByteBuf
import dev.cryptcraft.talkative.api.actor.ActorData
import dev.cryptcraft.talkative.client.TalkativeClient
import dev.cryptcraft.talkative.client.gui.editor.MainEditorScreen
import dev.cryptcraft.talkative.common.network.NetworkHandler.TalkativePacket
import net.minecraft.client.Minecraft
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.LivingEntity

class OpenActorEditorPacket(private val entityId: Int, private val actorData: ActorData?) : TalkativePacket.ServerboundTalkativePacket, TalkativePacket.ClientboundTalkativePacket {
    constructor(entityId: Int) : this(entityId, null)
    constructor(buf: FriendlyByteBuf) : this(buf.readInt(), readData(buf))

    override fun permissionCheck(player: ServerPlayer): Boolean {
        return player.hasPermissions(3)
    }

    override fun encode(buf: FriendlyByteBuf) {
        buf.writeInt(entityId)
        if (actorData != null) {
            actorData.validate()
            buf.writeNbt(actorData.serialize())
        }
    }

    override fun onReceived(ctx: NetworkManager.PacketContext) {
        ctx.player.level.getEntity(entityId)?.let {
            if (it is LivingEntity) {
                val screen = Minecraft.getInstance().screen
                TalkativeClient.editingActorData = actorData

                if (screen !is MainEditorScreen)
                    TalkativeClient.openEditorScreen(it)
            }
        }
    }

    companion object {
        fun readData(buf: FriendlyByteBuf): ActorData? {
            return if (buf.readableBytes() > 0)
                ActorData.deserialize(buf.readNbt()!!)
            else
                null
        }
    }
}