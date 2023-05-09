package ryanv.talkative.api

import ryanv.talkative.common.data.ActorData

interface ActorEntity {
    fun getActorData(): ActorData?
    fun setActorData(newActorData: ActorData?)
}