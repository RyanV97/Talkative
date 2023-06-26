package dev.cryptcraft.talkative.server.conversations

import it.unimi.dsi.fastutil.ints.Int2ReferenceOpenHashMap
import net.minecraft.commands.CommandSource
import net.minecraft.commands.CommandSourceStack
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.LivingEntity
import dev.cryptcraft.talkative.api.ActorEntity
import dev.cryptcraft.talkative.common.data.tree.DialogBranch
import dev.cryptcraft.talkative.common.data.tree.DialogNode
import dev.cryptcraft.talkative.common.network.clientbound.DialogPacket

class Conversation(val player: ServerPlayer, val actor: ActorEntity, private var branchPath: String) : CommandSource {
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
                executeNodeCommands(node)
            }
        }
    }

    fun onResponse(responseID: Int) {
        getBranch()?.let { branch ->
            branch.getNode(responseID)?.let { responseNode ->
                currentNodeID = responseNode.nodeId
                executeNodeCommands(responseNode)

                val nextNode = getNextNode()
                if (nextNode != null)
                    sendDialog(nextNode)
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
        val branch = getBranch() ?: return
        val responses = Int2ReferenceOpenHashMap<Component>()

        node.getResponseIDs()?.forEach { id ->
            //ToDo Change Node content to Component - This Component.literal is temporary until then
            branch.getNode(id).let { responseNode ->
                if (responseNode != null)
                    responses.put(id, Component.literal(responseNode.content))
                else
                    println("No Response Node found with ID: $id")
                    //ToDo: Make and Throw Talkative exceptions?
            }
        }

        DialogPacket(Component.literal(node.content), responses, node.getChildren().isEmpty()).sendToPlayer(player)
    }

    private fun executeNodeCommands(node: DialogNode) {
        val entity = actor as LivingEntity
        val commandSourceStack = CommandSourceStack(this, entity.position(), entity.rotationVector, entity.level as ServerLevel, 3, entity.name.string, entity.displayName, entity.level.server, entity)
        node.commands?.forEach {
            player.server.commands.performPrefixedCommand(commandSourceStack, it)
        }
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

    override fun sendSystemMessage(component: Component) {
        player.server.sendSystemMessage(component)
    }

    override fun acceptsSuccess(): Boolean {
        return false
    }

    override fun acceptsFailure(): Boolean {
        return false
    }

    override fun shouldInformAdmins(): Boolean {
        return false
    }
}