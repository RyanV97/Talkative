package dev.cryptcraft.talkative.server.events

import dev.architectury.event.events.common.LifecycleEvent
import dev.cryptcraft.talkative.server.FileUtil
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.storage.LevelResource

object WorldEventHandler {

    fun init() {
        LifecycleEvent.SERVER_LEVEL_LOAD.register(WorldEventHandler::serverWorldLoadEvent)
    }

    private fun serverWorldLoadEvent(level: ServerLevel) {
        val worldDir = level.server.getWorldPath(LevelResource.ROOT)
        FileUtil.DIR_WORLD = worldDir

        FileUtil.DIR_BRANCHES = worldDir.resolve("talkative/branches")
        if (!FileUtil.DIR_BRANCHES!!.toFile().exists())
            FileUtil.DIR_BRANCHES!!.toFile().mkdirs()
    }

}