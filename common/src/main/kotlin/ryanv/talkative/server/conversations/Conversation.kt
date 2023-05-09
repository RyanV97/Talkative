package ryanv.talkative.server.conversations

import it.unimi.dsi.fastutil.ints.Int2ReferenceOpenHashMap
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.TextComponent
import net.minecraft.server.level.ServerPlayer
import ryanv.talkative.api.ActorEntity
import ryanv.talkative.common.data.tree.DialogBranch
import ryanv.talkative.common.data.tree.DialogNode
import ryanv.talkative.common.network.clientbound.DialogPacket

class Conversation(val player: ServerPlayer, val actor: ActorEntity, private var branchPath: String) {
    val listeners: ArrayList<ServerPlayer>? = null
    private var currentNodeID: Int = 0
    private var branchGetter: (() -> DialogBranch)? = null

    fun startConversation() {
        //ToDo Fire an event or somethin
        this.branchGetter = ConversationManager.registerBranchReference(branchPath, this)
        getBranch()?.getNode(0)?.let { sendDialog(it) }
    }

    fun progressConversation() {
        getBranch()?.let {
            getNextNode()?.let { node ->
                sendDialog(node)
            }
        }
    }

    fun onResponse(responseID: Int) {
        getBranch()?.let { branch ->
            branch.getNode(responseID)?.let {
                currentNodeID = it.nodeId
                val node = getNextNode()
                if (node != null)
                    sendDialog(node)
                else
                    DialogPacket(null, null, true).sendToPlayer(player)
            }
        }
    }

    fun endConversation(player: ServerPlayer) {
        //ToDo Fire an event or somethin
        //If I want to run any logic/fire an event when a player ends their conversation
    }

    private fun sendDialog(node: DialogNode) {
        currentNodeID = node.nodeId
        val branch = getBranch()
        val responses = Int2ReferenceOpenHashMap<Component>()

        node.getResponseIDs()?.forEach {
            //ToDo Change Node content to Component - This TextComponent is temporary until then
            responses.put(it, TextComponent(branch?.getNode(it)?.content))
        }

        DialogPacket(TextComponent(node.content), responses, node.getChildren().isEmpty()).sendToPlayer(player)
    }

    fun changeBranch(newBranchPath: String) {
        if (branchGetter != null)
            ConversationManager.unregisterBranchReference(branchPath, this)

        branchPath = newBranchPath
        branchGetter = ConversationManager.registerBranchReference(branchPath, this)
    }

    fun getBranchPath(): String {
        return branchPath
    }

    private fun getBranch(): DialogBranch? {
        return branchGetter?.invoke()
    }

    private fun getNextNode(): DialogNode? {
        return getBranch()?.getChildNodeForPlayer(currentNodeID, player)
    }
}