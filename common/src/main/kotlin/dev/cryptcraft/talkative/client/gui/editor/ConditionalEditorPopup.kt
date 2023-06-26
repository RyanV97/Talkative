package dev.cryptcraft.talkative.client.gui.editor

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiComponent
import net.minecraft.client.gui.narration.NarrationElementOutput
import org.lwjgl.glfw.GLFW
import dev.cryptcraft.talkative.client.gui.TalkativeScreen
import dev.cryptcraft.talkative.client.gui.editor.widgets.evaluable.ExpressionWidget
import dev.cryptcraft.talkative.client.gui.widgets.lists.WidgetList
import dev.cryptcraft.talkative.client.gui.widgets.popup.PopupWidget
import dev.cryptcraft.talkative.client.data.ConditionalContext
import dev.cryptcraft.talkative.common.data.conditional.Conditional
import dev.cryptcraft.talkative.common.data.conditional.IntExpression

class ConditionalEditorPopup(parent: TalkativeScreen, x: Int, y: Int, width: Int, height: Int, private val context: ConditionalContext, val onSave: (context: ConditionalContext) -> Unit) : PopupWidget(x, y, width, height, parent) {
    private var entryList: WidgetList<*> = WidgetList(parent, x, y + 20, width, height - 20)
    private val pendingTasks = ArrayList<Runnable>()

    init {
        entryList.renderBackground = false
        refresh()
        entryList.setSize(width - 5, height - 20)
        addChild(entryList)

        button(width - 20,2, "+", 15,15)  {
            val expression = IntExpression("", 0, IntExpression.Operation.EQUALS)
            entryList.addChild(ExpressionWidget(this, expression, width, 25, Minecraft.getInstance().font))
        }

        button(width - 60, 0, "Save", 40, 20) {
            context.conditional = createConditional()
            onSave.invoke(context)
        }
    }

    private fun refresh() {
        context.refresh()
        context.conditional?.forEach {
            if (it is IntExpression) entryList.addChild(ExpressionWidget(this, it, width, 25, Minecraft.getInstance().font))
        }
    }

    override fun renderButton(poseStack: PoseStack, mouseX: Int, mouseY: Int, delta: Float) {
        fill(poseStack, 0, 0, width, 20, -1072689136)
        super.renderButton(poseStack, mouseX, mouseY, delta)
        GuiComponent.drawString(poseStack, Minecraft.getInstance().font, "Objective", x + 4, y + 5, 0xFFFFFF)
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        when (keyCode) {
            GLFW.GLFW_KEY_ESCAPE, GLFW.GLFW_KEY_ENTER, GLFW.GLFW_KEY_KP_ENTER, GLFW.GLFW_KEY_SPACE -> return true
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

    override fun tick() {
        pendingTasks.forEach { it.run() }
    }

    fun deleteEntry(entry: ExpressionWidget) {
        pendingTasks.add { entryList.remove(entry) }
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