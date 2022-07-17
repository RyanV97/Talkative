package ryanv.talkative.common.data.conditional

import net.minecraft.nbt.CompoundTag
import ryanv.talkative.common.data.tree.DialogContext
import ryanv.talkative.common.consts.NBTConstants

abstract class Expression {

    abstract val type: Int
    val not: Boolean = false
    val or: Boolean = false

    abstract fun eval(context: DialogContext): Boolean

    class GenericExpression(val valueA: Comparable<Any>, val valueB: Comparable<Any>, val operation: Operation): Expression() {
        override val type: Int = 0
        override fun eval(context: DialogContext): Boolean {
            return when(operation) {
                Operation.LESS_THAN -> valueA < valueB
                Operation.LESS_EQUAL -> valueA <= valueB
                Operation.EQUALS -> valueA == valueB
                Operation.GREATER_EQUAL -> valueA >= valueB
                Operation.GREATER_THAN -> valueA > valueB
                else -> false
            }
        }
    }

    class StringExpression(val valueA: String = "", val valueB: String = "", val operation: Operation): Expression() {
        override val type: Int = 1
        override fun eval(context: DialogContext): Boolean {
            return when(operation) {
                Operation.EQUALS -> valueA == valueB
                Operation.CONTAINS -> valueA.contains(valueB, ignoreCase = false)
                Operation.CONTAINS_IGNORE -> valueA.contains(valueB, ignoreCase = true)
                else -> false
            }
        }
    }

    enum class Operation {
        EQUALS, LESS_THAN, LESS_EQUAL, GREATER_THAN, GREATER_EQUAL, CONTAINS, CONTAINS_IGNORE
    }

    fun serialize(tag: CompoundTag): CompoundTag {
        tag.putInt(NBTConstants.EXPRESSION_TYPE, this.type)
        return tag
    }

    companion object {
        fun deserialize(tag: CompoundTag): Expression? {
            var expression: Expression? = null

            when (tag.getInt(NBTConstants.EXPRESSION_TYPE)) {
                //0 -> expression = GenericExpression()
                //1 -> expression = StringExpression()
            }

            return expression
        }
    }

}