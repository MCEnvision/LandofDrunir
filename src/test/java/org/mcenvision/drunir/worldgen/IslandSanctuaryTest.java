package org.mcenvision.drunir.worldgen;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IslandSanctuaryTest {
    @Test
    void protectedBoundsCoverCrossingStructuresWithoutBlockingDistantTerrain() {
        var island = new IslandRegion(0, 0, 12);
        assertTrue(island.sanctuary(0, 0));
        assertTrue(island.intersectsSanctuary(760, -8, 790, 8));
        assertTrue(island.intersectsSanctuary(-2000, -2000, 2000, 2000));
        assertFalse(island.intersectsSanctuary(800, 800, 850, 850));
        assertFalse(island.sanctuary(IslandRegion.OCEAN_RADIUS, 0));
    }

    @Test
    void sixStableWrecksStaySubmergedAndWellSeparated() {
        for (long seed : new long[]{0, 1, -1, 8675309}) {
            var region = IslandRegion.fromSeed(seed);
            var sites = IslandWreckSites.create(region);
            assertEquals(6, sites.size());
            assertEquals(sites, IslandWreckSites.create(region));
            for (var site : sites) {
                assertTrue(region.sanctuary(site.x(), site.z()));
                assertFalse(region.forest(site.x(), site.z()));
                assertTrue(region.surfaceHeight(site.x(), site.z()) < 53);
                assertTrue(site.variant() >= 0 && site.variant() < 4);
                assertTrue(site.turns() >= 0 && site.turns() < 4);
                for (var other : sites) {
                    if (other != site) assertTrue(Math.hypot(site.x() - other.x(), site.z() - other.z()) > 200);
                }
            }
        }
    }
}
