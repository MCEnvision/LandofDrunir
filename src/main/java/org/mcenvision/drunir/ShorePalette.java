package org.mcenvision.drunir;

public final class ShorePalette {
    public static final int LAST_TONE = 7;
    public static final double HALF_WIDTH = 12;

    private ShorePalette() {}

    public static int color(int tone) {
        double weight = Math.clamp(tone, 0, LAST_TONE) / (double) LAST_TONE;
        int green = (int) Math.round(255 - 75 * weight);
        int blue = (int) Math.round(255 - 203 * weight);
        return 0xFF0000 | green << 8 | blue;
    }

    public static int tone(double inlandDistance, int x, int z) {
        long hash = ((long) x * 3129871) ^ ((long) z * 116129781);
        hash = hash * hash * 42317861L + hash * 11L;
        double dither = ((hash >>> 24) & 255) / 255.0 - 0.5;
        double weight = (inlandDistance + HALF_WIDTH) / (2 * HALF_WIDTH);
        return (int) Math.clamp(Math.round(weight * LAST_TONE + dither), 0, LAST_TONE);
    }
}
