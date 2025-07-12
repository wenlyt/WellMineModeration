package ru.nedan.mixin;

import net.minecraft.client.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.nedan.event.impl.EventPress;

@Mixin(Keyboard.class)
public class MixinKeyboard {

    @Inject(at = @At("HEAD"), method = "onKey")
    private void injOnKey(long window, int key, int scancode, int i, int modifiers, CallbackInfo ci) {
        new EventPress(key, i).call();
    }

}
