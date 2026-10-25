package org.mcenvision.drunir.worldgen;

import io.netty.buffer.Unpooled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IslandBoundaryPayloadTest {
    @Test
    void boundedSnapshotRoundTripsExtremesAndRefusesInvalidCoordinates() {
        for (var payload : new IslandBoundaryPayload[]{new IslandBoundaryPayload(false, 0, 0, 0),
                new IslandBoundaryPayload(true, -29_999_984, 29_999_984, Long.MIN_VALUE),
                new IslandBoundaryPayload(true, 29_999_984, -29_999_984, Long.MAX_VALUE)}) {
            var buffer = Unpooled.buffer();
            try {
                IslandBoundaryPayload.CODEC.encode(buffer, payload);
                assertTrue(buffer.readableBytes() <= 19);
                assertEquals(payload, IslandBoundaryPayload.CODEC.decode(buffer));
                assertEquals(0, buffer.readableBytes());
                buffer.clear();
                buffer.writeBoolean(true).writeInt(Integer.MAX_VALUE).writeInt(0).writeByte(0);
                assertThrows(IllegalArgumentException.class, () -> IslandBoundaryPayload.CODEC.decode(buffer));
                buffer.clear();
                assertThrows(IndexOutOfBoundsException.class, () -> IslandBoundaryPayload.CODEC.decode(buffer));
            } finally {
                buffer.release();
            }
        }
    }
}
