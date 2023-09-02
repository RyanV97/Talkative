package dev.cryptcraft.talkative.server.conversations

import dev.cryptcraft.talkative.api.actor.ActorEntity
import dev.cryptcraft.talkative.api.tree.DialogBranch
import dev.cryptcraft.talkative.common.util.FileUtil
import dev.cryptcraft.talkative.common.util.RefCountMap
import net.minecraft.server.level.ServerPlayer
import java.util.*

object ConversationManager {
    private val conversations = HashMap<UUID, Conversation>()
    private val loadedBranches = RefCountMap<String, DialogBranch>()

    fun startConversation(player: ServerPlayer, actor: ActorEntity) {
        if (isInConversation(player))
            return

        actor.getActorData()?.getBranchForPlayer(player)?.let { branchRef ->
            val branch = loadedBranches[branchRef.filePath] ?: loadBranch(branchRef.filePath)
            if (branch != null) {
                val conversation = Conversation(player, actor, branchRef.filePath)
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
            it.endConversation(player)
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

    fun registerBranchReference(branchPath: String, reference: Any): (() -> DialogBranch)? {
        val branchRef = loadedBranches[branchPath] ?: loadBranch(branchPath)
        if (branchRef != null) return loadedBranches.registerReference(branchPath, reference)
        return null
    }

    fun unregisterBranchReference(path: String, reference: Any) {
        loadedBranches.unregisterReference(path, reference)
    }
}