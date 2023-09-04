package dev.cryptcraft.talkative.server.conversations

import dev.cryptcraft.talkative.api.actor.ActorEntity
import dev.cryptcraft.talkative.api.event.ConversationEvent
import dev.cryptcraft.talkative.api.tree.DialogBranch
import dev.cryptcraft.talkative.api.tree.node.BridgeNode
import dev.cryptcraft.talkative.api.tree.node.DialogNode
import dev.cryptcraft.talkative.api.tree.node.NodeBase
import dev.cryptcraft.talkative.api.tree.node.ResponseNode
import dev.cryptcraft.talkative.common.network.clientbound.DialogPacket
import net.minecraft.commands.CommandSource
import net.minecraft.commands.CommandSourceStack
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.LivingEntity

/**
 * A Conversation represents an ongoing "Dialog" between a Player and an Actor.
 */
class Conversation(val player: ServerPlayer, val actor: ActorEntity, private var branchPath: String) : CommandSource {
    val listeners: ArrayList<ServerPlayer>? = null
    private var currentNodeID: Int = 0
    private var branchGetter: (() -> DialogBranch)? = null

    fun startConversation() {
        if (ConversationEvent.START.invoker().start(this).isFalse) return

        this.branchGetter = ConversationManager.registerBranchReference(branchPath, this)
        val node = getBranch()?.getNode(0) ?: return
        sendDialog(node as DialogNode)
        executeNodeCommands(node)
    }

    fun progressConversation() {
        getBranch()?.let {
            getNextNode()?.let { node ->
                sendDialog(node)
                executeNodeCommands(node)
            }
        }

        val node = getNextNode() ?: return
        sendDialog(node as DialogNode)
        executeNodeCommands(node)
    }

    fun onResponse(responseID: Int) {
        ConversationEvent.RESPONSE.invoker().response(this, responseID)

        getBranch()?.let { branch ->
            if (responseID > 0) {
                branch.getNode(responseID)?.let { responseNode ->
                    currentNodeID = responseNode.nodeId
                    executeNodeCommands(responseNode)
                }
            }
            val nextNode = getNextNode()
            if (nextNode != null) {
                executeNodeCommands(nextNode)
                sendDialog(nextNode)
            }
            //else
            //ToDo Throw Exception?
        }
    }

    fun endConversation(player: ServerPlayer) {
        ConversationEvent.END.invoker().end(this)
    }

    private fun sendDialog(node: DialogNode) {
        ConversationEvent.PROGRESS.invoker().progress(this, currentNodeID, node.nodeId)
        currentNodeID = node.nodeId
        val branch = getBranch() ?: return
        val responses = ArrayList<DialogPacket.ResponseData>()

        if (node.getChildren().isNotEmpty()) {
            if (node.getChildrenType() == NodeBase.NodeType.Response) {
                node.getChildren().forEach { ref ->
                    branch.getNode(ref.nodeId)?.let { responseNode ->
                        if (responseNode is ResponseNode) {
                            if (responseNode.getConditional()?.eval(player) == false)
                                return@forEach

                            val type = if (responseNode.getChildren().isEmpty())
                                DialogPacket.ResponseData.Type.Exit
                            else
                                DialogPacket.ResponseData.Type.Response

                            responses.add(DialogPacket.ResponseData(ref.nodeId, responseNode.getContents(), type))
                        }
                        else
                            println("No Response Node found with ID: $ref.nodeId")
                        //ToDo: Make and Throw Talkative exceptions?
                    }
                }
            }
            else
                responses.add(DialogPacket.ResponseData(0, Component.literal("Continue").withStyle { it.withItalic(true) }, DialogPacket.ResponseData.Type.Continue))
        }
        else
            responses.add(DialogPacket.ResponseData(0, Component.literal("Leave").withStyle { it.withItalic(true) }, DialogPacket.ResponseData.Type.Exit))

        DialogPacket(node.getContents(), responses, node.getChildren().isEmpty()).sendToPlayer(player)
    }

    private fun executeNodeCommands(node: NodeBase) {
        val entity = actor as LivingEntity
        val commandSourceStack = CommandSourceStack(this, entity.position(), entity.rotationVector, entity.level as ServerLevel, 3, entity.name.string, entity.displayName, entity.level.server, entity)
        node.commands?.forEach {
            if (player.server.commands.performPrefixedCommand(commandSourceStack, it) == 0)
                println("Failed to run command '$it' in Conversation with player: ${player.displayName.string}") //ToDo Proper Logger
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
        val destination = getNextDialogForPlayer(currentNodeID, player) ?: return null
        if (destination.branchPath != branchPath)
            changeBranch(destination.branchPath)

        val node = getBranch()?.getNode(destination.nodeId)
        return if (node is DialogNode)
            node
        else
            null //ToDo No valid destination was found, exit convo and report issue
    }

    private fun getNextDialogForPlayer(parentId: Int, player: ServerPlayer): Destination? {
        return getNextDialogForPlayer(getBranch()?.getNode(parentId), player)
    }

    private fun getNextDialogForPlayer(parent: NodeBase?, player: ServerPlayer): Destination? {
        parent?.getChildren()?.forEach {
            val child = getBranch()!!.getNode(it.nodeId)
            if (child?.getConditional() == null || child.getConditional()!!.eval(player)) {
                when (child) {
                    is DialogNode -> {
                        return Destination(branchPath, child.nodeId)
                    }
                    is BridgeNode -> {
                        return Destination(child.destinationBranchPath, child.destinationNodeId)
                    }
                }
            }
        }
        return null
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

    data class Destination(val branchPath: String, val nodeId: Int)
}