package ryanv.talkative.forge

import dev.architectury.platform.forge.EventBuses
import ryanv.talkative.Talkative.init
import ryanv.talkative.Talkative
import net.minecraftforge.fml.common.Mod

@Mod(Talkative.MOD_ID)
class TalkativeForge {

    init {
        EventBuses.registerModEventBus(Talkative.MOD_ID, thedarkcolour.kotlinforforge.forge.MOD_CONTEXT.getKEventBus())
        init()
    }

}