package dev.cryptcraft.talkative.client.data

import dev.cryptcraft.talkative.api.conditional.Conditional
import dev.cryptcraft.talkative.client.TalkativeClient

abstract class ConditionalContext(var conditional: Conditional?) {
    class BranchContext(val actorId: Int, val branchIndex: Int, conditional: Conditional?) : ConditionalContext(conditional) {
        override fun refresh() {
            conditional = TalkativeClient.editingActorData?.dialogBranches?.get(branchIndex)?.getConditional()
        }
    }

    class NodeContext(val branchPath: String, val nodeId: Int, conditional: Conditional?) : ConditionalContext(conditional) {
        override fun refresh() {}
    }

    class MarkerContext(conditional: Conditional?) : ConditionalContext(conditional) {
        override fun refresh() {}
    }

    abstract fun refresh()
}