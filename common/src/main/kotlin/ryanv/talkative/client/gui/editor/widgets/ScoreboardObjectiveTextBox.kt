package ryanv.talkative.client.gui.editor.widgets

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.components.CommandSuggestions
import net.minecraft.client.gui.components.EditBox
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component

class ScoreboardObjectiveTextBox(font: Font, parentScreen: Screen, x: Int, y: Int, width: Int, height: Int, message: Component) : EditBox(font, x, y, width, height, message) {
    constructor(parentScreen: Screen, x: Int, y: Int, width: Int, height: Int, message: Component) : this(Minecraft.getInstance().font, parentScreen, x, y, width, height, message)

    val completionSuggestion: CommandSuggestions = CommandSuggestions(Minecraft.getInstance(), parentScreen, this, font, false, false, 0, 10, false, Int.MIN_VALUE)

    init {

        //ToDo Decide how to make suggestions for current scoreboard objective names
        setResponder {

        }
    }
}