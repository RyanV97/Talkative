package ryanv.talkative.api

import net.minecraft.nbt.CompoundTag

interface ActorData {
    fun shouldOverrideDisplayName(): Boolean
    fun serialize(tag: CompoundTag): CompoundTag
}