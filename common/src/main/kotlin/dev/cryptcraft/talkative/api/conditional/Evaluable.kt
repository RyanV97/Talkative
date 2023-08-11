package dev.cryptcraft.talkative.api.conditional

import net.minecraft.server.level.ServerPlayer

/**
 * Interface for classes that able to be evaluated in Conditionals.
 */
interface Evaluable {
    /**
     * @return True if the result of the evaluation will be inverted.
     */
    fun not(): Boolean

    /**
     * ToDo: idk how to explain the logic for this :^)
     */
    fun or(): Boolean

    /**
     * @return Evaluation result.
     */
    fun eval(player: ServerPlayer): Boolean
}