package ryanv.talkative

import dev.architectury.injectables.annotations.ExpectPlatform
import java.nio.file.Path

object TalkativePlatform {

    @JvmStatic
    @get:ExpectPlatform
    val configDirectory: Path?
        get() = null

}