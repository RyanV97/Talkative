package dev.cryptcraft.talkative.server.conversations

import net.minecraft.server.level.ServerPlayer
import dev.cryptcraft.talkative.api.ActorEntity
import dev.cryptcraft.talkative.common.data.ActorData
import dev.cryptcraft.talkative.common.data.tree.DialogBranch
import dev.cryptcraft.talkative.common.util.FileUtil
import dev.cryptcraft.talkative.common.util.RefCountMap
import java.util.*

object ConversationManager {
    private val conversations = HashMap<UUID, Conversation>()
    private val loadedBranches = RefCountMap<String, DialogBranch>()

    fun startConversation(player: ServerPlayer, actor: ActorEntity) {
        if (isInConversation(player))
            return

        actor.getActorData()?.getBranchForPlayer(player)?.let { branchRef ->
            val branch = loadedBranches[branchRef.fileString] ?: loadBranch(branchRef.fileString)
            if (branch != null) {
                val conversation = Conversation(player, actor, branchRef.fileString)
                conversations[player.uuid] = conversation
                conversation.startConversation()
            }
            else {
                //Branch Not Found or failed to create. Throw Error or Exception?
            }
        }
    }

    fun getConversation(player: ServerPlayer): Conversation? {
        return conversations[player.uuid]
    }

    fun isInConversation(player: ServerPlayer): Boolean {
        return conversations.contains(player.uuid)
    }

    fun endConversation(player: ServerPlayer) {
        conversations.remove(player.uuid)?.let {
            endConversation(player)
            unregisterBranchReference(it.getBranchPath(), it)
        }
    }

    private fun loadBranch(path: String): RefCountMap.ReferenceValue<DialogBranch>? {
        FileUtil.getBranchFromPath(path)?.let {
            loadedBranches.put(path, it)
            return loadedBranches[path]
        }
        return null
    }

    fun registerBranchReference(path: String, reference: Any): (() -> DialogBranch)? {
        return loadedBranches.registerReference(path, reference)
    }

    fun unregisterBranchReference(path: String, reference: Any) {
        loadedBranches.unregisterReference(path, reference)
    }
}