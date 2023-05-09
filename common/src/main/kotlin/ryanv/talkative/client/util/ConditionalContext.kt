package ryanv.talkative.client.util

import ryanv.talkative.client.TalkativeClient
import ryanv.talkative.common.data.conditional.Conditional

abstract class ConditionalContext(var conditional: Conditional?) {
    class BranchConditionalContext(val actorId: Int, val branchIndex: Int, conditional: Conditional?) : ConditionalContext(conditional) {
        override fun refresh() {
            conditional = TalkativeClient.editingActorData?.dialogBranches?.get(branchIndex)?.getConditional()
        }
    }

    class NodeConditionalContext(val branchPath: String, val nodeId: Int, conditional: Conditional?) : ConditionalContext(conditional) {
        override fun refresh() {}
    }

    abstract fun refresh()
}