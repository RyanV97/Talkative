package ryanv.talkative.api

import net.minecraft.server.level.ServerPlayer

interface Evaluable {
    fun not(): Boolean
    fun or(): Boolean
    fun eval(player: ServerPlayer): Boolean
}