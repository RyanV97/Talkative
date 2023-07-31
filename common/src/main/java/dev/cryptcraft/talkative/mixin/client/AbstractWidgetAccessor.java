package dev.cryptcraft.talkative.mixin.client;

import net.minecraft.client.gui.components.AbstractWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractWidget.class)
public interface AbstractWidgetAccessor {
    @Invoker("setFocused") void pleaseSetFocused(boolean focused);
}
