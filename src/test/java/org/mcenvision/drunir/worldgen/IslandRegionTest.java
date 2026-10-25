package org.mcenvision.drunir.worldgen;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IslandRegionTest {
    @Test
    void coastDescendsContinuouslyThroughSeaLevelIntoTheSeabed() {
        for (long seed : new long[]{0, 8675309, -1}) {
            var island = IslandRegion.fromSeed(seed);
            for (int direction = 0; direction < 360; direction += 15) {
                double angle = Math.toRadians(direction);
                double previous = Double.NaN;
                boolean dry = false;
                boolean shallow = false;
                boolean deep = false;
                for (int radius = 350; radius < 750; radius++) {
                    int x = island.centerX() + (int) Math.round(Math.cos(angle) * radius);
                    int z = island.centerZ() + (int) Math.round(Math.sin(angle) * radius);
                    double height = island.surfaceHeight(x, z);
                    if (!Double.isNaN(previous)) assertTrue(Math.abs(previous - height) < 1,
                            "The shoreline must not produce a multi-block cutoff.");
                    if (height >= 64) dry = true;
                    if (height >= 58 && height < 63) {
                        shallow = true;
                        assertTrue(island.beach(x, z), "The sandy shore must continue underwater.");
                    }
                    if (height <= 40) deep = true;
                    previous = height;
                }
                assertTrue(dry && shallow && deep);
            }
        }
    }

    @Test
    void occupiedTerrainMovesTheOneAnchorBeforeItIsSaved() {
        long seed = 8675309;
        IslandRegion original = IslandRegion.fromSeed(seed);
        IslandRegion selected = IslandRegion.findUnexplored(seed, region -> !region.equals(original));
        assertNotEquals(original, selected);
        assertEquals(selected, IslandRegion.findUnexplored(seed, region -> !region.equals(original)));
        assertThrows(IllegalStateException.class, () -> IslandRegion.findUnexplored(seed, region -> false));
    }

    @Test
    void seedChoosesOneStableLocation() {
        IslandRegion first = IslandRegion.fromSeed(8675309);
        assertEquals(first, IslandRegion.fromSeed(8675309));
        assertNotEquals(first, IslandRegion.fromSeed(8675310));
        assertTrue(Math.hypot(first.centerX(), first.centerZ()) > 4000);
        assertTrue(Math.hypot(first.centerX(), first.centerZ()) < 6200);
    }

    @Test
    void forestIsOneConnectedIslandWithNoRepeatingRegions() {
        for (long seed : new long[]{0, 1, -1, Long.MIN_VALUE, Long.MAX_VALUE}) {
            IslandRegion island = IslandRegion.fromSeed(seed);
            assertTrue(island.forest(island.centerX(), island.centerZ()));
            for (int direction = 0; direction < 360; direction += 5) {
                double angle = Math.toRadians(direction);
                boolean exited = false;
                for (int distance = 0; distance < 4096; distance += 4) {
                    int x = island.centerX() + (int) Math.round(Math.cos(angle) * distance);
                    int z = island.centerZ() + (int) Math.round(Math.sin(angle) * distance);
                    if (!island.forest(x, z)) exited = true;
                    if (exited) assertFalse(island.forest(x, z), "An island cannot start again along a ray.");
                }
            }
        }
    }

    @Test
    void oceanBeltSeparatesLandFromSurroundingTerrain() {
        IslandRegion island = IslandRegion.fromSeed(0);
        for (int direction = 0; direction < 360; direction += 3) {
            double angle = Math.toRadians(direction);
            int x = island.centerX() + (int) Math.round(Math.cos(angle) * 740);
            int z = island.centerZ() + (int) Math.round(Math.sin(angle) * 740);
            assertFalse(island.forest(x, z));
            assertFalse(island.beach(x, z));
            assertEquals(39, island.surfaceHeight(x, z), 0.001);
            assertEquals(1, island.influence(x, z), 0.001);
        }
    }

    @Test
    void transitionEndsWithoutChangingDistantTerrain() {
        IslandRegion island = IslandRegion.fromSeed(0);
        assertEquals(0, island.influence(island.centerX() + IslandRegion.BLEND_RADIUS, island.centerZ()));
        assertFalse(island.affects(island.centerX() + IslandRegion.BLEND_RADIUS, island.centerZ()));
        assertFalse(island.affects(Integer.MAX_VALUE, Integer.MIN_VALUE));
        double previous = 1;
        for (int distance = IslandRegion.OCEAN_RADIUS; distance <= IslandRegion.BLEND_RADIUS; distance++) {
            double weight = island.influence(island.centerX() + distance, island.centerZ());
            assertTrue(weight >= 0 && weight <= previous);
            previous = weight;
        }
    }

    @Test
    void islandInteriorStaysAboveSeaLevel() {
        IslandRegion island = IslandRegion.fromSeed(0);
        for (int x = island.centerX() - 600; x <= island.centerX() + 600; x += 8) {
            for (int z = island.centerZ() - 600; z <= island.centerZ() + 600; z += 8) {
                if (island.forest(x, z)) assertTrue(island.surfaceHeight(x, z) > 63);
            }
        }
    }
}
