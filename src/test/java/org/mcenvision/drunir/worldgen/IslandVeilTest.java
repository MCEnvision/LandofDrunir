package org.mcenvision.drunir.worldgen;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IslandVeilTest {
    @Test
    void fogCoversReefsAndWarmSandThenEndsSixteenBlocksIntoFullGrass() {
        var region = new IslandRegion(0, 0, 1234);
        int transition = 0;
        for (int x = 0; x < IslandRegion.OCEAN_RADIUS - 16; x++) {
            double inland = region.forestEdgeDistance(x, 0);
            float fog = IslandVeil.fog(region, x, 0);
            if (inland >= 28) assertEquals(0, fog);
            else if (inland <= 12) assertEquals(1, fog);
            else { assertTrue(fog > 0 && fog < 1); transition++; }
        }
        assertTrue(transition >= 14 && transition <= 18);
        assertEquals(0, IslandVeil.fog(region, IslandRegion.OCEAN_RADIUS, 0));
        assertFalse(IslandVeil.contains(null, 0, 0));
        assertTrue(IslandVeil.contains(region, 0, 0), "Navigation remains strange in the clear interior.");
    }
}
