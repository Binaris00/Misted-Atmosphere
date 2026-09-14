package org.binaris.misted_atmosphere.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.biome.Biome;
import org.binaris.misted_atmosphere.Config;
import org.binaris.misted_atmosphere.client.MistClient;
import org.binaris.misted_atmosphere.client.MistColors;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Biome.class)
public abstract class BiomeMixin {

    @Inject(method = "getFogColor", at = @At("RETURN"), cancellable = true)
    private void mistedAtmosphere$tealFogColor(CallbackInfoReturnable<Integer> cir) {
        if (!Config.tealTintEnabled) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level == null || minecraft.gameRenderer == null
                || !MistClient.isMistZone(level, minecraft.gameRenderer.getMainCamera().getPosition())) {
            return;
        }
        int fog = cir.getReturnValue();
        float r = ((fog >> 16) & 0xFF) / 255.0F;
        float g = ((fog >> 8) & 0xFF) / 255.0F;
        float b = (fog & 0xFF) / 255.0F;
        float s = Config.tealStrength;
        int nr = (int) (MistColors.lerp(r, Config.tealRed, s) * 255.0F);
        int ng = (int) (MistColors.lerp(g, Config.tealGreen, s) * 255.0F);
        int nb = (int) (MistColors.lerp(b, Config.tealBlue, s) * 255.0F);
        cir.setReturnValue((nr << 16) | (ng << 8) | nb);
    }
}