package ryanv.talkative.common.network

import me.shedaniel.architectury.networking.NetworkChannel
import me.shedaniel.architectury.networking.NetworkManager
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import ryanv.talkative.Talkative
import ryanv.talkative.client.TalkativeClient
import ryanv.talkative.common.data.Actor
import ryanv.talkative.common.network.bi.OpenEditorPacket_C2S
import ryanv.talkative.common.network.bi.OpenEditorPacket_S2C
import ryanv.talkative.common.network.bi.SyncBranchListPacket
import ryanv.talkative.common.network.c2s.AddBranchPacket
import ryanv.talkative.common.network.c2s.CreateBranchPacket
import ryanv.talkative.common.network.c2s.RemoveBranchPacket
import ryanv.talkative.common.network.c2s.UpdateBranchPacket
import ryanv.talkative.common.network.s2c.DialogPacket
import ryanv.talkative.common.network.s2c.OpenActorUIPacket

class NetworkHandler {
    companion object {
        val CHANNEL: NetworkChannel = NetworkChannel.create(ResourceLocation(Talkative.MOD_ID, "main"))

        fun init() {
            //Bi-directional
            CHANNEL.register(CreateBranchPacket::class.java, CreateBranchPacket::encode, ::CreateBranchPacket, CreateBranchPacket::process)
            CHANNEL.register(SyncBranchListPacket::class.java, SyncBranchListPacket::encode, ::SyncBranchListPacket, SyncBranchListPacket::process)

            //Client -> Server
            CHANNEL.register(OpenEditorPacket_C2S::class.java, OpenEditorPacket_C2S::encode, ::OpenEditorPacket_C2S, OpenEditorPacket_C2S::process)
            CHANNEL.register(AddBranchPacket::class.java, AddBranchPacket::encode, ::AddBranchPacket, AddBranchPacket::process)
            CHANNEL.register(RemoveBranchPacket::class.java, RemoveBranchPacket::encode, ::RemoveBranchPacket, RemoveBranchPacket::process)
            CHANNEL.register(UpdateBranchPacket::class.java, UpdateBranchPacket::encode, ::UpdateBranchPacket, UpdateBranchPacket::process)

            //Server -> Client
            CHANNEL.register(OpenEditorPacket_S2C::class.java, OpenEditorPacket_S2C::encode, ::OpenEditorPacket_S2C, OpenEditorPacket_S2C::process)
            CHANNEL.register(OpenActorUIPacket::class.java, OpenActorUIPacket::encode, ::OpenActorUIPacket, OpenActorUIPacket::process)
            CHANNEL.register(DialogPacket::class.java, DialogPacket::encode, ::DialogPacket, DialogPacket::process)
        }
    }
}