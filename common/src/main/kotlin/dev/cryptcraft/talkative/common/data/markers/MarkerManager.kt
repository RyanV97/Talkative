package dev.cryptcraft.talkative.common.data.markers

import it.unimi.dsi.fastutil.ints.Int2ReferenceOpenHashMap

object MarkerManager {
    val loadedMarkers = Int2ReferenceOpenHashMap<Marker>()
}