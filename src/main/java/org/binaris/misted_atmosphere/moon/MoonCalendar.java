package org.binaris.misted_atmosphere.moon;

public final class MoonCalendar {

    public static final int CYCLE_LENGTH_DAYS = 29;
    public static final int FULL_MOON_DAY = 13;
    public static final long TICKS_PER_DAY = 24000L;

    private MoonCalendar() {
    }

    public static long totalDays(long dayTime) {
        return Math.floorDiv(dayTime, TICKS_PER_DAY);
    }

    public static int dayOfCycle(long dayTime) {
        return (int) Math.floorMod(totalDays(dayTime), (long) CYCLE_LENGTH_DAYS);
    }

    public static long cycleNumber(long dayTime) {
        return Math.floorDiv(totalDays(dayTime), (long) CYCLE_LENGTH_DAYS) + 1L;
    }

    public static int vanillaPhase(int dayOfCycle) {
        if (dayOfCycle <= 0 || dayOfCycle >= 28) {
            return 4;
        }
        if (dayOfCycle <= 5) {
            return 5;
        }
        if (dayOfCycle == 6) {
            return 6;
        }
        if (dayOfCycle <= 12) {
            return 7;
        }
        if (dayOfCycle == 13) {
            return 0;
        }
        if (dayOfCycle <= 19) {
            return 1;
        }
        if (dayOfCycle == 20) {
            return 2;
        }
        return 3;
    }

    public static int phaseFor(long dayTime) {
        return vanillaPhase(dayOfCycle(dayTime));
    }

    public static boolean isFullMoonNight(long dayTime) {
        return dayOfCycle(dayTime) == FULL_MOON_DAY;
    }

    public static SpecialMoon specialMoonFor(long dayTime, boolean bloodEnabled, int bloodInterval, boolean blueEnabled, int blueInterval) {
        if (!isFullMoonNight(dayTime)) {
            return SpecialMoon.NONE;
        }
        long cycle = cycleNumber(dayTime);
        if (blueEnabled && blueInterval > 0 && cycle % blueInterval == 0L) {
            return SpecialMoon.BLUE;
        }
        if (bloodEnabled && bloodInterval > 0 && cycle % bloodInterval == 0L) {
            return SpecialMoon.BLOOD;
        }
        return SpecialMoon.NONE;
    }
}
