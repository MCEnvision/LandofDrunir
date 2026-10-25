package org.mcenvision.drunir.worldgen;

import net.minecraft.network.Connection;

public final class IslandBoundaryState {
    private static Connection connection;
    private static IslandRegion region;

    private IslandBoundaryState() {}

    public static void receive(Connection source, IslandRegion value) {
        connection = source;
        region = value;
    }

    public static IslandRegion region(Connection current) {
        return current != null && connection == current ? region : null;
    }

    public static void clear(Connection source) {
        if (source == null || connection == source) {
            connection = null;
            region = null;
        }
    }
}
