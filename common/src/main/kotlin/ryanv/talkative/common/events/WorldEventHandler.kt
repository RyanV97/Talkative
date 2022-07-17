package ryanv.talkative.common.events

import me.shedaniel.architectury.event.events.LifecycleEvent
import net.minecraft.world.level.storage.LevelResource
import ryanv.talkative.common.util.FileUtil

class WorldEventHandler {

    companion object {
        fun init() {
            LifecycleEvent.SERVER_WORLD_LOAD.register { level ->
                val worldDir = level.server.getWorldPath(LevelResource.ROOT)
                FileUtil.DIR_WORLD = worldDir
                FileUtil.DIR_BRANCH = worldDir.resolve("talkative/branches")
                if(!FileUtil.DIR_BRANCH.toFile().exists())
                    FileUtil.DIR_BRANCH.toFile().mkdirs()
            }
        }
    }

}