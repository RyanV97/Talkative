package dev.cryptcraft.talkative.common.markdown

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import org.commonmark.node.*
import org.commonmark.parser.Parser

object MarkdownParser {
    fun parse(string: String): MutableComponent? {
        val parser = Parser.builder().build()
        val node = parser.parse(string)
        val visitor = TalkativeVisitor()
        node.accept(visitor)
        return visitor.completeMessage
    }

    fun decode(component: Component): String {
        var s = ""
        for (c in component.toFlatList())
            s += decodePart(c)
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