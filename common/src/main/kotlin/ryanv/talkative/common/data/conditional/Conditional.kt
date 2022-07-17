package ryanv.talkative.common.data.conditional

import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import ryanv.talkative.common.data.tree.DialogContext
import ryanv.talkative.common.consts.NBTConstants

class Conditional: ArrayList<Expression>() {

    fun eval(context: DialogContext): Boolean {
        var output: Boolean = isEmpty()
        for (expression in this) {
            val result = expression.not != expression.eval(context)

            output = if (expression.or)
                output or result
            else
                output and result

            if (output)
                return output
        }
        return output
    }

    fun serialize(tag: CompoundTag): CompoundTag {
        val list = ListTag()
        for (expression in this)
            list.add(expression.serialize(CompoundTag()))
        tag.put(NBTConstants.CONDITIONAL_EXPRESSIONS, list)
        return tag
    }

    companion object {
        fun deserialize(tag: CompoundTag): Conditional {
            val conditional = Conditional()
            val list = tag.getList(NBTConstants.CONDITIONAL_EXPRESSIONS, 10)
            for (expressionTag in list) {
                Expression.deserialize(expressionTag as CompoundTag)?.let { conditional.add(it) }
            }
            return conditional
        }
    }

}