package dev.cryptcraft.talkative.client

import net.minecraft.client.Minecraft
import dev.cryptcraft.talkative.client.gui.DataScreen
import dev.cryptcraft.talkative.common.data.ActorData
import dev.cryptcraft.talkative.common.data.tree.DialogBranch

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