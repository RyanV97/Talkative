package ryanv.talkative.client.gui.editor

import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.EditBox
import net.minecraft.client.gui.screens.Screen
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.network.chat.TextComponent
import ryanv.talkative.client.gui.TalkativeScreen
import ryanv.talkative.client.gui.editor.widgets.ConditionalWidget
import ryanv.talkative.client.gui.widgets.lists.WidgetList
import ryanv.talkative.common.util.NBTConstants
import ryanv.talkative.common.network.NetworkHandler
import ryanv.talkative.common.network.serverbound.UpdateConditionalPacket

class ConditionalEditorScreen(parent: Screen?, val actorId: Int, val holderData: CompoundTag): TalkativeScreen(parent, TextComponent("Conditional Editor")) {
    private var priorityBox: EditBox? = null
    private var conditionalList: WidgetList<ConditionalEditorScreen>? = null
    private val pendingTasks = ArrayList<Runnable>()

    override fun init() {
        super.init()
        priorityBox = addButton(EditBox(font, width - 60, height - 20, 20, 20, TextComponent.EMPTY))
        if(conditionalList == null) {
            conditionalList = addButton(WidgetList(this, 0, 0, width, height - 20))
            deserializeConditional(holderData.getCompound(NBTConstants.CONDITIONAL))
        }

        addButton(Button(width - 20, height - 20, 20, 20, TextComponent("New")) {
            conditionalList?.addChild(ConditionalWidget(this, 0, 0, width, 30, font))
        })
        addButton(Button(width - 40, height - 20, 20, 20, TextComponent("Save")) {
            onSave()
        })
    }

    fun deleteEntry(entry: ConditionalWidget) {
        pendingTasks.add {
            conditionalList?.remove(entry)
        }
    }

    fun onSave() {
        holderData.put(NBTConstants.CONDITIONAL, serializeConditional(CompoundTag()))
        UpdateConditionalPacket(actorId, holderData).sendToServer()
    }

    fun serializeConditional(tag: CompoundTag): CompoundTag {
        tag.putInt(NBTConstants.CONDITIONAL_PRIORITY, priorityBox!!.value!!.toInt())

        val list = ListTag()
        conditionalList?.children?.forEach {
            val conditionalWidget = it as ConditionalWidget
            list.add(conditionalWidget.serialize(CompoundTag()))
        }
        tag.put(NBTConstants.CONDITIONAL_EXPRESSIONS, list)

        return tag
    }

    fun deserializeConditional(tag: CompoundTag) {
        priorityBox?.value = tag.getInt(NBTConstants.CONDITIONAL_PRIORITY).toString()

        val list = tag.getList(NBTConstants.CONDITIONAL_EXPRESSIONS, 10)
        list.forEach {
            conditionalList?.addChild(ConditionalWidget.deserialize(this, it as CompoundTag))
        }
    }

    override fun tick() {
        if(pendingTasks.isNotEmpty()) {
            for(task in pendingTasks) {
                task.run()
            }
            pendingTasks.clear()
        }
    }

    override fun onKeyPressed(keyCode: Int, j: Int, k: Int): Boolean {
        return false
    }

    override fun onCharTyped(char: Char, i: Int): Boolean {
        return false
    }

    override fun onMouseClick(mouseX: Double, mouseY: Double, mouseButton: Int): Boolean {
        return false
    }

    override fun onMouseDrag(mouseX: Double, mouseY: Double, mouseButton: Int, distanceX: Double, distanceY: Double): Boolean {
        return false
    }

    override fun onMouseRelease(mouseX: Double, mouseY: Double, mouseButton: Int): Boolean {
        return false
    }

    override fun onMouseScroll(mouseX: Double, mouseY: Double, scrollAmount: Double): Boolean {
        return false
    }
}