package dev.cryptcraft.talkative.mixin.client;

import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.util.Map;

@Mixin(ModelBakery.class)
public abstract class ModelBakeryMixin {
    @Shadow @Final private Map<ResourceLocation, UnbakedModel> unbakedCache;
    @Shadow @Final private ResourceManager resourceManager;
    @Shadow protected abstract BlockModel loadBlockModel(ResourceLocation location) throws IOException;
    @Shadow protected abstract void loadTopLevel(ModelResourceLocation location);

    @Shadow @Final private Map<ResourceLocation, UnbakedModel> topLevelModels;
    private boolean bakedTalkativeModels = false;

    @Inject(method = "loadTopLevel", at = @At("HEAD"))
    private void bakeTalkativeModels(ModelResourceLocation location, CallbackInfo ci) {
        if (!bakedTalkativeModels) {
            Map<ResourceLocation, Resource> markers = resourceManager.listResources("models/markers", resourceLocation -> resourceLocation.getPath().endsWith(".json"));
            for (ResourceLocation resourceLocation : markers.keySet()) {
                try {
                    ModelResourceLocation modelLocation = new ModelResourceLocation(
                            new ResourceLocation(
                                    resourceLocation.getNamespace(),
                                    resourceLocation.getPath().replace("models/", "").replace(".json", "")
                            ),
                            "marker"
                    );
                    BlockModel blockModel = loadBlockModel(modelLocation);
                    this.unbakedCache.put(modelLocation, blockModel);
                    this.topLevelModels.put(modelLocation, blockModel);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            bakedTalkativeModels = true;
        }
    }
}
