package ryanv.talkative.api;

import ryanv.talkative.common.data.ActorData;

public interface ActorEntity {
    ActorData getActorData();
    void setActorData(ActorData serverActorDataData);
}
