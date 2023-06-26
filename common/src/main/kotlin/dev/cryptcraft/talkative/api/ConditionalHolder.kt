package dev.cryptcraft.talkative.api

import dev.cryptcraft.talkative.common.data.conditional.Conditional

interface ConditionalHolder {
    fun getConditional(): Conditional?
    fun setConditional(newConditional: Conditional?)
}