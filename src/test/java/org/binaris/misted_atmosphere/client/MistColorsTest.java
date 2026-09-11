package org.binaris.misted_atmosphere.client;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MistColorsTest {

    private static final float EPSILON = 0.0001F;

    @Test
    void overcastWithZeroStrengthIsIdentity() {
        float[] result = MistColors.overcast(0.2F, 0.4F, 0.6F, 0.0F);
        assertEquals(0.2F, result[0], EPSILON);
        assertEquals(0.4F, result[1], EPSILON);
        assertEquals(0.6F, result[2], EPSILON);
    }

    @Test
    void overcastDesaturatesAndDarkens() {
        float[] result = MistColors.overcast(1.0F, 0.0F, 0.0F, 1.0F);
        assertEquals(0.3538275F, result[0], EPSILON);
        assertEquals(0.1263275F, result[1], EPSILON);
        assertEquals(0.1263275F, result[2], EPSILON);
    }

    @Test
    void overcastKeepsGreyValuesGrey() {
        float[] result = MistColors.overcast(0.5F, 0.5F, 0.5F, 1.0F);
        assertEquals(0.325F, result[0], EPSILON);
        assertEquals(0.325F, result[1], EPSILON);
        assertEquals(0.325F, result[2], EPSILON);
    }

    @Test
    void blendWithZeroStrengthIsIdentity() {
        float[] result = MistColors.blend(0.1F, 0.2F, 0.3F, 0.9F, 0.8F, 0.7F, 0.0F);
        assertEquals(0.1F, result[0], EPSILON);
        assertEquals(0.2F, result[1], EPSILON);
        assertEquals(0.3F, result[2], EPSILON);
    }

    @Test
    void blendWithFullStrengthReturnsTarget() {
        float[] result = MistColors.blend(0.1F, 0.2F, 0.3F, 0.9F, 0.8F, 0.7F, 1.0F);
        assertEquals(0.9F, result[0], EPSILON);
        assertEquals(0.8F, result[1], EPSILON);
        assertEquals(0.7F, result[2], EPSILON);
    }

    @Test
    void blendWithHalfStrengthIsMidpoint() {
        float[] result = MistColors.blend(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 0.25F, 0.5F);
        assertEquals(0.5F, result[0], EPSILON);
        assertEquals(0.25F, result[1], EPSILON);
        assertEquals(0.125F, result[2], EPSILON);
    }

    @Test
    void strengthIsClamped() {
        float[] low = MistColors.overcast(0.2F, 0.4F, 0.6F, -1.0F);
        assertEquals(0.2F, low[0], EPSILON);
        float[] high = MistColors.blend(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 5.0F);
        assertEquals(1.0F, high[0], EPSILON);
    }
}
