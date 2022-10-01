package ryanv.talkative.common.network

import me.shedaniel.architectury.networking.NetworkChannel
import net.minecraft.resources.ResourceLocation
import ryanv.talkative.Talkative
import ryanv.talkative.common.network.bi.OpenBranchEditorPacket_C2S
import ryanv.talkative.common.network.bi.OpenBranchEditorPacket_S2C
import ryanv.talkative.common.network.bi.SyncBranchListPacket
import ryanv.talkative.common.network.c2s.*
import ryanv.talkative.common.network.s2c.DialogPacket
import ryanv.talkative.common.network.s2c.OpenActorEditorPacket

class NetworkHandler {
    companion object {
        val CHANNEL: NetworkChannel = NetworkChannel.create(ResourceLocation(Talkative.MOD_ID, "main"))

        fun init() {
            //Bi-directional
            CHANNEL.register(CreateBranchPacket::class.java, CreateBranchPacket::encode, ::CreateBranchPacket, CreateBranchPacket::process)
            CHANNEL.register(SyncBranchListPacket::class.java, SyncBranchListPacket::encode, ::SyncBranchListPacket, SyncBranchListPacket::process)

            //Client -> Server
            CHANNEL.register(OpenBranchEditorPacket_C2S::class.java, OpenBranchEditorPacket_C2S::encode, ::OpenBranchEditorPacket_C2S, OpenBranchEditorPacket_C2S::process)
            CHANNEL.register(AddBranchPacket::class.java, AddBranchPacket::encode, ::AddBranchPacket, AddBranchPacket::process)
            CHANNEL.register(RemoveBranchPacket::class.java, RemoveBranchPacket::encode, ::RemoveBranchPacket, RemoveBranchPacket::process)
            CHANNEL.register(UpdateBranchPacket::class.java, UpdateBranchPacket::encode, ::UpdateBranchPacket, UpdateBranchPacket::process)
            CHANNEL.register(DialogResponsePacket::class.java, DialogResponsePacket::encode, ::DialogResponsePacket, DialogResponsePacket::process)

            //Server -> Client
            CHANNEL.register(OpenBranchEditorPacket_S2C::class.java, OpenBranchEditorPacket_S2C::encode, ::OpenBranchEditorPacket_S2C, OpenBranchEditorPacket_S2C::process)
            CHANNEL.register(OpenActorEditorPacket::class.java, OpenActorEditorPacket::encode, ::OpenActorEditorPacket, OpenActorEditorPacket::process)
            CHANNEL.register(DialogPacket::class.java, DialogPacket::encode, ::DialogPacket, DialogPacket::process)
        }
    }
}