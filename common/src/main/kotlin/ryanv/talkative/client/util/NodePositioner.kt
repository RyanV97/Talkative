package ryanv.talkative.client.util

import ryanv.talkative.client.gui.widgets.DialogNodeWidget

class NodePositioner {

    companion object {

        fun layoutTree(rootWidget: DialogNodeWidget) {
            apply(rootWidget, 0)
        }

        private fun apply(node: DialogNodeWidget, column: Int) {
            node.x = column * (node.width + 10)
            if (node.children.isNotEmpty()) {
                val numOfChildren: Int = node.children.size
                for (i in 0 until numOfChildren) {
                    val child: DialogNodeWidget = node.children[i]
                    var prevSibling: DialogNodeWidget?
                    if (i == 0) child.y = node.y else {
                        prevSibling = node.children.get(i - 1)
                        if(prevSibling.y + prevSibling.height > prevSibling.lowestChildY)
                            child.y = prevSibling.y + prevSibling.height + 10
                        else
                            child.y = prevSibling.lowestChildY + 10
                    }
                    apply(child, column + 1)
                    if (i == numOfChildren - 1) {
//                        if(child.lowestChildY < child.y + child.height)
//                            node.lowestChildY =
                        node.lowestChildY = child.lowestChildY
                    }
                }
            }
            if (node.lowestChildY < node.y + node.height) node.lowestChildY = node.y + node.height
        }

    }

}