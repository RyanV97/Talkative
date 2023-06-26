package dev.cryptcraft.talkative.forge

import dev.architectury.platform.forge.EventBuses
import dev.cryptcraft.talkative.Talkative.init
import dev.cryptcraft.talkative.Talkative
import net.minecraftforge.fml.common.Mod

@Mod(dev.cryptcraft.talkative.Talkative.MOD_ID)
class TalkativeForge {

    init {
        EventBuses.registerModEventBus(dev.cryptcraft.talkative.Talkative.MOD_ID, thedarkcolour.kotlinforforge.forge.MOD_CONTEXT.getKEventBus())
        init()
    }

}