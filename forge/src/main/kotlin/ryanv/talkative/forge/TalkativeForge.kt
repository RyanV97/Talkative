package ryanv.talkative.forge

import ryanv.talkative.Talkative.init
import ryanv.talkative.Talkative
import me.shedaniel.architectury.platform.forge.EventBuses
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext

@Mod(Talkative.MOD_ID)
class TalkativeForge {

    init {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(Talkative.MOD_ID, FMLJavaModLoadingContext.get().modEventBus)
        init()
    }

}