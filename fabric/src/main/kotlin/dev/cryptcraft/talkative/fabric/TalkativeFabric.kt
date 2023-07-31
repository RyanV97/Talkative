package dev.cryptcraft.talkative.fabric

import dev.cryptcraft.talkative.Talkative
import net.fabricmc.api.ModInitializer

class TalkativeFabric : ModInitializer {

    override fun onInitialize() {
        Talkative.init()
    }

}