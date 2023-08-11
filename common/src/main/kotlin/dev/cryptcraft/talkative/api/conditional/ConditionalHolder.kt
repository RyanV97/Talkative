package dev.cryptcraft.talkative.api.conditional

/**
 * Interface for classes that contain Conditional data.
 */
interface ConditionalHolder {
    fun getConditional(): Conditional?
    fun setConditional(newConditional: Conditional?)
}