package dev.cryptcraft.talkative.common.markdown

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import org.commonmark.node.*
import org.commonmark.parser.Parser

object MarkdownParser {
    fun markdownToComponents(input: String): List<MutableComponent> {
        return markdownToComponents(input.split("\n", ignoreCase = true))
    }

    fun markdownToComponents(input: List<String>): List<MutableComponent> {
        val list = ArrayList<MutableComponent>()
        val parser = Parser.builder().build()

        for (string in input) {
            val node = parser.parse(string)
            val visitor = TalkativeVisitor()
            node.accept(visitor)
            list.add(visitor.completeMessage ?: Component.empty())
        }

        return list
    }

    fun componentsToMarkdown(input: List<Component>): String {
        var s = ""
        val it = input.iterator()
        while (it.hasNext()) {
            val component = it.next()
            for (c in component.toFlatList())
                s += decodePart(c)
            if (it.hasNext()) s += "\n"
        }
        return s
    }

    private fun decodePart(component: Component): String {
        var s = component.string

        if (component.style.isBold)
            s = "**$s**"
        if (component.style.isItalic)
            s = "*$s*"

        for (sibling in component.siblings) s += decodePart(sibling)
        return s
    }

    class TalkativeVisitor : AbstractVisitor() {
        var completeMessage: MutableComponent? = null

        override fun visit(text: Text?) {
            if (text == null) return

            val msg = Component.literal(text.literal)
            var node = text.parent
            while (node !is Document && node != null) {
                when (node) {
                    is Emphasis -> msg.style = msg.style.withItalic(true)
                    is StrongEmphasis -> msg.style = msg.style.withBold(true)
                }
                node = node.parent
            }

            if (completeMessage == null)
                completeMessage = msg
            else
                completeMessage!!.append(msg)
            super.visit(text)
        }
    }
}