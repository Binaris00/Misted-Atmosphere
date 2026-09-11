package org.binaris.misted_atmosphere.mixin;

import net.minecraft.world.level.dimension.DimensionType;
import org.binaris.misted_atmosphere.Config;
import org.binaris.misted_atmosphere.moon.MoonCalendar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DimensionType.class)
public abstract class DimensionTypeMixin {

    @Inject(method = "moonPhase", at = @At("HEAD"), cancellable = true)
    private void mistedAtmosphere$moonPhase(long dayTime, CallbackInfoReturnable<Integer> cir) {
        if (Config.moonCycleEnabled) {
            cir.setReturnValue(MoonCalendar.phaseFor(dayTime));
        }
    }
}
