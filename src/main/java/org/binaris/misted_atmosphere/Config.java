package org.binaris.misted_atmosphere;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = MistedAtmosphere.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class Config {

    public static final ForgeConfigSpec COMMON_SPEC;
    public static final ForgeConfigSpec CLIENT_SPEC;

    private static final ForgeConfigSpec.BooleanValue MOON_CYCLE_ENABLED;
    private static final ForgeConfigSpec.BooleanValue BLOOD_MOON_ENABLED;
    private static final ForgeConfigSpec.IntValue BLOOD_MOON_CYCLE_INTERVAL;
    private static final ForgeConfigSpec.BooleanValue BLUE_MOON_ENABLED;
    private static final ForgeConfigSpec.IntValue BLUE_MOON_CYCLE_INTERVAL;
    private static final ForgeConfigSpec.BooleanValue NO_SUN_BURN;

    private static final ForgeConfigSpec.BooleanValue OVERCAST_ENABLED;
    private static final ForgeConfigSpec.DoubleValue OVERCAST_STRENGTH;
    private static final ForgeConfigSpec.BooleanValue MIST_ENABLED;
    private static final ForgeConfigSpec.BooleanValue NIGHT_MIST_ENABLED;
    private static final ForgeConfigSpec.DoubleValue MIST_FOG_START;
    private static final ForgeConfigSpec.DoubleValue MIST_FOG_END;
    private static final ForgeConfigSpec.BooleanValue TEAL_TINT_ENABLED;
    private static final ForgeConfigSpec.DoubleValue TEAL_STRENGTH;
    private static final ForgeConfigSpec.ConfigValue<String> TEAL_COLOR;
    private static final ForgeConfigSpec.DoubleValue FADE_SECONDS;

    public static boolean moonCycleEnabled;
    public static boolean bloodMoonEnabled;
    public static int bloodMoonCycleInterval;
    public static boolean blueMoonEnabled;
    public static int blueMoonCycleInterval;
    public static boolean noSunBurn;

    public static boolean overcastEnabled;
    public static float overcastStrength;
    public static boolean mistEnabled;
    public static boolean nightMistEnabled;
    public static float mistFogStart;
    public static float mistFogEnd;
    public static boolean tealTintEnabled;
    public static float tealStrength;
    public static float tealRed;
    public static float tealGreen;
    public static float tealBlue;
    public static float fadeSeconds;

    static {
        ForgeConfigSpec.Builder common = new ForgeConfigSpec.Builder();
        common.comment("Moon cycle settings").push("moon");
        MOON_CYCLE_ENABLED = common
                .comment("Replace the vanilla 8 day moon cycle with a 29 day cycle")
                .define("cycleEnabled", true);
        BLOOD_MOON_ENABLED = common
                .comment("Show a red blood moon on the full moon night of every Nth cycle")
                .define("bloodMoonEnabled", true);
        BLOOD_MOON_CYCLE_INTERVAL = common
                .comment("Blood moon frequency in cycles (3 = every third cycle)")
                .defineInRange("bloodMoonCycleInterval", 3, 1, 1000);
        BLUE_MOON_ENABLED = common
                .comment("Show a blue moon on the full moon night of every Nth cycle")
                .define("blueMoonEnabled", true);
        BLUE_MOON_CYCLE_INTERVAL = common
                .comment("Blue moon frequency in cycles (5 = every fifth cycle). Blue moons take priority over blood moons when both land on the same night")
                .defineInRange("blueMoonCycleInterval", 5, 1, 1000);
        common.pop();
        common.comment("Gameplay settings").push("gameplay");
        NO_SUN_BURN = common
                .comment("Monsters no longer burn in daylight while still only spawning at night")
                .define("noSunBurn", true);
        common.pop();
        COMMON_SPEC = common.build();

        ForgeConfigSpec.Builder client = new ForgeConfigSpec.Builder();
        client.comment("Overcast daylight settings").push("overcast");
        OVERCAST_ENABLED = client
                .comment("Desaturate and dim the sky and fog during the day so it looks overcast")
                .define("enabled", true);
        OVERCAST_STRENGTH = client
                .comment("How strong the overcast effect is (0 = vanilla, 1 = heavily overcast)")
                .defineInRange("strength", 0.6D, 0.0D, 1.0D);
        client.pop();
        client.comment("Mist and fog settings").push("mist");
        MIST_ENABLED = client
                .comment("Enable dense mist over lakes and oceans")
                .define("enabled", true);
        NIGHT_MIST_ENABLED = client
                .comment("Extend the mist everywhere during the night")
                .define("nightEnabled", true);
        MIST_FOG_START = client
                .comment("Distance in blocks where the mist starts to fade in while inside the mist. Lower = haze closer to the player")
                .defineInRange("fogStart", 8.0D, 0.0D, 512.0D);
        MIST_FOG_END = client
                .comment("Distance in blocks where the mist fully hides the world while inside the mist. Higher = more transparent, silhouettes stay visible further away")
                .defineInRange("fogEnd", 80.0D, 1.0D, 512.0D);
        TEAL_TINT_ENABLED = client
                .comment("Tint the fog and sky with a blue teal grading while inside the mist")
                .define("tealTintEnabled", true);
        TEAL_STRENGTH = client
                .comment("How strong the teal grading is (0 = none, 1 = full teal)")
                .defineInRange("tealStrength", 0.75D, 0.0D, 1.0D);
        TEAL_COLOR = client
                .comment("Teal grading color in #RRGGBB format")
                .define("tealColor", "#12545F");
        client.pop();
        client.comment("Transition settings").push("transitions");
        FADE_SECONDS = client
                .comment("Smooth fade time in seconds when entering or leaving the mist, and when the overcast look turns on or off. 0 disables the fade")
                .defineInRange("fadeSeconds", 1.5D, 0.0D, 10.0D);
        client.pop();
        CLIENT_SPEC = client.build();
    }

    private Config() {
    }

    @SubscribeEvent
    static void onLoad(ModConfigEvent event) {
        if (event.getConfig().getSpec() == COMMON_SPEC) {
            loadCommon();
        } else if (event.getConfig().getSpec() == CLIENT_SPEC) {
            loadClient();
        }
    }

    private static void loadCommon() {
        moonCycleEnabled = MOON_CYCLE_ENABLED.get();
        bloodMoonEnabled = BLOOD_MOON_ENABLED.get();
        bloodMoonCycleInterval = BLOOD_MOON_CYCLE_INTERVAL.get();
        blueMoonEnabled = BLUE_MOON_ENABLED.get();
        blueMoonCycleInterval = BLUE_MOON_CYCLE_INTERVAL.get();
        noSunBurn = NO_SUN_BURN.get();
    }

    private static void loadClient() {
        overcastEnabled = OVERCAST_ENABLED.get();
        overcastStrength = OVERCAST_STRENGTH.get().floatValue();
        mistEnabled = MIST_ENABLED.get();
        nightMistEnabled = NIGHT_MIST_ENABLED.get();
        mistFogStart = MIST_FOG_START.get().floatValue();
        mistFogEnd = MIST_FOG_END.get().floatValue();
        tealTintEnabled = TEAL_TINT_ENABLED.get();
        tealStrength = TEAL_STRENGTH.get().floatValue();
        float[] teal = parseColor(TEAL_COLOR.get(), 0.07F, 0.33F, 0.37F);
        tealRed = teal[0];
        tealGreen = teal[1];
        tealBlue = teal[2];
        fadeSeconds = FADE_SECONDS.get().floatValue();
    }

    static float[] parseColor(String value, float fallbackRed, float fallbackGreen, float fallbackBlue) {
        if (value != null) {
            String hex = value.trim();
            if (hex.startsWith("#")) {
                hex = hex.substring(1);
            }
            if (hex.length() == 6) {
                try {
                    int rgb = Integer.parseInt(hex, 16);
                    return new float[]{
                            ((rgb >> 16) & 0xFF) / 255.0F,
                            ((rgb >> 8) & 0xFF) / 255.0F,
                            (rgb & 0xFF) / 255.0F
                    };
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return new float[]{fallbackRed, fallbackGreen, fallbackBlue};
    }
}
