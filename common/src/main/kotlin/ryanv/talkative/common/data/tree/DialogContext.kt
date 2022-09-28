package ryanv.talkative.common.data.tree

import net.minecraft.nbt.ListTag
import ryanv.talkative.common.consts.NBTConstants

class DialogContext(var currentNode: DialogNode) {

    val indexPath = ArrayList<Int>()

    fun traverse(index: Int) {
        if(currentNode.children.isNotEmpty() && currentNode.children.size > index) {
            indexPath.add(index)
            //currentNode = currentNode.children[index] - broken since changing child structure
            //Send Node to Client first
            //Then start loading up children
            var children: ListTag = ListTag() // Change to load branch and get children ListTag from branch root
            for (i in 0 until indexPath.size) {
                children = children.getCompound(indexPath[i]).getList(NBTConstants.NODE_CHILDREN, 10)
            }
        }
    }

}