package dev.cryptcraft.talkative

import dev.architectury.registry.registries.DeferredRegister
import net.minecraft.core.Registry
import net.minecraft.world.item.Item
import dev.cryptcraft.talkative.common.item.ActorWandItem
import dev.cryptcraft.talkative.common.network.NetworkHandler
import dev.cryptcraft.talkative.server.events.EntityEventHandler
import dev.cryptcraft.talkative.server.events.PlayerEventHandler
import dev.cryptcraft.talkative.server.events.WorldEventHandler

object Talkative {
    const val MOD_ID = "talkative"

    private val ITEMS: DeferredRegister<Item> = DeferredRegister.create(dev.cryptcraft.talkative.Talkative.MOD_ID, Registry.ITEM_REGISTRY)
    val ACTOR_WAND = dev.cryptcraft.talkative.Talkative.ITEMS.register("actor_wand") { ActorWandItem() }

    @JvmStatic
    fun init() {
        dev.cryptcraft.talkative.Talkative.ITEMS.register()
        EntityEventHandler.init()
        WorldEventHandler.init()
        PlayerEventHandler.init()
        NetworkHandler.init()
    }
}