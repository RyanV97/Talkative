package dev.cryptcraft.talkative.client.gui

interface Animatable {
    fun animate(delta: Float)
    fun isAnimating(): Boolean
    fun getAnimationProgress(): Float
    fun onFinishAnimating() {}
}