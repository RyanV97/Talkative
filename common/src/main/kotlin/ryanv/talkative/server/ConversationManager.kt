package ryanv.talkative.server

import net.minecraft.server.level.ServerPlayer
import ryanv.talkative.api.IActorEntity
import ryanv.talkative.common.data.tree.DialogNode
import ryanv.talkative.common.network.NetworkHandler
import ryanv.talkative.common.network.s2c.DialogPacket
import ryanv.talkative.common.util.FileUtil
import java.util.UUID

object ConversationManager {
    private val conversations = HashMap<UUID, Conversation>()

    fun startConversation(player: ServerPlayer, actor: IActorEntity) {
        if(isInConversation(player))
            return
        val branchRef = actor.actorData.getBranchForPlayer(player)
        if (branchRef != null) {
            val branch = FileUtil.getBranchFromPath(branchRef.fileString)
            if (branch != null) {
                val rootNode = branch.nodes[0]
                val responses = rootNode!!.getResponses(branch)
                val conversation = Conversation(branchRef.fileString)
                conversations[player.uuid] = conversation
                conversation.progressConversation(player, rootNode, responses)
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
        conversations.remove(player.uuid)?.end(player)
    }
}