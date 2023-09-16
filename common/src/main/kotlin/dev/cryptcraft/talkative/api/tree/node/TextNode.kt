package dev.cryptcraft.talkative.api.tree.node

import dev.cryptcraft.talkative.api.conditional.Conditional
import dev.cryptcraft.talkative.common.util.NBTConstants
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.StringTag
import net.minecraft.nbt.Tag
import net.minecraft.network.chat.Component

abstract class TextNode(nodeId: Int, conditional: Conditional?) : NodeBase(nodeId, conditional) {
    abstract fun setContents(contents: List<Component>)
    abstract fun getContents(): List<Component>

    override fun serialize(tag: CompoundTag): CompoundTag {
        val list = ListTag()
        for (line in getContents())
            list.add(StringTag.valueOf(Component.Serializer.toJson(line)))
        tag.put(NBTConstants.NODE_TEXT_CONTENTS, list)
        return super.serialize(tag)
    }

    override fun deserialize(tag: CompoundTag): NodeBase {
        val list = ArrayList<Component>()
        for (line in tag.getList(NBTConstants.NODE_TEXT_CONTENTS, Tag.TAG_STRING.toInt()))
            list.add(Component.Serializer.fromJson((line as StringTag).asString) ?: Component.empty())
        setContents(list)
        return this
    }
}