package dev.cryptcraft.talkative.api.actor

/**
 * Interface applied to *Living Entities* (via [dev.cryptcraft.talkative.mixin.entity.LivingEntityActorDataMixin])
 *
 * Used to store, get, and create ActorData.
 * Note that every Living Entity is an ActorEntity, but not every ActorEntity necessarily has ActorData.
 */
interface ActorEntity {
    /**
     * @return [ActorData] attached to this Entity if any exists, otherwise Null
     */
    fun getActorData(): ActorData?

    /**
     * @return Attached [ActorData], or creates, attaches, and returns a new [ActorData].
     */
    fun getOrCreateActorData(): ActorData

    /**
     * Sets the [ActorData] attached to this Entity
     * @param newActorData [ActorData] to be attached and/or replace current [ActorData]
     */
    fun setActorData(newActorData: ActorData?)
}