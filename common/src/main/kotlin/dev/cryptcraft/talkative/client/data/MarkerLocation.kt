package dev.cryptcraft.talkative.client.data

import net.minecraft.resources.ResourceLocation

class MarkerLocation(namespace: String, path: String) : ResourceLocation(namespace, path) {
    constructor(location: ResourceLocation) : this(location.namespace, location.path)

    fun getOutlineLocation(): ResourceLocation {
        return ResourceLocation(namespace, path.replaceAfterLast(".png", "_outline.png"))
    }
}
