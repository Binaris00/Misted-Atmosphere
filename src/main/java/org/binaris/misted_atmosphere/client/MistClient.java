package org.binaris.misted_atmosphere.client;

import com.mojang.blaze3d.systems.RenderSystem;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MistClient {

    private static final Logger LOGGER = LoggerFactory.getLogger("MistedAtmosphere");
    private static long lastLog = 0L;

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

    public static float[] transformFogColor(ClientLevel level, Vec3 cameraPos, float red, float green, float blue) {
        if (level == null || level.dimension() != Level.OVERWORLD) {
            return null;
        }
        float[] result = {red, green, blue};
        boolean changed = false;
        if (Config.overcastEnabled && !isNight(level)) {
            result = MistColors.overcast(result[0], result[1], result[2], Config.overcastStrength);
            changed = true;
        }
        if (Config.tealTintEnabled && isMistZone(level, cameraPos)) {
            result = MistColors.blend(result[0], result[1], result[2], Config.tealRed, Config.tealGreen, Config.tealBlue, Config.tealStrength);
            changed = true;
        }
        long now = System.currentTimeMillis();
        if (now - lastLog > 3000L) {
            lastLog = now;
            LOGGER.info("[Mist] transform cfg overcast={} teal={} night={} mist={} in: {},{},{} out: {},{},{}",
                    Config.overcastEnabled, Config.tealTintEnabled, isNight(level), isMistZone(level, cameraPos),
                    String.format("%.3f", red), String.format("%.3f", green), String.format("%.3f", blue),
                    String.format("%.3f", result[0]), String.format("%.3f", result[1]), String.format("%.3f", result[2]));
        }
        return changed ? result : null;
    }

    public static void applyMistDensity(Camera camera) {
        if (!Config.mistEnabled || camera.getFluidInCamera() != FogType.NONE) {
            return;
        }
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null || !isMistZone(level, camera.getPosition())) {
            return;
        }
        RenderSystem.setShaderFogStart(Math.min(RenderSystem.getShaderFogStart(), Config.mistFogStart));
        RenderSystem.setShaderFogEnd(Math.min(RenderSystem.getShaderFogEnd(), Config.mistFogEnd));
    }
}
