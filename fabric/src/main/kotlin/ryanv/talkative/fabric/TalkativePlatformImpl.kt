package ryanv.talkative.fabric

import net.fabricmc.loader.api.FabricLoader
import java.nio.file.Path

object TalkativePlatformImpl {

    val configDirectory: Path
        get() = FabricLoader.getInstance().configDir

}