package ryanv.talkative

import dev.architectury.registry.registries.DeferredRegister
import net.minecraft.core.Registry
import net.minecraft.world.item.Item
import ryanv.talkative.common.item.ActorWandItem
import ryanv.talkative.common.network.NetworkHandler
import ryanv.talkative.server.events.EntityEventHandler
import ryanv.talkative.server.events.PlayerEventHandler
import ryanv.talkative.server.events.WorldEventHandler

object Talkative {
    const val MOD_ID = "talkative"

    private val ITEMS: DeferredRegister<Item> = DeferredRegister.create(MOD_ID, Registry.ITEM_REGISTRY)
    val ACTOR_WAND = ITEMS.register("actor_wand") { ActorWandItem() }

    @JvmStatic
    fun init() {
        ITEMS.register()
        EntityEventHandler.init()
        WorldEventHandler.init()
        PlayerEventHandler.init()
        NetworkHandler.init()
    }
}