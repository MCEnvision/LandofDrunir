package org.mcenvision.drunir.boulder;

import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class BoulderCatalogTest {
    @Test
    void everyPieceFitsItsCellAndReachesItsStackingHeight() {
        var catalog = BoulderCatalog.INSTANCE;
        for (int layers = 1; layers <= 8; layers++) {
            var silhouettes = new HashSet<>();
            for (int form = 0; form < 4; form++) {
                for (int turn = 0; turn < 4; turn++) {
                    var boxes = catalog.geometry(form, layers, turn);
                    assertEquals(layers * 2, boxes.stream().mapToDouble(BoulderCatalog.Box::maxY).max().orElseThrow());
                    for (var box : boxes) {
                        assertTrue(box.minX() >= 0 && box.maxX() <= 16 && box.minX() < box.maxX());
                        assertTrue(box.minY() >= 0 && box.minY() < box.maxY());
                        assertTrue(box.minZ() >= 0 && box.maxZ() <= 16 && box.minZ() < box.maxZ());
                        assertEquals(box, box.turn().turn().turn().turn());
                    }
                }
                silhouettes.add(catalog.geometry(form, layers, 0));
            }
            assertEquals(4, silhouettes.size(), "Each form needs a distinct model.");
        }
    }

    @Test
    void sixtyChoicesHaveStableUniqueIdentifiers() {
        var catalog = BoulderCatalog.INSTANCE;
        var identifiers = new HashSet<String>();
        assertEquals(5, catalog.sizes().size());
        for (var material : catalog.materials()) {
            for (var size : catalog.sizes()) {
                for (var form : catalog.forms()) {
                    assertTrue(identifiers.add(BoulderCatalog.itemId(material.id(), size.id(), form.id())));
                }
            }
        }
        assertEquals(60, identifiers.size());
    }

    @Test
    void stackingConservesEveryLayerAndBoundsTheOverflow() {
        for (int existing = 0; existing <= 8; existing++) {
            for (int added = 1; added <= 8; added++) {
                var plan = BoulderStackPlan.add(existing, added);
                assertEquals(existing + added, plan.lowerLayers() + plan.upperLayers());
                assertTrue(plan.lowerLayers() >= 1 && plan.lowerLayers() <= 8);
                assertTrue(plan.upperLayers() >= 0 && plan.upperLayers() <= 8);
                if (plan.upperLayers() > 0) assertEquals(8, plan.lowerLayers());
            }
        }
        assertThrows(IllegalArgumentException.class, () -> BoulderStackPlan.add(9, 1));
        assertThrows(IllegalArgumentException.class, () -> BoulderStackPlan.add(1, 0));
    }
}
