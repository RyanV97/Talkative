package dev.cryptcraft.talkative.fabric

import net.fabricmc.loader.api.FabricLoader
import java.nio.file.Path

object TalkativePlatformImpl {

    val configDirectory: Path
        get() = FabricLoader.getInstance().configDir

    val gameDirectory: Path
        get() = FabricLoader.getInstance().gameDir

}