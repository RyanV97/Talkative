package ryanv.talkative.forge

import ryanv.talkative.Talkative.init
import ryanv.talkative.Talkative
import me.shedaniel.architectury.platform.forge.EventBuses
import net.minecraftforge.fml.common.Mod

@Mod(Talkative.MOD_ID)
class TalkativeForge {

    init {
        EventBuses.registerModEventBus(Talkative.MOD_ID, thedarkcolour.kotlinforforge.forge.MOD_CONTEXT.getKEventBus())
        init()
    }

}