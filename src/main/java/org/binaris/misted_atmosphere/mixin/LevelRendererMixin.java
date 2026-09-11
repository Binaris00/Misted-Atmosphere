package org.binaris.misted_atmosphere.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.resources.ResourceLocation;
import org.binaris.misted_atmosphere.client.ClientMoonTextures;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {

    @Redirect(
            method = "renderSky",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/resources/ResourceLocation;)V",
                    ordinal = 1
            )
    )
    private void mistedAtmosphere$useSpecialMoonTexture(int shaderTexture, ResourceLocation location) {
        RenderSystem.setShaderTexture(shaderTexture, ClientMoonTextures.resolve(location));
    }
}