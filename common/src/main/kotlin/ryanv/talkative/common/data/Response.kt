package ryanv.talkative.common.data

import net.minecraft.nbt.CompoundTag

class Response(val id: Int, val contents: String) {

    fun serialize(tag: CompoundTag): CompoundTag {
        tag.putInt("id", id)
        tag.putString("contents", contents)
        return tag
    }

    companion object {
        fun deserialize(tag: CompoundTag): Response {
            return Response(tag.getInt("id"), tag.getString("contents"))
        }
    }

}