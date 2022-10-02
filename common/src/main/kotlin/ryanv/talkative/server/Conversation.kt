package ryanv.talkative.server

import net.minecraft.server.level.ServerPlayer
import ryanv.talkative.common.data.Response
import ryanv.talkative.common.data.tree.DialogNode
import ryanv.talkative.common.network.NetworkHandler
import ryanv.talkative.common.network.s2c.DialogPacket

class Conversation(val branchPath: String) {
    private val nodeHistory: ArrayList<Int> = ArrayList()

    fun progressConversation(player: ServerPlayer, nextNode: DialogNode, responses: List<Response>?) {
        nodeHistory.add(nextNode.nodeId)
        NetworkHandler.CHANNEL.sendToPlayer(player, DialogPacket(nextNode, responses))
    }

    fun end(player: ServerPlayer) {

    }

    fun getHistory(): ArrayList<Int> {
        return nodeHistory
    }
}