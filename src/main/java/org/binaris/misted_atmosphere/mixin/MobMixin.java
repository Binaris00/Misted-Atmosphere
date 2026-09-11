package org.binaris.misted_atmosphere.mixin;

import net.minecraft.world.entity.Mob;
import org.binaris.misted_atmosphere.Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public abstract class MobMixin {

    @Inject(method = "isSunBurnTick", at = @At("HEAD"), cancellable = true)
    private void mistedAtmosphere$preventSunBurn(CallbackInfoReturnable<Boolean> cir) {
        if (Config.noSunBurn) {
            cir.setReturnValue(false);
        }
    }
}
