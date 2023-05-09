package ryanv.talkative.client.gui.editor

import com.mojang.blaze3d.vertex.PoseStack
import kotlinx.coroutines.Runnable
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiComponent
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.TextComponent
import org.lwjgl.glfw.GLFW
import ryanv.talkative.client.gui.ActorDataScreen
import ryanv.talkative.client.gui.TalkativeScreen
import ryanv.talkative.client.gui.editor.widgets.evaluable.ExpressionWidget
import ryanv.talkative.client.gui.widgets.lists.WidgetList
import ryanv.talkative.client.util.ConditionalContext
import ryanv.talkative.common.data.conditional.Conditional
import ryanv.talkative.common.data.conditional.IntExpression
import ryanv.talkative.common.network.serverbound.UpdateBranchConditionalPacket
import ryanv.talkative.common.network.serverbound.UpdateNodeConditionalPacket

class ConditionalEditorScreen(parent: Screen?, private val context: ConditionalContext) : TalkativeScreen(parent, TextComponent("Conditional Editor")), ActorDataScreen {
    private var entryList: WidgetList<ConditionalEditorScreen> = WidgetList(this, 0, 20, 0, 0)
    private val pendingTasks = ArrayList<Runnable>()

    init {
        entryList.renderBackground = false
        refresh()
    }

    override fun init() {
        super.init()
        entryList.setSize(width - 5, height - 20)
        addButton(entryList)

        addButton(Button(width - 20,2,15,15, TextComponent("+").withStyle { it.withColor(ChatFormatting.GREEN) }) {
            val expression = IntExpression("", 0, IntExpression.Operation.EQUALS)
            entryList.addChild(ExpressionWidget(this, expression, width, 25, Minecraft.getInstance().font))
        })

        addButton(Button(width - 60, 0, 40, 20, TextComponent("Save")) {
            when (context) {
                is ConditionalContext.BranchConditionalContext -> UpdateBranchConditionalPacket(context.actorId, context.branchIndex, createConditional())
                is ConditionalContext.NodeConditionalContext -> UpdateNodeConditionalPacket(context.branchPath, context.nodeId, createConditional())
                else -> null
            }?.sendToServer()
            onClose()
        })
    }

    override fun refresh() {
        context.refresh()
        context.conditional?.forEach {
            if (it is IntExpression) entryList.addChild(ExpressionWidget(this, it, width, 25, Minecraft.getInstance().font))
            //else  entryList?.addChild(ExpressionWidget(this, , width, 50, Minecraft.getInstance().font)) - Sub-Conditionals
        }
    }

    override fun render(poseStack: PoseStack?, mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground(poseStack)
        fill(poseStack, 0, 0, width, 20, -1072689136)
        GuiComponent.drawString(poseStack, minecraft?.font, "Scoreboard Objective", 5, 7, 0xFFFFFF)
        GuiComponent.drawString(poseStack, minecraft?.font, "Value", 160, 7, 0xFFFFFF)
        super.render(poseStack, mouseX, mouseY, delta)
    }

    override fun onKeyPressed(keyCode: Int, j: Int, k: Int): Boolean {
        when (keyCode) {
            GLFW.GLFW_KEY_ESCAPE, GLFW.GLFW_KEY_ENTER, GLFW.GLFW_KEY_KP_ENTER, GLFW.GLFW_KEY_SPACE -> return true
        }
        return false
    }

    fun createConditional(): Conditional? {
        if (entryList.children.isEmpty())
            return null

        val conditional = Conditional()
        entryList.children.forEach { widget ->
            val expression = widget as ExpressionWidget
            expression.getModifiedEvaluable()?.let { conditional.add(it) }
        }
        return if (conditional.isEmpty()) null else conditional
    }

    override fun tick() {
        pendingTasks.forEach { it.run() }
    }

    fun deleteEntry(entry: ExpressionWidget) {
        pendingTasks.add { entryList.remove(entry) }
    }
}