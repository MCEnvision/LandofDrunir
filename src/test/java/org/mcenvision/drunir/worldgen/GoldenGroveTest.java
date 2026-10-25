package org.mcenvision.drunir.worldgen;

import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class GoldenGroveTest {
    @Test
    void irregularSitesAreStableAcrossChunkBorders() {
        var region = new IslandRegion(0, 0, 7192);
        var sites = new ArrayList<GroveLayout.Site>();
        var offsets = new HashSet<String>();
        for (int x = IslandRegion.RADIUS / 16 - 8; x <= IslandRegion.RADIUS / 16 - 2; x++) {
            for (int z = -5; z <= 5; z++) {
                var chunk = GroveLayout.sites(region, x, z);
                assertEquals(chunk, GroveLayout.sites(region, x, z));
                for (var site : chunk) {
                    assertEquals(x, Math.floorDiv(site.x(), 16));
                    assertEquals(z, Math.floorDiv(site.z(), 16));
                    for (var previous : sites) assertTrue(Math.hypot(site.x() - previous.x(), site.z() - previous.z()) >= 3.4);
                    offsets.add(Math.floorMod(site.x(), 4) + "," + Math.floorMod(site.z(), 4));
                    sites.add(site);
                }
            }
        }
        assertTrue(sites.size() > 100, "The outer forest must stay dense");
        assertEquals(16, offsets.size(), "Tree sites must use every subcell offset rather than form rows");
    }

    @Test
    void densityFallsFromTheOuterForestTowardTheCenter() {
        var region = new IslandRegion(0, 0, 7192);
        int[] counts = new int[3];
        int[] chunks = new int[3];
        for (int x = -33; x <= 33; x++) {
            for (int z = -33; z <= 33; z++) {
                int blockX = x * 16 + 8, blockZ = z * 16 + 8;
                if (!region.forest(blockX, blockZ)) continue;
                double outward = region.distance(blockX, blockZ)
                        / (region.coastRadius(blockX, blockZ) - IslandRegion.FOREST_INSET);
                int band = outward >= 0.75 ? 0 : outward >= 0.4 && outward <= 0.6 ? 1
                        : outward <= 0.3 ? 2 : -1;
                if (band < 0) continue;
                chunks[band]++;
                counts[band] += GroveLayout.sites(region, x, z).size();
            }
        }
        double edge = (double) counts[0] / chunks[0];
        double middle = (double) counts[1] / chunks[1];
        double center = (double) counts[2] / chunks[2];
        for (int count : chunks) assertTrue(count > 0, "Every density band must sample real forest chunks");
        assertTrue(edge > middle * 1.5);
        assertTrue(middle > center * 1.5);
    }

    @Test
    void crownsCoverEveryBranchTopAndAllLeavesHaveLiveLogSupport() {
        for (long seed = 0; seed < 96; seed++) {
            for (double wonder : new double[]{0, 0.5, 1}) {
                var tree = GoldenTreeShape.create(seed, wonder);
                assertEquals(tree, GoldenTreeShape.create(seed, wonder));
                Set<GoldenTreeShape.Point> connected = new HashSet<>();
                var queue = new ArrayDeque<GoldenTreeShape.Point>();
                queue.add(new GoldenTreeShape.Point(0, 0, 0));
                while (!queue.isEmpty()) {
                    var point = queue.remove();
                    if (!tree.logs().containsKey(point) || !connected.add(point)) continue;
                    for (var next : neighbors(point)) queue.add(next);
                }
                assertEquals(tree.logs().size(), connected.size(), "Every branch must connect to the trunk.");
                for (var log : tree.logs().keySet()) {
                    var above = log.offset(0, 1, 0);
                    assertTrue(tree.logs().containsKey(above) || tree.leaves().containsKey(above), "No exposed log tops.");
                }
                assertTrue(tree.leaves().keySet().stream().allMatch(leaf -> leaf.y() >= 3),
                        "Keep three clear blocks above the dirt");
                tree.leaves().forEach((leaf, distance) -> {
                    assertFalse(tree.logs().containsKey(leaf));
                    assertTrue(distance >= 1 && distance <= 6);
                    assertTrue(neighbors(leaf).stream().anyMatch(next -> distance == 1
                            ? tree.logs().containsKey(next) : tree.leaves().getOrDefault(next, 7) == distance - 1));
                });
                assertTrue(tree.leaves().size() >= 40);
            }
        }
    }

    @Test
    void familiarAndWhimsicalFormsMixThroughoutTheIsland() {
        Set<GoldenTreeShape.Form> edgeForms = new HashSet<>(), centerForms = new HashSet<>();
        int edgeUnusual = 0, centerUnusual = 0;
        for (long seed = 0; seed < 512; seed++) {
            var edge = GoldenTreeShape.create(seed, GroveLayout.wonder(15));
            var center = GoldenTreeShape.create(seed, GroveLayout.wonder(IslandRegion.RADIUS - IslandRegion.FOREST_INSET - 20));
            edgeForms.add(edge.form());
            centerForms.add(center.form());
            if (edge.form() == GoldenTreeShape.Form.BRANCHING) edgeUnusual++;
            if (center.form() == GoldenTreeShape.Form.BRANCHING) centerUnusual++;
        }
        assertEquals(2, edgeForms.size());
        assertEquals(2, centerForms.size());
        assertTrue(centerUnusual > edgeUnusual * 2);
        assertTrue(centerUnusual < 450, "Familiar tree forms must still occur inland");
    }

    @Test
    void liftingACanopyExtendsItsTrunkWithoutChangingItsLeavesOrBranches() {
        var tree = GoldenTreeShape.create(42, 1);
        var lifted = tree.raisedBy(3);
        tree.leaves().forEach((point, distance) -> assertEquals(distance, lifted.leaves().get(point.offset(0, 3, 0))));
        tree.logs().forEach((point, axis) -> assertEquals(axis, lifted.logs().get(point.offset(0, 3, 0))));
        for (int y = 0; y < 3; y++) assertEquals('y', lifted.logs().get(new GoldenTreeShape.Point(0, y, 0)));
        assertThrows(IllegalArgumentException.class, () -> tree.raisedBy(-1));
    }

    @Test
    void centerClearingExcludesRootsAndOverhangingCrowns() {
        for (long seed = 0; seed < 24; seed++) {
            var region = new IslandRegion(327, -521, seed);
            for (int x = 18; x <= 22; x++) {
                for (int z = -35; z <= -31; z++) {
                    for (var site : GroveLayout.sites(region, x, z)) assertTrue(region.distance(site.x(), site.z()) > 10);
                }
            }
        }
        var region = new IslandRegion(0, 0, 7192);
        var tree = GoldenTreeShape.create(42, 0.5);
        assertFalse(GroveLayout.clearsCenter(region, new GroveLayout.Site(10, 0, 42), tree));
        int leftmost = tree.leaves().keySet().stream().mapToInt(GoldenTreeShape.Point::x).min().orElseThrow();
        int anchor = 10 - leftmost;
        assertTrue(anchor > 10);
        assertFalse(GroveLayout.clearsCenter(region, new GroveLayout.Site(anchor, 0, 42), tree),
                "A root outside the clearing must not let its canopy intrude");
        assertTrue(GroveLayout.clearsCenter(region, new GroveLayout.Site(50, 50, 42), tree));
    }

    @Test
    void bothStylesIncludeSmallMediumAndTallTrees() {
        for (var form : GoldenTreeShape.Form.values()) {
            Set<Integer> heights = new HashSet<>();
            for (long seed = 0; seed < 1024; seed++) {
                var tree = GoldenTreeShape.create(seed, 0.5);
                if (tree.form() != form) continue;
                heights.add(tree.logs().keySet().stream().mapToInt(GoldenTreeShape.Point::y).max().orElseThrow() + 1);
            }
            assertTrue(heights.stream().anyMatch(height -> height <= 9));
            assertTrue(heights.stream().anyMatch(height -> height >= 10 && height <= 14));
            assertTrue(heights.stream().anyMatch(height -> height >= 19));
            assertTrue(heights.size() >= 12, "Tree heights must have a broad range in each style");
        }
    }

    private static java.util.List<GoldenTreeShape.Point> neighbors(GoldenTreeShape.Point point) {
        return java.util.List.of(point.offset(1, 0, 0), point.offset(-1, 0, 0), point.offset(0, 1, 0),
                point.offset(0, -1, 0), point.offset(0, 0, 1), point.offset(0, 0, -1));
    }
}
