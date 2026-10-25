package org.mcenvision.drunir;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShorePaletteTest {
    @Test
    void oceanAndForestEndsRemainStableAcrossCoordinates() {
        for (int x = -100; x <= 100; x++) {
            assertEquals(0, ShorePalette.tone(-100, x, -x));
            assertEquals(7, ShorePalette.tone(100, x, -x));
        }
    }

    @Test
    void shoreWarmsGraduallyWithoutChangingBrightnessOfTheRedChannel() {
        assertEquals(0xFFFFFF, ShorePalette.color(0));
        int previous = ShorePalette.color(0);
        for (int tone = 1; tone <= ShorePalette.LAST_TONE; tone++) {
            int color = ShorePalette.color(tone);
            assertEquals(255, (color >> 16) & 255);
            assertTrue(((color >> 8) & 255) <= ((previous >> 8) & 255));
            assertTrue((color & 255) <= (previous & 255));
            previous = color;
        }
        assertEquals(ShorePalette.color(0), ShorePalette.color(-1));
        assertEquals(ShorePalette.color(7), ShorePalette.color(8));
    }

    @Test
    void theFullTransitionAppearsInOrderAlongTheShore() {
        int seen = 0;
        int previous = -1;
        for (int inland = -24; inland <= 24; inland++) {
            int tone = ShorePalette.tone(inland, 12, 34);
            assertTrue(tone >= previous);
            seen |= 1 << tone;
            previous = tone;
        }
        assertEquals(255, seen);
    }
}
