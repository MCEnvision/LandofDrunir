package org.mcenvision.drunir.client;

import org.junit.jupiter.api.Test;
import org.mcenvision.drunir.DrunirCollection;

import static org.junit.jupiter.api.Assertions.*;

class CreatureModelsTest {
    @Test
    void everyRegisteredCreatureBakesAndWalksOnlyExistingParts() {
        for (String name : DrunirCollection.CONTENT.creatures()) {
            var root = DrunirClient.geometry(name).bakeRoot();
            assertTrue(root.getAllParts().anyMatch(part -> !part.isEmpty()), name);
            var walk = DrunirClient.walk(name);
            assertNotNull(walk, name);
            if (name.equals("lightling") || name.equals("sandstalker")) {
                assertTrue(walk.boneAnimations().isEmpty(), name);
                continue;
            }
            assertFalse(walk.boneAnimations().isEmpty(), name);
            assertTrue(walk.looping(), name);
            walk.boneAnimations().forEach((bone, channels) -> {
                assertTrue(root.getAllParts().anyMatch(part -> part.hasChild(bone)), name + ": " + bone);
                for (var channel : channels) {
                    float previous = -1;
                    for (var frame : channel.keyframes()) {
                        assertTrue(Float.isFinite(frame.timestamp()) && frame.timestamp() > previous, name);
                        assertTrue(frame.timestamp() <= walk.lengthInSeconds(), name + ": keyframe exceeds loop duration");
                        assertTrue(frame.target().isFinite(), name);
                        previous = frame.timestamp();
                    }
                }
            });
        }
        assertTrue(DrunirClient.geometry("magmafiend_fireball").bakeRoot().getAllParts()
                .anyMatch(part -> !part.isEmpty()));
        assertThrows(IllegalArgumentException.class, () -> DrunirClient.geometry("missing"));
        assertThrows(IllegalArgumentException.class, () -> DrunirClient.walk("missing"));
    }
}
