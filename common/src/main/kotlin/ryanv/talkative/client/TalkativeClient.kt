package ryanv.talkative.client

import net.minecraft.client.Minecraft
import ryanv.talkative.client.gui.DataScreen
import ryanv.talkative.common.data.ActorData
import ryanv.talkative.common.data.tree.DialogBranch

object TalkativeClient {
    var editingActorData: ActorData? = null
        set(value) {
            println("Client Actor Data changed. From: $field to $value")
            val screen = Minecraft.getInstance().screen
            if (screen is DataScreen) screen.refresh()
            field = value
        }
    var editingBranch: DialogBranch? = null
        set(value) {
            println("Client Branch changed. From: $field to $value")
            val screen = Minecraft.getInstance().screen
            if (screen is DataScreen) screen.refresh()
            field = value
        }
    var editingBranchPath: String? = null
}