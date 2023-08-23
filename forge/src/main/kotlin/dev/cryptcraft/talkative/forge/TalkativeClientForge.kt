package dev.cryptcraft.talkative.forge

import dev.cryptcraft.talkative.Talkative
import dev.cryptcraft.talkative.client.TalkativeClient
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent

@Mod.EventBusSubscriber(modid = Talkative.MOD_ID, value = [Dist.CLIENT], bus = Mod.EventBusSubscriber.Bus.MOD)
object TalkativeClientForge {
    @SubscribeEvent
    fun clientInit(event: FMLClientSetupEvent) {
        TalkativeClient.init()
    }
}