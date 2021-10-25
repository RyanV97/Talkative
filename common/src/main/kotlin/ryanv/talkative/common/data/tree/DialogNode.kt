package ryanv.talkative.common.data.tree

import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.StringTag
import net.minecraft.nbt.Tag
import ryanv.talkative.common.data.Conditional
import ryanv.talkative.consts.NBTConstants

class DialogNode(var content: String = "", val responses: LinkedHashMap<String, Conditional?> = LinkedHashMap(), val children: ArrayList<DialogNode> = ArrayList()) {

    fun serialize(tag: CompoundTag): CompoundTag {
        tag.putString(NBTConstants.NODE_CONTENT, content)

        if(responses.isNotEmpty()) {
            val tagList = ListTag()
            for (entry in responses) {
                val responseTag = CompoundTag()
                responseTag.putString(NBTConstants.NODE_RESPONSE_STRING, entry.key)
                if(entry.value != null)
                    responseTag.put(NBTConstants.CONDITIONAL, entry.value!!.serialize(CompoundTag()))
                tagList.add(responseTag)
            }
            tag.put(NBTConstants.NODE_RESPONSES, tagList)
        }

        if(children.isNotEmpty()) {
            val childrenTag = ListTag()
            for(child in children)
                childrenTag.add(child.serialize(CompoundTag()))
            tag.put(NBTConstants.NODE_CHILDREN, childrenTag)
        }

        return tag
    }

    companion object {
        fun deserialize(tag: CompoundTag): DialogNode {
            val node = DialogNode()

            node.content = tag.getString(NBTConstants.NODE_CONTENT)
            if(tag.contains(NBTConstants.NODE_RESPONSES)) {
                val listResponses: ListTag = tag.getList(NBTConstants.NODE_RESPONSES, 10)
                for(tagResponse: Tag in listResponses) {
                    val responseString = (tagResponse as CompoundTag).getString(NBTConstants.NODE_RESPONSE_STRING)
                    val responseConditional = Conditional.deserialize(tagResponse.getCompound(NBTConstants.CONDITIONAL))
                    node.responses[responseString] = responseConditional
                }
            }

            if(tag.contains(NBTConstants.NODE_CHILDREN)) {
                val listChildren: ListTag = tag.getList(NBTConstants.NODE_CHILDREN, 10)
                for(tagChild: Tag in listChildren)
                    node.children.add(deserialize(tagChild as CompoundTag))
            }

            return node
        }
    }

}