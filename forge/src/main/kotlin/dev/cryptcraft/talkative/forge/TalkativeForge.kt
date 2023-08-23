package dev.cryptcraft.talkative.forge

import dev.architectury.platform.forge.EventBuses
import dev.cryptcraft.talkative.Talkative
import net.minecraftforge.fml.common.Mod

@Mod(Talkative.MOD_ID)
object TalkativeForge {
    init {
        EventBuses.registerModEventBus(Talkative.MOD_ID, thedarkcolour.kotlinforforge.forge.MOD_CONTEXT.getKEventBus())
        Talkative.init()
    }
}