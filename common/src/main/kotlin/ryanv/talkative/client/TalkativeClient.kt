package ryanv.talkative.client

import net.minecraft.client.Minecraft
import ryanv.talkative.client.gui.DataScreen
import ryanv.talkative.common.data.ActorData
import ryanv.talkative.common.data.tree.DialogBranch

object TalkativeClient {
    var editingActorData: ActorData? = null
        set(value) {
            field = value
            val screen = Minecraft.getInstance().screen
            if (screen is DataScreen) screen.refresh()
        }

    var editingBranch: DialogBranch? = null
        set(value) {
            field = value
            val screen = Minecraft.getInstance().screen
            if (screen is DataScreen) screen.refresh()
        }

    var editingBranchPath: String? = null
}