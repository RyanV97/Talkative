package ryanv.talkative

import me.shedaniel.architectury.registry.DeferredRegister
import net.minecraft.core.Registry
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.Item
import ryanv.talkative.common.events.EntityEventHandler
import ryanv.talkative.common.events.PlayerEventHandler
import ryanv.talkative.common.events.WorldEventHandler
import ryanv.talkative.common.item.ActorWandItem
import ryanv.talkative.common.network.NetworkHandler
import ryanv.talkative.common.util.ActorUtil

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