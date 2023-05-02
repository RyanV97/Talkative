package ryanv.talkative.common.data.conditional

import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.server.level.ServerPlayer
import ryanv.talkative.common.util.NBTConstants

class Conditional: ArrayList<Expression>() {
    var priority: Int = 0

    fun eval(player: ServerPlayer): Boolean {
        if(isEmpty())
            return true

        var output: Boolean? = null
        for (expression in this) {
            val result = expression.not != expression.eval(player)
            println("Result: $result - Output: $output")

            if(output == null)
                output = result
            output = if (expression.or)
                output or result
            else
                output and result

//            if (output)
//                return output
        }

        println("Output: $output")
        return output ?: false
    }

    fun serialize(tag: CompoundTag): CompoundTag {
        tag.putInt(NBTConstants.CONDITIONAL_PRIORITY, priority)

        val list = ListTag()
        for (e in this) {
            val expression = e as Expression.IntExpression
            list.add(expression.serialize(CompoundTag()))
        }
        tag.put(NBTConstants.CONDITIONAL_EXPRESSIONS, list)

        return tag
    }

    companion object {
        fun deserialize(tag: CompoundTag): Conditional {
            val conditional = Conditional()
            conditional.priority = tag.getInt(NBTConstants.CONDITIONAL_PRIORITY)

            val list = tag.getList(NBTConstants.CONDITIONAL_EXPRESSIONS, 10)
            for (expressionTag in list) {
                Expression.IntExpression.deserialize(expressionTag as CompoundTag)?.let { conditional.add(it) }
            }

            return conditional
        }
    }

    enum class Type {
        BRANCH, OTHER
    }
}