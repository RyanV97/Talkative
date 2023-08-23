package dev.cryptcraft.talkative.api.actor.markers

/**
 * Client-Side interface for attaching a marker to a LivingEntity for rendering.
 */
interface MarkerEntity {
    fun getMarker(): Marker?
    fun setMarker(marker: Marker?)
}