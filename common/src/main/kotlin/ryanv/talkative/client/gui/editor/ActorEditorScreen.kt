package ryanv.talkative.client.gui.editor

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiComponent
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.EditBox
import net.minecraft.network.chat.TextComponent
import net.minecraft.world.entity.LivingEntity
import ryanv.talkative.api.IConditional
import ryanv.talkative.client.TalkativeClient
import ryanv.talkative.client.gui.TalkativeScreen
import ryanv.talkative.client.gui.widgets.NestedWidget
import ryanv.talkative.client.gui.widgets.lists.TalkativeList
import ryanv.talkative.client.gui.widgets.lists.WidgetList
import ryanv.talkative.common.data.Actor
import ryanv.talkative.common.data.tree.BranchReference
import ryanv.talkative.common.network.NetworkHandler
import ryanv.talkative.common.network.bi.OpenBranchEditorPacket_C2S
import ryanv.talkative.common.network.c2s.AddBranchPacket
import ryanv.talkative.common.network.c2s.RemoveBranchPacket
import ryanv.talkative.common.network.s2c.OpenConditionalEditorPacket

class ActorEditorScreen(val actorEntity: LivingEntity, val actor: Actor? = null): TalkativeScreen(null, TextComponent("Actor Editor")) {
    private var list: TalkativeList<BranchListEntry>? = null
    private var details: BranchDetailsWidget? = null

    override fun init() {
        super.init()
        addButton(Button(5, height - 25, 75, 20, TextComponent("Add Branch")) {
            minecraft?.setScreen(BranchDirectoryScreen(this) {
                if(it!!.value.isNotBlank())
                    addBranchToActor(it.value)
            })
//            popup = PopupWidget((width / 2) - 200, (height / 2) - 100, 400, 200, this, "New Branch")
//                .title(TextComponent("Add Branch"))
//                .label(0, 11, "Branch File Location:")
//                .button(235, 5, "...", width = 20) {
//                    minecraft?.setScreen(BranchDirectoryScreen(this) {
//                        popup!!.getAllTextFields()[0].value = it?.value
//                    })
//                }
//                .textField(110, 5)
//                .button(0, 180, "Cancel") { closePopup() }
//                .button(350, 180, "Confirm") {
//                    val path = popup!!.getAllTextFields()[0].value
//                    addBranchToActor(path)
//                }
        })

        list = addWidget(TalkativeList(minecraft, 0, height - 30, 0, width / 2))
        list?.enableBackground = false

        details = addButton(BranchDetailsWidget(this, font, width / 2, 0, width / 2, height))

        populateBranchList()
    }

    private fun populateBranchList() {
        actor?.dialogBranches?.forEach {
            list?.addEntry(BranchListEntry(minecraft!!, list!!, it, ::onSelectionChange))
        }
    }

    override fun render(poseStack: PoseStack?, mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground(poseStack)
        list?.render(poseStack, mouseX, mouseY, delta)
        super.render(poseStack, mouseX, mouseY, delta)
    }

    override fun renderBackground(poseStack: PoseStack?) {
        fill(poseStack, 0, 0, width, height, -1072689136)
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

    override fun onKeyPressed(keyCode: Int, j: Int, k: Int): Boolean {
        return false
    }

    override fun onCharTyped(char: Char, i: Int): Boolean {
        return false
    }

    private fun onSelectionChange(entry: TalkativeList.TalkativeListEntry) {
        details?.branch = (entry as BranchListEntry).value
    }

    private fun addBranchToActor(path: String) {
        NetworkHandler.CHANNEL.sendToServer(AddBranchPacket(actorEntity.id, path))
    }

    class BranchListEntry(minecraft: Minecraft, parent: TalkativeList<BranchListEntry>, val value: BranchReference, onSelectionChange: ((selection: TalkativeList.TalkativeListEntry) -> Unit?)?): TalkativeList.TalkativeListEntry(minecraft, parent, onSelectionChange) {
        override fun render(poseStack: PoseStack?, index: Int, top: Int, left: Int, width: Int, height: Int, mouseX: Int, mouseY: Int, isMouseOver: Boolean, partialTicks: Float) {
            val color: Int = if(parent.selectedEntry == this) 0xFF5555FF.toInt() else if(isMouseOver) 0xFF55FF55.toInt() else 0xFFFFFFFF.toInt()
            GuiComponent.drawString(poseStack, minecraft.font, value.fileString, left + 1, top + 2, color)
        }
    }

    class BranchDetailsWidget(val parent: ActorEditorScreen, private val font: Font, x: Int, y: Int, width: Int, height: Int): NestedWidget(x, y, width, height, TextComponent("Branch Details")) {
        var branch: BranchReference? = null
            set(value) {
                field = value
                repopulateDetails()
            }
        private val conditionalList: WidgetList<ActorEditorScreen>

        init {
            conditionalList = addChild(WidgetList(parent, x, y + 10, width, height - 10))
        }

        private fun repopulateDetails() {
            conditionalList.clear()
            children.clear()
            addChild(Button(x, 0, 50, 20, TextComponent("Edit")) {
                NetworkHandler.CHANNEL.sendToServer(OpenBranchEditorPacket_C2S(branch!!.fileString))
            })
            addChild(Button(x, 40, 100, 20, TextComponent("Edit Conditional")) {
                val holderData = (branch as IConditional).data
                NetworkHandler.CHANNEL.sendToServer(OpenConditionalEditorPacket(parent.actorEntity.id, holderData))
            })
            addChild(Button(x, 20, 50, 20, TextComponent("Remove")) {
                NetworkHandler.CHANNEL.sendToServer(RemoveBranchPacket(parent.actorEntity.id, parent.list!!.indexOf(parent.list!!.selectedEntry)))
            })
        }
    }
}