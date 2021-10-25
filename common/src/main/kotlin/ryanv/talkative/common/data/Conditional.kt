package ryanv.talkative.common.data

import net.minecraft.nbt.CompoundTag

class Conditional {

    fun serialize(tag: CompoundTag): CompoundTag {

        return tag
    }

    companion object {
        fun deserialize(tag: CompoundTag): Conditional? {
            if(tag != null) {
                val conditional: Conditional = Conditional()

                return conditional
            }
            else
                return null
        }
    }

}