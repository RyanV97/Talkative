package ryanv.talkative.api

import ryanv.talkative.common.data.conditional.Conditional

interface ConditionalHolder {
    fun getConditional(): Conditional?
    fun setConditional(newConditional: Conditional?)
}