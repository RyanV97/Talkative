package ryanv.talkative.client

import net.minecraft.client.Minecraft
import net.minecraft.nbt.ListTag
import net.minecraft.world.entity.player.Player
import ryanv.talkative.api.IActorEntity
import ryanv.talkative.client.gui.DialogScreen
import ryanv.talkative.client.gui.editor.ActorEditorScreen
import ryanv.talkative.client.gui.editor.BranchDirectoryScreen
import ryanv.talkative.common.data.Actor

class TalkativeClient {

    companion object {
        fun openDialogScreen(entity: IActorEntity, player: Player) {
            Minecraft.getInstance().setScreen(DialogScreen())
        }

        fun openActorEditor(actorData: Actor) {
            Minecraft.getInstance().setScreen(ActorEditorScreen(actorData))
        }

        fun loadBranchList(list: ListTag?) {
            val screen = Minecraft.getInstance().screen
            if(screen is BranchDirectoryScreen)
                screen.loadBranchList(list)
        }
    }

}