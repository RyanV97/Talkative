package dev.cryptcraft.talkative.client.gui.editor.widgets.evaluable

import dev.cryptcraft.talkative.api.conditional.Evaluable
import dev.cryptcraft.talkative.api.conditional.ScoreboardExpression
import dev.cryptcraft.talkative.client.gui.GuiConstants
import dev.cryptcraft.talkative.client.gui.editor.ConditionalEditorPopup
import dev.cryptcraft.talkative.client.gui.editor.widgets.ScoreboardObjectiveTextBox
import dev.cryptcraft.talkative.client.gui.widgets.IconButton
import dev.cryptcraft.talkative.client.gui.widgets.NestedWidget
import dev.cryptcraft.talkative.client.gui.widgets.TalkativeButton
import dev.cryptcraft.talkative.mixin.client.MouseHandlerAccessor
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.EditBox
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.Component

class ExpressionWidget(parent: ConditionalEditorPopup, val expression: ScoreboardExpression, width: Int, height: Int, font: Font) : NestedWidget(0,0, width,height, Component.empty()), EvaluableWidget {
    private val propertyBox: ScoreboardObjectiveTextBox
    private val operationButton: TalkativeButton
    private val valueBox: EditBox
    private val deleteButton: IconButton

    private var operation: ScoreboardExpression.Operation = ScoreboardExpression.Operation.EQUALS
        set(value) {
            field = value
            operationButton.message = Component.literal(value.label)
        }

    init {
        propertyBox = addChild(ScoreboardObjectiveTextBox(parent.parent, 0, 0, 120, 20, Component.empty()))
        operationButton = addChild(TalkativeButton(0, 0, 22, 20, Component.literal("=="), ::cycleOperation))
        valueBox = addChild(EditBox(font, 0, 0, 40, 20, Component.empty()))
        valueBox.setFilter { return@setFilter it.toIntOrNull() != null || it.isBlank() }
        deleteButton = addChild(IconButton(0, 0, 20, 20, GuiConstants.DELETE_ICON) { parent.deleteEntry(this) })

        propertyBox.value = expression.objectiveName
        operation = expression.operation
        valueBox.value = expression.compareValue.toString()
    }

    override fun recalculateChildren() {
        val xPos = x + 5
        val yPos = y + (height / 2) - 10
        propertyBox.x = xPos
        propertyBox.y = yPos
        operationButton.x = xPos + 125
        operationButton.y = yPos
        valueBox.x = xPos + 153
        valueBox.y = yPos
        deleteButton.x = x + width - 20
        deleteButton.y = yPos
    }

    private fun cycleOperation(btn: Button) {
        val invert = (Minecraft.getInstance().mouseHandler as MouseHandlerAccessor).activeButton == 1
        var index = operation.ordinal + if (invert) -1 else 1
        val values = ScoreboardExpression.Operation.values()

        if (index >= values.size)
            index = 0
        else if (index < 0)
            index = values.size - 1

        operation = values[index]
    }

    override fun getOriginalEvaluable(): Evaluable? {
        return expression
    }

    override fun getModifiedEvaluable(): Evaluable? {
        return ScoreboardExpression(propertyBox.value, valueBox.value.toIntOrNull() ?: 0, operation)
    }

    override fun updateNarration(narrationElementOutput: NarrationElementOutput) {}
}