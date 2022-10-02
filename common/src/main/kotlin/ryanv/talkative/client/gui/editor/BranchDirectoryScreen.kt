package ryanv.talkative.client.gui.editor

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.screens.Screen
import net.minecraft.nbt.ListTag
import net.minecraft.network.chat.TextComponent
import ryanv.talkative.client.gui.TalkativeScreen
import ryanv.talkative.client.gui.popup.PopupWidget
import ryanv.talkative.client.gui.widgets.lists.StringSelectionList
import ryanv.talkative.client.gui.widgets.lists.TalkativeList
import ryanv.talkative.common.network.NetworkHandler
import ryanv.talkative.common.network.bi.SyncBranchListPacket
import ryanv.talkative.common.network.c2s.CreateBranchPacket

class BranchDirectoryScreen(parent: Screen?, private var onConfirm: (selection: StringSelectionList.StringEntry?) -> Unit): TalkativeScreen(parent, TextComponent.EMPTY) {
    lateinit var list: StringSelectionList
    var selectedEntry: StringSelectionList.StringEntry? = null

    override fun init() {
        super.init()
        var listRight = width - (width / 3)
        list = addWidget(StringSelectionList(minecraft, 0, height, 0, listRight, ::onSelectionChange))

        addButton(Button(width - 50, height - 20, 50, 20, TextComponent("Confirm")) {
            onConfirm(selectedEntry)
            onClose()
        })

        addButton(Button(listRight, height - 20, 70, 20, TextComponent("New Branch")) {
            popup = PopupWidget((width / 2) - 155, (height / 2) - 15, 310, 30, this)
                .textField(5, 5, width = 195, defaultString = getSelectedPath())
                .button(205, 5, "Save") {
                    createBranch(popup!!.getAllTextFields()[0].value)
                }
                .button(259, 5, "Cancel") {
                    closePopup()
                }
        })

        NetworkHandler.CHANNEL.sendToServer(SyncBranchListPacket(null))
    }

    override fun render(poseStack: PoseStack?, mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground(poseStack)
        list.render(poseStack, mouseX, mouseY, delta)
        super.render(poseStack, mouseX, mouseY, delta)
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

    override fun onClose() {
        minecraft!!.setScreen(parent)
    }

    fun createBranch(path: String) {
        NetworkHandler.CHANNEL.sendToServer(CreateBranchPacket(path))
        closePopup()
    }

    fun getSelectedPath(): String {
        if(selectedEntry != null) return selectedEntry!!.value else return ""
    }

    fun loadBranchList(list: ListTag?) {
        this.list.children().clear()
        list?.forEach {
            this.list.addEntry(it.asString)
        }
    }

    fun onSelectionChange(selection: TalkativeList.TalkativeListEntry?) {
        this.selectedEntry = selection as StringSelectionList.StringEntry?
    }

}