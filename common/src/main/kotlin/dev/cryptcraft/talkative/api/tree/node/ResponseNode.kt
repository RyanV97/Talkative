package dev.cryptcraft.talkative.api.tree.node

import dev.cryptcraft.talkative.api.conditional.Conditional
import dev.cryptcraft.talkative.common.util.NBTConstants
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component

class ResponseNode(nodeId: Int, private var contents: Component, conditional: Conditional? = null) : TextNode(nodeId, conditional) {
    override fun serialize(tag: CompoundTag): CompoundTag {
        tag.putString(NBTConstants.NODE_TEXT_CONTENTS, Component.Serializer.toJson(contents))
        return super.serialize(tag)
    }

    override fun deserialize(tag: CompoundTag): NodeBase {
        setContents(Component.Serializer.fromJson(tag.getString(NBTConstants.NODE_TEXT_CONTENTS)) ?: Component.empty())
        return this
    }

    override fun getNodeType(): NodeType {
        return NodeType.Response
    }

    override fun setContents(contents: Component) {
        this.contents = contents
    }

    override fun getContents(): Component {
        return contents
    }
}