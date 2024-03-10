package dev.cryptcraft.talkative.server.events

import dev.architectury.event.events.common.LifecycleEvent
import dev.cryptcraft.talkative.server.FileUtil
import dev.cryptcraft.talkative.server.TalkativeWorldConfig
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.storage.LevelResource

object WorldEventHandler {
    fun init() {
        LifecycleEvent.SERVER_LEVEL_LOAD.register(WorldEventHandler::serverWorldLoadEvent)
        LifecycleEvent.SERVER_LEVEL_SAVE.register(WorldEventHandler::serverWorldSaveEvent)
    }

    private fun serverWorldLoadEvent(level: ServerLevel) {
        val worldDir = level.server.getWorldPath(LevelResource.ROOT)
        FileUtil.DIR_WORLD = worldDir
        FileUtil.DIR_TALKATIVE = worldDir.resolve("talkative")
        FileUtil.DIR_BRANCHES = FileUtil.DIR_TALKATIVE?.resolve("branches")

        TalkativeWorldConfig.load()
    }

    private fun serverWorldSaveEvent(serverLevel: ServerLevel?) {
        TalkativeWorldConfig.save()
    }
}