package org.mcenvision.drunir.worldgen;

import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class BoulderFormationTest {
    private record Position(int x, int y, int z) {}

    @Test
    void profilesAreDistinctEvenAfterIgnoringRotationAndBlockParts() {
        Set<String> profiles = new HashSet<>();
        for (int index = 0; index < BoulderFormation.VARIANT_COUNT; index++) {
            var cells = BoulderFormation.variant(index);
            String canonical = null;
            for (int rotation = 0; rotation < 4; rotation++) {
                String shape = BoulderFormation.rotate(cells, rotation).stream()
                        .map(cell -> cell.x() + "," + cell.y() + "," + cell.z())
                        .sorted().reduce("", (a, b) -> a + ";" + b);
                if (canonical == null || shape.compareTo(canonical) < 0) canonical = shape;
            }
            assertTrue(profiles.add(canonical), "Duplicate silhouette: " + index);
        }
        assertEquals(128, profiles.size());
        assertThrows(IllegalArgumentException.class, () -> BoulderFormation.variant(-1));
        assertThrows(IllegalArgumentException.class, () -> BoulderFormation.variant(128));
    }

    @Test
    void pointedProfilesRemainConnectedSupportedAndSolidInside() {
        for (int index = 0; index < BoulderFormation.VARIANT_COUNT; index++) {
            var cells = BoulderFormation.variant(index);
            Map<Position, BoulderFormation.Cell> positions = new HashMap<>();
            int top = cells.stream().mapToInt(BoulderFormation.Cell::y).max().orElseThrow();
            assertTrue(top >= 2 && top <= 3);
            long base = cells.stream().filter(cell -> cell.y() == 0).count();
            long crown = cells.stream().filter(cell -> cell.y() == top).count();
            assertTrue(base >= 2 && crown == 1, "Rock must taper toward its tips: " + index);
            assertTrue(cells.stream().anyMatch(cell -> cell.part() == BoulderFormation.Part.SLAB));
            assertTrue(cells.stream().anyMatch(cell -> cell.part() == BoulderFormation.Part.STAIRS));
            assertTrue(cells.size() <= 21, "Natural rocks must remain short and narrow");
            for (var cell : cells) {
                assertNull(positions.put(new Position(cell.x(), cell.y(), cell.z()), cell));
                assertTrue(Math.abs(cell.x()) <= 1 && Math.abs(cell.z()) <= 1);
                assertTrue(cell.facing() >= 0 && cell.facing() < 4);
            }
            for (var cell : cells) {
                if (cell.y() == 0) assertEquals(BoulderFormation.Part.FULL, cell.part());
                else {
                    var below = positions.get(new Position(cell.x(), cell.y() - 1, cell.z()));
                    assertNotNull(below);
                    assertEquals(BoulderFormation.Part.FULL, below.part(), "Never stack a body on a partial top");
                }
            }
            var connected = new HashSet<Position>();
            var pending = new ArrayDeque<Position>();
            pending.add(new Position(0, 0, 0));
            List<Position> offsets = List.of(new Position(1, 0, 0), new Position(-1, 0, 0),
                    new Position(0, 1, 0), new Position(0, -1, 0), new Position(0, 0, 1), new Position(0, 0, -1));
            while (!pending.isEmpty()) {
                var pos = pending.removeFirst();
                if (!positions.containsKey(pos) || !connected.add(pos)) continue;
                for (var offset : offsets) pending.add(new Position(pos.x() + offset.x(), pos.y() + offset.y(), pos.z() + offset.z()));
            }
            assertEquals(cells.size(), connected.size(), "Disconnected rock: " + index);
            assertEquals(cells, BoulderFormation.rotate(cells, 4));
        }
    }

    @Test
    void seedSelectionIsStableAndReachesEveryProfile() {
        Set<Object> selected = new HashSet<>();
        for (long seed = 0; seed < 4096; seed++) {
            var cells = BoulderFormation.create(seed);
            assertEquals(cells, BoulderFormation.create(seed));
            selected.add(cells);
        }
        assertTrue(selected.size() >= BoulderFormation.VARIANT_COUNT);
    }
}
