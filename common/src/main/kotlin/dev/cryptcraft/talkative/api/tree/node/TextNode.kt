package dev.cryptcraft.talkative.api.tree.node

import net.minecraft.network.chat.Component

interface TextNode {
    fun setContents(contents: Component)
    fun getContents(): Component
}