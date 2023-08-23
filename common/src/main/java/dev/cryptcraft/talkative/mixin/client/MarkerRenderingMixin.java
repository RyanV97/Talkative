package dev.cryptcraft.talkative.mixin.client;

import dev.cryptcraft.talkative.client.MarkerLayer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class MarkerRenderingMixin<T extends LivingEntity, M extends EntityModel<T>> {
    @Shadow protected abstract boolean addLayer(RenderLayer<T, M> layer);

    @Inject(method = "<init>", at = @At("RETURN"))
    private void addTalkativeLayers(EntityRendererProvider.Context context, EntityModel entityModel, float f, CallbackInfo ci) {
        this.addLayer(new MarkerLayer<>((LivingEntityRenderer<T, M>) (Object) this));
    }
}
