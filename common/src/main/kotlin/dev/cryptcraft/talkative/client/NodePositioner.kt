package dev.cryptcraft.talkative.client

import dev.cryptcraft.talkative.client.gui.editor.branch.widgets.nodes.NodeWidget

object NodePositioner {
    fun layoutTree(rootWidget: NodeWidget) {
        layout(rootWidget, 0)
    }

    private fun layout(node: NodeWidget, column: Int) {
        //Position Node horizontally based on Column and fixed width
        node.x = column * (node.width + 10)

        if (node.childNodes.isNotEmpty()) {
            val numOfChildren: Int = node.childNodes.size
            for (index in 0 until numOfChildren) {
                val child: NodeWidget = node.childNodes[index]
                var prevSibling: NodeWidget?

                if (index == 0) child.y = node.y
                else {
                    prevSibling = node.childNodes[index - 1]
                    if(prevSibling.y + prevSibling.height > prevSibling.lowestChildY)
                        child.y = prevSibling.y + prevSibling.height + 10
                    else
                        child.y = prevSibling.lowestChildY + 10
                }
                layout(child, column + 1)

                if (index == numOfChildren - 1)
                    node.lowestChildY = child.lowestChildY
            }
        }
        if (node.lowestChildY < node.y + node.height) node.lowestChildY = node.y + node.height
    }
}