package org.binaris.misted_atmosphere.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.phys.Vec3;
import org.binaris.misted_atmosphere.client.MistClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin {

    @Inject(method = "getSkyColor(Lnet/minecraft/world/phys/Vec3;F)Lnet/minecraft/world/phys/Vec3;", at = @At("RETURN"), cancellable = true)
    private void mistedAtmosphere$matchFogSkyColor(Vec3 cameraPos, float partialTick, CallbackInfoReturnable<Vec3> cir) {
        ClientLevel level = (ClientLevel) (Object) this;
        Vec3 skyColor = cir.getReturnValue();
        float[] transformed = MistClient.transformFogColor(level, cameraPos, (float) skyColor.x, (float) skyColor.y, (float) skyColor.z);
        if (transformed != null) {
            cir.setReturnValue(new Vec3(transformed[0], transformed[1], transformed[2]));
        }
    }

    @Inject(method = "getCloudColor(F)Lnet/minecraft/world/phys/Vec3;", at = @At("RETURN"), cancellable = true)
    private void mistedAtmosphere$matchFogCloudColor(float partialTick, CallbackInfoReturnable<Vec3> cir) {
        ClientLevel level = (ClientLevel) (Object) this;
        Vec3 cloudColor = cir.getReturnValue();
        float[] transformed = MistClient.transformFogColor(level, Minecraft.getInstance().gameRenderer.getMainCamera().getPosition(), (float) cloudColor.x, (float) cloudColor.y, (float) cloudColor.z);
        if (transformed != null) {
            cir.setReturnValue(new Vec3(transformed[0], transformed[1], transformed[2]));
        }
    }
}