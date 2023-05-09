package ryanv.talkative.client

import net.minecraft.client.Minecraft
import ryanv.talkative.client.gui.ActorDataScreen
import ryanv.talkative.client.gui.editor.ActorEditorScreen
import ryanv.talkative.common.data.ActorData

object TalkativeClient {
    var editingActorData: ActorData? = null
        set (value) {
            val screen = Minecraft.getInstance().screen
            if (screen is ActorDataScreen) screen.refresh()
            field = value
        }
}