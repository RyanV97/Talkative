package dev.cryptcraft.talkative.mixin.client;

import net.minecraft.client.gui.components.AbstractScrollWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractScrollWidget.class)
public interface AbstractScrollWidgetAccessor {
    @Accessor double getScrollAmount();
}
