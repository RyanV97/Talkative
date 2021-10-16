package ryanv.talkative

import me.shedaniel.architectury.registry.DeferredRegister
import net.minecraft.core.Registry
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.world.entity.LivingEntity
import ryanv.talkative.common.item.ActorWandItem
import ryanv.talkative.common.util.ActorUtil

object Talkative {

    const val MOD_ID = "talkative"

    val ITEMS = DeferredRegister.create(MOD_ID, Registry.ITEM_REGISTRY)
    val ACTOR_WAND = ITEMS.register("actor_wand") { ActorWandItem() }

    @JvmStatic
    fun init() {
        ITEMS.register()
    }

}