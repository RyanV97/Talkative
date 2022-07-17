package ryanv.talkative.common.network

import io.netty.buffer.Unpooled
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import me.shedaniel.architectury.networking.NetworkManager
import me.shedaniel.architectury.utils.NbtType
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.StringTag
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import ryanv.talkative.Talkative
import ryanv.talkative.client.TalkativeClient
import ryanv.talkative.common.data.Actor
import ryanv.talkative.common.util.FileUtil

class NetworkHandler {
    companion object {

        val Client_OpenActorUI = ResourceLocation(Talkative.MOD_ID, "s2c_actor_ui")
        val Both_SyncBranchList = ResourceLocation(Talkative.MOD_ID, "sync_branch_list")

        fun init() {
            //Client
            NetworkManager.registerReceiver(NetworkManager.s2c(), Client_OpenActorUI) { byteBuf, context ->
                TalkativeClient.openActorEditor(Actor().deserialize(byteBuf.readNbt() as CompoundTag))
            }
            NetworkManager.registerReceiver(NetworkManager.s2c(), Both_SyncBranchList) { byteBuf, context ->
                val tag = byteBuf.readNbt()
                val list = tag?.getList("branchList", NbtType.STRING)
                TalkativeClient.loadBranchList(list)
            }
            //Server
            NetworkManager.registerReceiver(NetworkManager.c2s(), Both_SyncBranchList) { byteBuf, context ->
                runBlocking {
                    launch {
                        val buf = FriendlyByteBuf(Unpooled.buffer())
                        val files = FileUtil.getBranchFilePaths()

                        val tag = CompoundTag()
                        val list = ListTag()
                        files.forEach{
                            list.add(StringTag.valueOf(it))
                        }
                        tag.put("branchList", list)

                        buf.writeNbt(tag)
                        NetworkManager.sendToPlayer(context.player as ServerPlayer?, Both_SyncBranchList, buf)
                    }
                }
            }
        }

    }
}