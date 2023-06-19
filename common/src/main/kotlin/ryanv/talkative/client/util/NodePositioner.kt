package ryanv.talkative.client.util

import ryanv.talkative.client.gui.editor.widgets.NodeWidget

object NodePositioner {
    fun layoutTree(rootWidget: NodeWidget) {
        layout(rootWidget, 0)
    }

    private fun layout(node: NodeWidget, column: Int) {
        //Position Node horizontally based on Column and fixed width
        node.x = column * (node.width + 10)

        if (node.children.isNotEmpty()) {
            val numOfChildren: Int = node.children.size
            for (index in 0 until numOfChildren) {
                val child: NodeWidget = node.children[index]
                var prevSibling: NodeWidget?

                if (index == 0) child.y = node.y
                else {
                    prevSibling = node.children[index - 1]
                    if(prevSibling.y + prevSibling.height > prevSibling.lowestChildY)
                        child.y = prevSibling.y + prevSibling.height + 10
                    else
                        child.y = prevSibling.lowestChildY + 10
                }
                layout(child, column + 1)

                if (index == numOfChildren - 1) {
//                        if(child.lowestChildY < child.y + child.height)
//                            node.lowestChildY =
                    node.lowestChildY = child.lowestChildY
                }
            }
        }
        if (node.lowestChildY < node.y + node.height) node.lowestChildY = node.y + node.height
    }
}