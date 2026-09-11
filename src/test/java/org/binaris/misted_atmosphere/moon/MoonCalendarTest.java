package org.binaris.misted_atmosphere.moon;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MoonCalendarTest {

    private static final long DAY = 24000L;

    @Test
    void cycleBoundaries() {
        assertEquals(0, MoonCalendar.dayOfCycle(0L));
        assertEquals(28, MoonCalendar.dayOfCycle(28L * DAY));
        assertEquals(0, MoonCalendar.dayOfCycle(29L * DAY));
        assertEquals(1L, MoonCalendar.cycleNumber(0L));
        assertEquals(1L, MoonCalendar.cycleNumber(28L * DAY));
        assertEquals(2L, MoonCalendar.cycleNumber(29L * DAY));
        assertEquals(2L, MoonCalendar.cycleNumber(57L * DAY));
        assertEquals(3L, MoonCalendar.cycleNumber(58L * DAY));
    }

    @Test
    void everyDayMapsToExpectedVanillaPhase() {
        int[] expected = {
                4,
                5, 5, 5, 5, 5,
                6,
                7, 7, 7, 7, 7, 7,
                0,
                1, 1, 1, 1, 1, 1,
                2,
                3, 3, 3, 3, 3, 3, 3,
                4
        };
        for (int day = 0; day < MoonCalendar.CYCLE_LENGTH_DAYS; day++) {
            long dayTime = day * DAY;
            assertEquals(expected[day], MoonCalendar.phaseFor(dayTime), "phase for day " + (day + 1));
            assertEquals(expected[day], MoonCalendar.vanillaPhase(day), "vanilla phase for day " + (day + 1));
        }
    }

    @Test
    void fullMoonIsOnDayFourteen() {
        assertTrue(MoonCalendar.isFullMoonNight(13L * DAY));
        assertFalse(MoonCalendar.isFullMoonNight(12L * DAY));
        assertFalse(MoonCalendar.isFullMoonNight(14L * DAY));
        assertEquals(0, MoonCalendar.phaseFor(13L * DAY));
    }

    @Test
    void bloodMoonEveryThirdCycle() {
        long cycle3FullMoon = 71L * DAY;
        long cycle6FullMoon = 158L * DAY;
        assertEquals(SpecialMoon.BLOOD, MoonCalendar.specialMoonFor(cycle3FullMoon, true, 3, true, 5));
        assertEquals(SpecialMoon.BLOOD, MoonCalendar.specialMoonFor(cycle6FullMoon, true, 3, true, 5));
    }

    @Test
    void blueMoonEveryFifthCycle() {
        long cycle5FullMoon = 129L * DAY;
        long cycle10FullMoon = 274L * DAY;
        assertEquals(SpecialMoon.BLUE, MoonCalendar.specialMoonFor(cycle5FullMoon, true, 3, true, 5));
        assertEquals(SpecialMoon.BLUE, MoonCalendar.specialMoonFor(cycle10FullMoon, true, 3, true, 5));
    }

    @Test
    void blueMoonTakesPriorityWhenCyclesOverlap() {
        long cycle15FullMoon = 419L * DAY;
        assertEquals(15L, MoonCalendar.cycleNumber(cycle15FullMoon));
        assertEquals(SpecialMoon.BLUE, MoonCalendar.specialMoonFor(cycle15FullMoon, true, 3, true, 5));
    }

    @Test
    void noSpecialMoonOutsideFullMoonNight() {
        long cycle3Day14 = 72L * DAY;
        long cycle5Day12 = 128L * DAY;
        assertEquals(SpecialMoon.NONE, MoonCalendar.specialMoonFor(cycle3Day14, true, 3, true, 5));
        assertEquals(SpecialMoon.NONE, MoonCalendar.specialMoonFor(cycle5Day12, true, 3, true, 5));
    }

    @Test
    void disabledMoonsAreNeverReported() {
        long cycle3FullMoon = 71L * DAY;
        long cycle5FullMoon = 129L * DAY;
        assertEquals(SpecialMoon.NONE, MoonCalendar.specialMoonFor(cycle3FullMoon, false, 3, true, 5));
        assertEquals(SpecialMoon.NONE, MoonCalendar.specialMoonFor(cycle5FullMoon, true, 3, false, 5));
        assertEquals(SpecialMoon.NONE, MoonCalendar.specialMoonFor(cycle3FullMoon, true, 0, true, 5));
        assertEquals(SpecialMoon.NONE, MoonCalendar.specialMoonFor(cycle5FullMoon, true, 3, true, 0));
    }

    @Test
    void negativeDayTimeIsHandled() {
        assertEquals(28, MoonCalendar.dayOfCycle(-1L * DAY));
        assertEquals(0L, MoonCalendar.cycleNumber(-1L * DAY));
    }
}
