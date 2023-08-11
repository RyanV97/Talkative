package dev.cryptcraft.talkative.common

import dev.cryptcraft.talkative.api.actor.markers.Marker
import it.unimi.dsi.fastutil.ints.Int2ReferenceOpenHashMap

object MarkerManager {
    val loadedMarkers = Int2ReferenceOpenHashMap<Marker>()
}