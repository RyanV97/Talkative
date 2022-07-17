package ryanv.talkative.client.gui.editor

import com.mojang.blaze3d.vertex.PoseStack
import io.netty.buffer.Unpooled
import me.shedaniel.architectury.networking.NetworkManager
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.screens.Screen
import net.minecraft.nbt.ListTag
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.chat.TextComponent
import ryanv.talkative.api.client.gui.IListHandler
import ryanv.talkative.client.gui.TalkativeScreen
import ryanv.talkative.client.gui.popup.PopupWidget
import ryanv.talkative.client.gui.widgets.StringSelectionList
import ryanv.talkative.common.network.NetworkHandler

class BranchDirectoryScreen(parent: Screen?, private var onConfirm: (selection: StringSelectionList.Entry?) -> Unit): TalkativeScreen(parent, TextComponent.EMPTY), IListHandler<StringSelectionList.Entry> {

    lateinit var list: StringSelectionList
    var selectedEntry: StringSelectionList.Entry? = null

    override fun init() {
        var listRight = width - (width / 3)
        list = addWidget(StringSelectionList(minecraft, this, 0, height, listRight))
        addButton(Button(width - 50, height - 20, 50, 20, TextComponent("Confirm")) {
            onConfirm(selectedEntry)
            onClose()
        })
        addButton(Button(listRight, height - 20, 70, 20, TextComponent("New Branch")) {
            popup = PopupWidget((width / 2) - 125, (height / 2) - 15, 250, 30, this)
                .textField(5, 5, width = 190, defaultString = getSelectedPath())
                .button(200, 5, "Save") {
                    createBranch(popup!!.getAllTextFields()[0].value)
                }
        })
        NetworkManager.sendToServer(NetworkHandler.Both_SyncBranchList, FriendlyByteBuf(Unpooled.buffer()))
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

    fun createBranch(label: String) {
        list.addEntry(label)
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

    override fun onSelectionChange(selection: StringSelectionList.Entry) {
        this.selectedEntry = selection
    }

}