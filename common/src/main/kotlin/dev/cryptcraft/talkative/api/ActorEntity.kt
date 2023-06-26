package dev.cryptcraft.talkative.api

import dev.cryptcraft.talkative.common.data.ActorData

interface ActorEntity {
    fun getActorData(): ActorData?
    fun getOrCreateActorData(): ActorData
    fun setActorData(newActorData: ActorData?)
}