package dev.cryptcraft.talkative.forge

import net.minecraftforge.fml.loading.FMLPaths
import java.nio.file.Path

object TalkativePlatformImpl {

    val configDirectory: Path
        get() = FMLPaths.CONFIGDIR.get()

    val gameDirectory: Path
        get() = FMLPaths.GAMEDIR.get()

}