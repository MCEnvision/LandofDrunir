package org.mcenvision.drunir.client;

import java.util.List;
import java.util.Locale;

public final class MysticNavigation {
    public record Coordinates(double x, double y, double z) {}

    private MysticNavigation() {}

    public static float compass(double ticks, int seed) {
        double rotation = ticks / 60 + seed * 0.03125;
        return (float) (rotation - Math.floor(rotation));
    }

    public static Coordinates coordinates(double ticks) {
        return new Coordinates(18000 * Math.sin(ticks * 0.0017) + 900 * Math.cos(ticks * 0.0081),
                90 + 110 * Math.sin(ticks * 0.0063),
                18000 * Math.cos(ticks * 0.0021) + 900 * Math.sin(ticks * 0.0097));
    }

    public static void distort(List<String> lines, Coordinates position) {
        int x = (int) Math.floor(position.x()), y = (int) Math.floor(position.y()), z = (int) Math.floor(position.z());
        for (int index = 0; index < lines.size(); index++) {
            String line = lines.get(index);
            if (line == null) continue;
            if (line.startsWith("XYZ:")) {
                lines.set(index, String.format(Locale.ROOT, "XYZ: %.3f / %.5f / %.3f", position.x(), position.y(), position.z()));
            } else if (line.startsWith("Block:")) {
                lines.set(index, String.format(Locale.ROOT, "Block: %d %d %d [%d %d %d]", x, y, z, x & 15, y & 15, z & 15));
            } else if (line.startsWith("Chunk:")) {
                lines.set(index, String.format(Locale.ROOT, "Chunk: %d %d %d [%d %d in r.%d.%d.mca]",
                        x >> 4, y >> 4, z >> 4, (x >> 4) & 31, (z >> 4) & 31, x >> 9, z >> 9));
            } else if (line.startsWith("Chunk-relative:")) {
                lines.set(index, String.format(Locale.ROOT, "Chunk-relative: %d %d %d", x & 15, y & 15, z & 15));
            } else if (line.contains("Targeted Block:") || line.contains("Targeted Fluid:")) {
                lines.set(index, line.substring(0, line.indexOf(':') + 1) + " " + x + ", " + y + ", " + z);
            }
        }
    }
}
