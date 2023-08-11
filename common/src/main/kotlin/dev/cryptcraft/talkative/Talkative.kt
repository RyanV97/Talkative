package dev.cryptcraft.talkative

import dev.architectury.registry.registries.DeferredRegister
import net.minecraft.core.Registry
import net.minecraft.world.item.Item
import dev.cryptcraft.talkative.common.item.ActorWandItem
import dev.cryptcraft.talkative.common.network.NetworkHandler
import dev.cryptcraft.talkative.server.events.EntityEventHandler
import dev.cryptcraft.talkative.server.events.PlayerEventHandler
import dev.cryptcraft.talkative.server.events.WorldEventHandler
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.HoverEvent.Action

object Talkative {
    const val MOD_ID = "talkative"

    private val ITEMS: DeferredRegister<Item> = DeferredRegister.create(MOD_ID, Registry.ITEM_REGISTRY)
    val ACTOR_WAND = ITEMS.register("actor_wand") { ActorWandItem() }

//    val TEST_ACTION = Action<>() //ToDo Add custom hover action for showing rolls etc.?

    @JvmStatic
    fun init() {
        ITEMS.register()
        EntityEventHandler.init()
        WorldEventHandler.init()
        PlayerEventHandler.init()
        NetworkHandler.init()
    }
}