package ru.nedan.mixin;

import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.nedan.event.impl.EventPress;

@Mixin(Mouse.class)
public class MixinMouse {

    @Inject(at = @At("HEAD"), method = "onMouseButton")
    private void onMouse(long window, int button, int action, int mods, CallbackInfo ci) {
        new EventPress(button + 400, action).call();
    }

}
