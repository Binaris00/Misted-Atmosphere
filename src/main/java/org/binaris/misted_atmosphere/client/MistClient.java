package org.binaris.misted_atmosphere.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.Vec3;
import org.binaris.misted_atmosphere.Config;

public final class MistClient {

    private static float mistAmount;
    private static float overcastAmount;
    private static long lastUpdateMillis = -1L;

    private MistClient() {
    }

    public static boolean isNight(ClientLevel level) {
        long timeOfDay = Math.floorMod(level.getDayTime(), 24000L);
        return timeOfDay >= 13000L && timeOfDay < 23000L;
    }

    public static boolean isMistZone(ClientLevel level, Vec3 cameraPos) {
        if (!Config.mistEnabled) {
            return false;
        }
        if (level == null || level.dimension() != Level.OVERWORLD) {
            return false;
        }
        if (Config.nightMistEnabled && isNight(level)) {
            return true;
        }
        return isWaterBiome(level, BlockPos.containing(cameraPos));
    }

    private static boolean isWaterBiome(ClientLevel level, BlockPos pos) {
        Holder<Biome> biome = level.getBiome(pos);
        return biome.is(BiomeTags.IS_OCEAN) || biome.is(BiomeTags.IS_RIVER) || biome.is(BiomeTags.IS_BEACH);
    }

    private static void tick(ClientLevel level, Vec3 cameraPos) {
        if (level == null || level.dimension() != Level.OVERWORLD) {
            mistAmount = 0.0F;
            overcastAmount = 0.0F;
            lastUpdateMillis = -1L;
            return;
        }
        float targetMist = isMistZone(level, cameraPos) ? 1.0F : 0.0F;
        float targetOvercast = Config.overcastEnabled && !isNight(level) ? 1.0F : 0.0F;
        long now = Util.getMillis();
        if (lastUpdateMillis < 0L) {
            lastUpdateMillis = now;
            mistAmount = targetMist;
            overcastAmount = targetOvercast;
            return;
        }
        float deltaSeconds = (now - lastUpdateMillis) / 1000.0F;
        if (deltaSeconds < 0.005F) {
            return;
        }
        lastUpdateMillis = now;
        float step = Config.fadeSeconds <= 0.0F ? 1.0F : Math.min(deltaSeconds, 0.25F) / Config.fadeSeconds;
        mistAmount = MistColors.approach(mistAmount, targetMist, step);
        overcastAmount = MistColors.approach(overcastAmount, targetOvercast, step);
    }

    public static float[] transformFogColor(ClientLevel level, Vec3 cameraPos, float red, float green, float blue) {
        if (level == null || level.dimension() != Level.OVERWORLD) {
            return null;
        }
        tick(level, cameraPos);
        float[] result = {red, green, blue};
        boolean changed = false;
        if (overcastAmount > 0.0F) {
            result = MistColors.overcast(result[0], result[1], result[2], Config.overcastStrength * overcastAmount);
            changed = true;
        }
        if (Config.tealTintEnabled && mistAmount > 0.0F) {
            result = MistColors.blend(result[0], result[1], result[2], Config.tealRed, Config.tealGreen, Config.tealBlue, Config.tealStrength * mistAmount);
            changed = true;
        }
        return changed ? result : null;
    }

    public static void applyMistDensity(Camera camera) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }
        tick(level, camera.getPosition());
        if (!Config.mistEnabled || camera.getFluidInCamera() != FogType.NONE || mistAmount <= 0.0F) {
            return;
        }
        float currentStart = RenderSystem.getShaderFogStart();
        float currentEnd = RenderSystem.getShaderFogEnd();
        float targetStart = Math.min(currentStart, Config.mistFogStart);
        float targetEnd = Math.min(currentEnd, Config.mistFogEnd);
        RenderSystem.setShaderFogStart(MistColors.lerp(currentStart, targetStart, mistAmount));
        RenderSystem.setShaderFogEnd(MistColors.lerp(currentEnd, targetEnd, mistAmount));
    }
}
