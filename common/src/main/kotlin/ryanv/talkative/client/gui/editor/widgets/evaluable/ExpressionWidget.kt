package ryanv.talkative.client.gui.editor.widgets.evaluable

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.EditBox
import net.minecraft.network.chat.TextComponent
import ryanv.talkative.api.Evaluable
import ryanv.talkative.client.gui.editor.ConditionalEditorScreen
import ryanv.talkative.client.gui.widgets.NestedWidget
import ryanv.talkative.common.data.conditional.Expression

class ExpressionWidget(parent: ConditionalEditorScreen, val expression: Expression, width: Int, height: Int, font: Font) : NestedWidget(0,0, width,height, TextComponent.EMPTY), EvaluableWidget {
    val propertyBox: EditBox
    val operationButton: Button
    val valueBox: EditBox
    val deleteButton: Button

    var operation: Expression.Operation = Expression.Operation.EQUALS
        set(value) {
            field = value
            val label =
                when (value) {
                    Expression.Operation.EQUALS -> "=="
                    Expression.Operation.LESS_THAN -> "<"
                    Expression.Operation.LESS_EQUAL -> "<="
                    Expression.Operation.GREATER_THAN -> ">"
                    Expression.Operation.GREATER_EQUAL -> ">="
                }
            operationButton.message = TextComponent(label)
        }

    init {
        propertyBox = addChild(EditBox(font, 0, 0, 120, 20, TextComponent.EMPTY))
        operationButton = addChild(Button(0, 0, 22, 20, TextComponent("=="), ::cycleOperation))
        valueBox = addChild(EditBox(font, 0, 0, 60, 20, TextComponent.EMPTY))
        valueBox.setFilter {
            return@setFilter it.toIntOrNull() != null || it.isBlank()
        }
        deleteButton = addChild(Button(0, 0, 20, 20, TextComponent("X")) {
            parent.deleteEntry(this)
        })

        val e = expression as Expression.IntExpression
        propertyBox.value = e.propertyName
        operation = e.operation
        valueBox.value = e.valueB.toString()
    }

    override fun renderButton(poseStack: PoseStack, mouseX: Int, mouseY: Int, partialTicks: Float) {
        fill(poseStack, x, y, x + width, y + height, 0x11FFFFFF.toInt())
        super.renderButton(poseStack, mouseX, mouseY, partialTicks)
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
        deleteButton.x = x + width - 25
        deleteButton.y = yPos
    }

    private fun cycleOperation(btn: Button) {
        var index = operation.ordinal + 1
        val values = Expression.Operation.values()
        if (index >= values.size)
            index = 0
        operation = values[index]
    }

    override fun getOriginalEvaluable(): Evaluable? {
        return expression
    }

    override fun getModifiedEvaluable(): Evaluable? {
        return Expression.IntExpression(propertyBox.value, valueBox.value.toIntOrNull() ?: 0, operation)
    }
}