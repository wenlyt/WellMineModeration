package ru.nedan.mixin;

import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.nedan.event.impl.EventRender2D;

@Mixin(InGameHud.class)
public class MixinInGameHud {

    @Inject(at = @At("HEAD"), method = "render")
    public void injRender(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        new EventRender2D(matrices, tickDelta).call();
    }

}
