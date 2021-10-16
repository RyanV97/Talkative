package ryanv.talkative.common.data

import net.minecraft.nbt.CompoundTag

class Conditional {

    companion object {
        fun deserialize(tag: CompoundTag): Conditional {
            val conditional: Conditional = Conditional()

            return conditional
        }
    }

}