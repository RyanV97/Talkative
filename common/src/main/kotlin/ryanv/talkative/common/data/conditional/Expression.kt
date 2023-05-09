package ryanv.talkative.common.data.conditional

import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import ryanv.talkative.api.Evaluable
import ryanv.talkative.common.util.NBTConstants

abstract class Expression(private val not: Boolean, private val or: Boolean) : Evaluable {
    class IntExpression(val propertyName: String, val valueB: Int, val operation: Operation, not: Boolean = false, or: Boolean = false) : Expression(not, or) {
        override fun eval(player: ServerPlayer): Boolean {
            val scoreboard = player.scoreboard
            if (scoreboard.hasObjective(propertyName)) {
                println("Scoreboard has Objective")
                val objective = scoreboard.getObjective(propertyName)
                if (scoreboard.hasPlayerScore(player.scoreboardName, objective)) {
                    println("Scoreboard has Score")
                    val valueA = scoreboard.getOrCreatePlayerScore(player.scoreboardName, scoreboard.getObjective(propertyName)).score
                    println("ValA: $valueA - $operation - ValB: $valueB")
                    return when (operation) {
                        Operation.LESS_THAN -> valueA < valueB
                        Operation.LESS_EQUAL -> valueA <= valueB
                        Operation.EQUALS -> valueA == valueB
                        Operation.GREATER_EQUAL -> valueA >= valueB
                        Operation.GREATER_THAN -> valueA > valueB
                    }
                }
            }
            return false
        }

        fun serialize(tag: CompoundTag): CompoundTag {
            tag.putString(NBTConstants.EXPRESSION_PROP, propertyName)
            tag.putInt(NBTConstants.EXPRESSION_VALUE, valueB)
            tag.putString(NBTConstants.EXPRESSION_OPERATION, operation.toString())
            return tag
        }

        companion object {
            fun deserialize(tag: CompoundTag): IntExpression? {
                return IntExpression(tag.getString(NBTConstants.EXPRESSION_PROP), tag.getInt(
                        NBTConstants.EXPRESSION_VALUE), Operation.valueOf(tag.getString(
                            NBTConstants.EXPRESSION_OPERATION)))
            }
        }
    }

    override fun not(): Boolean {
        return not
    }

    override fun or(): Boolean {
        return or
    }

    enum class Operation {
        EQUALS, LESS_THAN, LESS_EQUAL, GREATER_THAN, GREATER_EQUAL
    }
}