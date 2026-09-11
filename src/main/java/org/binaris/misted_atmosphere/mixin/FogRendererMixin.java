package org.binaris.misted_atmosphere.mixin;

import net.minecraft.client.Camera;
import net.minecraft.client.renderer.FogRenderer;
import org.binaris.misted_atmosphere.client.MistClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FogRenderer.class)
public abstract class FogRendererMixin {

    @Inject(method = "setupFog", at = @At("TAIL"))
    private static void mistedAtmosphere$applyMistDensity(Camera camera, FogRenderer.FogMode mode, float farPlaneDistance, boolean nearFog, float partialTick, CallbackInfo ci) {
        MistClient.applyMistDensity(camera);
    }
}