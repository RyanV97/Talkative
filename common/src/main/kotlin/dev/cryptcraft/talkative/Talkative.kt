package dev.cryptcraft.talkative

import dev.architectury.registry.registries.DeferredRegister
import dev.cryptcraft.talkative.common.item.ActorWandItem
import dev.cryptcraft.talkative.common.network.NetworkHandler
import dev.cryptcraft.talkative.server.events.EntityEventHandler
import dev.cryptcraft.talkative.server.events.PlayerEventHandler
import dev.cryptcraft.talkative.server.events.WorldEventHandler
import net.minecraft.core.Registry
import net.minecraft.world.item.Item
import org.apache.logging.log4j.LogManager

object Talkative {
    val LOGGER = LogManager.getLogger()
    const val MOD_ID = "talkative"

    private val ITEMS: DeferredRegister<Item> = DeferredRegister.create(MOD_ID, Registry.ITEM_REGISTRY)
    val ACTOR_WAND = ITEMS.register("actor_wand") { ActorWandItem() }

    @JvmStatic
    fun init() {
        ITEMS.register()

        EntityEventHandler.init()
        PlayerEventHandler.init()
        WorldEventHandler.init()

        NetworkHandler.init()
    }
}