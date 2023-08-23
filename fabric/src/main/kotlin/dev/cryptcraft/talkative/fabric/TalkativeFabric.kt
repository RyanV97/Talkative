package dev.cryptcraft.talkative.fabric

import dev.cryptcraft.talkative.Talkative
import net.fabricmc.api.ModInitializer

object TalkativeFabric : ModInitializer {

    override fun onInitialize() {
        Talkative.init()
    }

}