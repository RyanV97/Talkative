package ryanv.talkative.client

import net.minecraft.client.Minecraft
import net.minecraft.nbt.ListTag
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import ryanv.talkative.api.IActorEntity
import ryanv.talkative.client.gui.DialogScreen
import ryanv.talkative.client.gui.TalkativeScreen
import ryanv.talkative.client.gui.editor.ActorEditorScreen
import ryanv.talkative.client.gui.editor.BranchDirectoryScreen
import ryanv.talkative.client.gui.editor.BranchEditorScreen
import ryanv.talkative.common.data.Actor
import ryanv.talkative.common.data.tree.DialogBranch
import ryanv.talkative.common.network.s2c.DialogPacket

class TalkativeClient {

    companion object {
        fun openDialogScreen(entity: IActorEntity, player: Player) {
            Minecraft.getInstance().setScreen(DialogScreen())
        }

        fun openActorEditor(entity: LivingEntity, actorData: Actor) {
            Minecraft.getInstance().setScreen(ActorEditorScreen(entity, actorData))
        }

        fun openBranchEditor(path: String, branch: DialogBranch) {
            var parent: TalkativeScreen? = null
            if(Minecraft.getInstance().screen is ActorEditorScreen)
                parent = Minecraft.getInstance().screen as ActorEditorScreen
            Minecraft.getInstance().setScreen(BranchEditorScreen(parent, path, branch))
        }

        fun loadBranchList(list: ListTag?) {
            val screen = Minecraft.getInstance().screen
            if(screen is BranchDirectoryScreen)
                screen.loadBranchList(list)
        }

        fun processDialogPacket(packet: DialogPacket) {
            val currentScreen = Minecraft.getInstance().screen
            if(currentScreen is DialogScreen)
                currentScreen.loadDialog(packet.speaker, packet.contents, packet.responses)
        }
    }

}