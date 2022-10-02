package ryanv.talkative.api;

import net.minecraft.nbt.CompoundTag;

import ryanv.talkative.common.data.conditional.Conditional;

public interface IConditional {
    Type getConditionalType();
    Conditional getConditional();
    CompoundTag getData();
    enum Type { BRANCH, NODE }
}
