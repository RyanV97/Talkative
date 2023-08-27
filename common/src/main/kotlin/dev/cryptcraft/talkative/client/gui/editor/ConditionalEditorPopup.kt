package dev.cryptcraft.talkative.client.gui.editor

import com.mojang.blaze3d.vertex.PoseStack
import dev.cryptcraft.talkative.api.conditional.Conditional
import dev.cryptcraft.talkative.api.conditional.ScoreboardExpression
import dev.cryptcraft.talkative.client.data.ConditionalContext
import dev.cryptcraft.talkative.client.gui.TalkativeScreen
import dev.cryptcraft.talkative.client.gui.editor.widgets.evaluable.ExpressionWidget
import dev.cryptcraft.talkative.client.gui.widgets.lists.WidgetList
import dev.cryptcraft.talkative.client.gui.widgets.popup.PopupWidget
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiComponent
import net.minecraft.client.gui.narration.NarrationElementOutput
import org.lwjgl.glfw.GLFW

class ConditionalEditorPopup(parent: TalkativeScreen, x: Int, y: Int, width: Int, height: Int, private val context: ConditionalContext, private val onSave: (context: ConditionalContext) -> Unit) : PopupWidget(x, y, width, height, parent) {
    private var entryList: WidgetList<*> = WidgetList(parent, x, y + 20, width, height - 20)
    private val removalList = ArrayList<ExpressionWidget>()

    init {
        entryList.renderBackground = false
        refresh()
        entryList.setSize(width - 5, height - 20)
        addChild(entryList)

        button(width - 23,4, "+", 16,16)  {
            val expression = ScoreboardExpression("", 0, ScoreboardExpression.Operation.EQUALS)
            entryList.addChild(ExpressionWidget(this, expression, width, 25, Minecraft.getInstance().font))
        }
    }

    private fun refresh() {
        context.refresh()
        context.conditional?.forEach {
            if (it is ScoreboardExpression) entryList.addChild(ExpressionWidget(this, it, width, 25, Minecraft.getInstance().font))
        }
    }

    override fun renderButton(poseStack: PoseStack, mouseX: Int, mouseY: Int, delta: Float) {
        super.renderButton(poseStack, mouseX, mouseY, delta)
        GuiComponent.drawString(poseStack, Minecraft.getInstance().font, "Objective", x + 8, y + 8, 0xFFFFFF)
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        when (keyCode) {
            GLFW.GLFW_KEY_ENTER, GLFW.GLFW_KEY_KP_ENTER, GLFW.GLFW_KEY_SPACE -> return true
        }
        return super.keyPressed(keyCode, scanCode, modifiers)
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, delta: Double): Boolean {
        return entryList.mouseScrolled(mouseX, mouseY, delta)
    }

    override fun updateNarration(narrationElementOutput: NarrationElementOutput) {
    }

    override fun recalculateChildren() {
        entryList.x = x
        entryList.y = y + 20
        entryList.width = width
        entryList.height = height - 20
    }

    override fun onClose() {
        context.conditional = createConditional()
        onSave.invoke(context)
    }

    override fun tick() {
        removalList.forEach {
            entryList.remove(it)
        }
    }

    fun deleteEntry(entry: ExpressionWidget) {
        removalList.add(entry)
    }

    private fun createConditional(): Conditional? {
        if (entryList.children.isEmpty())
            return null

        val conditional = Conditional()
        entryList.children.forEach { widget ->
            val expression = widget as ExpressionWidget
            expression.getModifiedEvaluable()?.let { conditional.add(it) }
        }
        return if (conditional.isEmpty()) null else conditional
    }
}