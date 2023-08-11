package dev.cryptcraft.talkative.api.conditional

import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import dev.cryptcraft.talkative.common.util.NBTConstants

class ScoreboardExpression(val objectiveName: String, val compareValue: Int, val operation: Operation, private val not: Boolean = false, private val or: Boolean = false) : Evaluable {
    override fun eval(player: ServerPlayer): Boolean {
        val scoreboard = player.scoreboard
        if (scoreboard.hasObjective(objectiveName)) {
            val objective = scoreboard.getObjective(objectiveName)
            if (scoreboard.hasPlayerScore(player.scoreboardName, objective)) {
                val objectiveValue = scoreboard.getOrCreatePlayerScore(player.scoreboardName, scoreboard.getObjective(objectiveName)).score
                return when (operation) {
                    Operation.LESS_THAN -> objectiveValue < compareValue
                    Operation.LESS_EQUAL -> objectiveValue <= compareValue
                    Operation.EQUALS -> objectiveValue == compareValue
                    Operation.GREATER_EQUAL -> objectiveValue >= compareValue
                    Operation.GREATER_THAN -> objectiveValue > compareValue
                }
            }
        }
        return false
    }

    override fun not(): Boolean {
        return not
    }

    override fun or(): Boolean {
        return or
    }

    fun serialize(tag: CompoundTag): CompoundTag {
        tag.putString(NBTConstants.EXPRESSION_PROP, objectiveName)
        tag.putInt(NBTConstants.EXPRESSION_VALUE, compareValue)
        tag.putString(NBTConstants.EXPRESSION_OPERATION, operation.toString())
        return tag
    }

    companion object {
        fun deserialize(tag: CompoundTag): ScoreboardExpression? {
            return ScoreboardExpression(tag.getString(NBTConstants.EXPRESSION_PROP), tag.getInt(
                    NBTConstants.EXPRESSION_VALUE), Operation.valueOf(tag.getString(
                        NBTConstants.EXPRESSION_OPERATION)))
        }
    }

    enum class Operation {
        EQUALS, LESS_THAN, LESS_EQUAL, GREATER_THAN, GREATER_EQUAL
    }
}