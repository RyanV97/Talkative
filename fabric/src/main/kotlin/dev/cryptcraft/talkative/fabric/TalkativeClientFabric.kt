package dev.cryptcraft.talkative.fabric

import dev.cryptcraft.talkative.client.TalkativeClient
import net.fabricmc.api.ClientModInitializer

object TalkativeClientFabric : ClientModInitializer {
    override fun onInitializeClient() {
        TalkativeClient.init()
    }
}