package dev.cryptcraft.talkative.client.gui.editor.widgets

import net.minecraft.client.gui.components.Checkbox
import net.minecraft.network.chat.Component

class CallbackCheckbox(x: Int, y: Int, width: Int, height: Int, label: Component, value: Boolean = false, private val callback: (() -> Unit?)) : Checkbox(x, y, width, height, label, value) {
    constructor(x: Int, y: Int, width: Int, height: Int, label: Component, callback: (() -> Unit?)) : this(x, y, width, height, label, false, callback)

    override fun onPress() {
        super.onPress()
        callback.invoke()
    }

}