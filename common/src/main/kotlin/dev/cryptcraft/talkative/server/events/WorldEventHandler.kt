package dev.cryptcraft.talkative.server.events

import dev.architectury.event.events.common.LifecycleEvent
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.storage.LevelResource
import dev.cryptcraft.talkative.common.util.FileUtil

object WorldEventHandler {

    fun init() {
        LifecycleEvent.SERVER_LEVEL_LOAD.register(WorldEventHandler::serverWorldLoadEvent)
    }

    private fun serverWorldLoadEvent(level: ServerLevel) {
        val worldDir = level.server.getWorldPath(LevelResource.ROOT)
        FileUtil.DIR_WORLD = worldDir
        FileUtil.DIR_BRANCH = worldDir.resolve("talkative/branches")
        if (!FileUtil.DIR_BRANCH.toFile().exists())
            FileUtil.DIR_BRANCH.toFile().mkdirs()
    }

}