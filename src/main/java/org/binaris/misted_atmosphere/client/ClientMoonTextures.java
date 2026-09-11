package org.binaris.misted_atmosphere.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import org.binaris.misted_atmosphere.Config;
import org.binaris.misted_atmosphere.MistedAtmosphere;
import org.binaris.misted_atmosphere.moon.MoonCalendar;
import org.binaris.misted_atmosphere.moon.SpecialMoon;

public final class ClientMoonTextures {

    public static final ResourceLocation VANILLA_MOON_PHASES = ResourceLocation.withDefaultNamespace("textures/environment/moon_phases.png");
    private static final ResourceLocation BLOOD_MOON_PHASES = ResourceLocation.fromNamespaceAndPath(MistedAtmosphere.MODID, "textures/environment/blood_moon_phases.png");
    private static final ResourceLocation BLUE_MOON_PHASES = ResourceLocation.fromNamespaceAndPath(MistedAtmosphere.MODID, "textures/environment/blue_moon_phases.png");

    private ClientMoonTextures() {
    }

    public static ResourceLocation resolve(ResourceLocation original) {
        if (!VANILLA_MOON_PHASES.equals(original) || !Config.moonCycleEnabled) {
            return original;
        }
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return original;
        }
        SpecialMoon special = MoonCalendar.specialMoonFor(level.getDayTime(), Config.bloodMoonEnabled, Config.bloodMoonCycleInterval, Config.blueMoonEnabled, Config.blueMoonCycleInterval);
        return switch (special) {
            case BLOOD -> BLOOD_MOON_PHASES;
            case BLUE -> BLUE_MOON_PHASES;
            case NONE -> original;
        };
    }
}
