package org.mcenvision.drunir.client;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MysticNavigationTest {
    @Test
    void compassCompletesAContinuousTurnInSixtyTicks() {
        assertEquals(0, MysticNavigation.compass(0, 0));
        assertEquals(0.25F, MysticNavigation.compass(15, 0));
        assertEquals(0.5F, MysticNavigation.compass(30, 0));
        assertEquals(0.75F, MysticNavigation.compass(45, 0));
        assertEquals(0, MysticNavigation.compass(60, 0));
        for (int seed : new int[]{Integer.MIN_VALUE, -1, 0, 1, Integer.MAX_VALUE}) {
            float value = MysticNavigation.compass(123.5, seed);
            assertTrue(value >= 0 && value < 1);
        }
    }

    @Test
    void coordinateDisplayChangesWithoutTouchingOtherDebugInformation() {
        var lines = new ArrayList<>(List.of("XYZ: 1 / 2 / 3", "Block: 1 2 3", "Chunk: 0 0 0", "Chunk-relative: 1 2 3",
                "Biome: landofdrunir:yellow_tree_forest", "Targeted Block: 1, 2, 3", "\u00a7nTargeted Fluid: 1, 2, 3"));
        var fake = new MysticNavigation.Coordinates(-17.5, 90.75, 33.25);
        MysticNavigation.distort(lines, fake);
        assertEquals("XYZ: -17.500 / 90.75000 / 33.250", lines.get(0));
        assertEquals("Block: -18 90 33 [14 10 1]", lines.get(1));
        assertEquals("Chunk: -2 5 2 [30 2 in r.-1.0.mca]", lines.get(2));
        assertEquals("Chunk-relative: 14 10 1", lines.get(3));
        assertEquals("Biome: landofdrunir:yellow_tree_forest", lines.get(4));
        assertEquals("Targeted Block: -18, 90, 33", lines.get(5));
        assertEquals("\u00a7nTargeted Fluid: -18, 90, 33", lines.get(6));
        assertNotEquals(MysticNavigation.coordinates(0), MysticNavigation.coordinates(100));
        assertTrue(MysticNavigation.coordinates(250).y() > MysticNavigation.coordinates(750).y());
    }
}
