package ryanv.talkative.client.gui.widgets

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.EditBox
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.TextComponent
import ryanv.talkative.client.gui.editor.ConditionalEditorScreen
import ryanv.talkative.common.consts.NBTConstants
import ryanv.talkative.common.data.conditional.Expression

class ConditionalWidget(parent: ConditionalEditorScreen, x: Int, y: Int, width: Int, height: Int, font: Font): NestedWidget(x,y, width,height, TextComponent.EMPTY) {
    val propertyBox: EditBox
    val operationButton: Button
    val valueBox: EditBox
    val deleteButton: Button

    var operation: Expression.Operation = Expression.Operation.EQUALS
        set(value) {
            field = value
            operationButton.message = TextComponent(value.toString())
        }

    init {
        propertyBox = addChild(EditBox(font, x, y, 100, 20, TextComponent.EMPTY))
        operationButton = addChild(Button(x + 105, y, 50, 20, TextComponent("EQUALS"), ::cycleOperation))
        valueBox = addChild(EditBox(font, x + 160, y, 100, 20, TextComponent.EMPTY))
        deleteButton = addChild(Button(x + width - 20, y, 20, 20, TextComponent("X")) {
            parent.deleteEntry(this)
        })
    }

    override fun renderButton(poseStack: PoseStack, mouseX: Int, mouseY: Int, partialTicks: Float) {
        fill(poseStack, x, y, x + width, y + height, 0xFFFFFFFF.toInt())
        super.renderButton(poseStack, mouseX, mouseY, partialTicks)
    }

    override fun recalculateChildren() {
        propertyBox.x = x
        propertyBox.y = y
        operationButton.x = x + 105
        operationButton.y = y
        valueBox.x = x + 160
        valueBox.y = y
        deleteButton.x = x + width - 20
        deleteButton.y = y + height / 4
    }

    private fun cycleOperation(btn: Button) {
        var index = operation.ordinal + 1
        val values = Expression.Operation.values()
        if(index >= values.size)
            index = 0
        operation = values[index]
    }

    fun serialize(tag: CompoundTag): CompoundTag {
        tag.putString(NBTConstants.EXPRESSION_PROP, propertyBox.value)
        tag.putInt(NBTConstants.EXPRESSION_VALUE, valueBox.value.toInt())
        tag.putString(NBTConstants.EXPRESSION_OPERATION, operation.toString())
        return tag
    }

    companion object {
        fun deserialize(parent: ConditionalEditorScreen, tag: CompoundTag): ConditionalWidget {
            println(tag)
            val widget = ConditionalWidget(parent, 0,0, parent.width,30, Minecraft.getInstance().font)
            widget.propertyBox.value = tag.getString(NBTConstants.EXPRESSION_PROP)
            widget.valueBox.value = tag.getInt(NBTConstants.EXPRESSION_VALUE).toString()
            widget.operation = Expression.Operation.valueOf(tag.getString(NBTConstants.EXPRESSION_OPERATION))
            return widget
        }
    }
}