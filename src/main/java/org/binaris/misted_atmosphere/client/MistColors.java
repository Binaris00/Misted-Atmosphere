package org.binaris.misted_atmosphere.client;

public final class MistColors {

    private MistColors() {
    }

    public static float[] overcast(float red, float green, float blue, float strength) {
        float s = clamp(strength, 0.0F, 1.0F);
        if (s <= 0.0F) {
            return new float[]{red, green, blue};
        }
        float luminance = 0.299F * red + 0.587F * green + 0.114F * blue;
        float desaturation = s * 0.65F;
        float brightness = 1.0F - s * 0.35F;
        return new float[]{
                lerp(red, luminance, desaturation) * brightness,
                lerp(green, luminance, desaturation) * brightness,
                lerp(blue, luminance, desaturation) * brightness
        };
    }

    public static float[] blend(float red, float green, float blue, float targetRed, float targetGreen, float targetBlue, float strength) {
        float s = clamp(strength, 0.0F, 1.0F);
        return new float[]{
                lerp(red, targetRed, s),
                lerp(green, targetGreen, s),
                lerp(blue, targetBlue, s)
        };
    }

    public static float lerp(float from, float to, float delta) {
        return from + (to - from) * delta;
    }

    public static float approach(float current, float target, float step) {
        if (current < target) {
            return Math.min(target, current + step);
        }
        if (current > target) {
            return Math.max(target, current - step);
        }
        return target;
    }

    public static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }
}
