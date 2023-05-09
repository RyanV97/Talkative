package ryanv.talkative.common.data.conditional

import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.server.level.ServerPlayer
import ryanv.talkative.api.Evaluable
import ryanv.talkative.common.util.NBTConstants

class Conditional : ArrayList<Evaluable>(), Evaluable {
    private var not: Boolean = false
    private var or: Boolean = false

    override fun eval(player: ServerPlayer): Boolean {
        if (isEmpty())
            return true
        var output: Boolean? = null

        for (evaluable in this) {
            val result = evaluable.not() != evaluable.eval(player)
            println("Result: $result - Output: $output")
            output = if (output == null) result else if (evaluable.or()) output or result else output and result
        }
        println("Output: $output")

        return output ?: false
    }

    override fun not(): Boolean {
        return not
    }

    override fun or(): Boolean {
        return or
    }

    fun serialize(tag: CompoundTag = CompoundTag()): CompoundTag {
        val list = ListTag()
        for (e in this) {
            val expression = e as Expression.IntExpression
            list.add(expression.serialize(CompoundTag()))
        }
        tag.put(NBTConstants.CONDITIONAL_EXPRESSIONS, list)

        return tag
    }

    companion object {
        fun deserialize(tag: CompoundTag?): Conditional? {
            if (tag == null)
                return null
            val conditional = Conditional()

            val list = tag.getList(NBTConstants.CONDITIONAL_EXPRESSIONS, 10)
            if (list.size == 0)
                return null

            for (expressionTag in list) {
                Expression.IntExpression.deserialize(expressionTag as CompoundTag)?.let { conditional.add(it) }
            }
            return conditional
        }
    }
}