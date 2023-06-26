package dev.cryptcraft.talkative.fabric

import dev.cryptcraft.talkative.Talkative.init
import net.fabricmc.api.ModInitializer

class TalkativeFabric : ModInitializer {

    override fun onInitialize() {
        init()
    }

}