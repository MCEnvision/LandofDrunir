package org.mcenvision.drunir.boulder;

import java.util.ArrayList;
import java.util.List;

import static org.mcenvision.drunir.boulder.BoulderCatalog.Box;

public record BoulderJoin(int center, int north, int east, int south, int west,
                          int northWest, int northEast, int southEast, int southWest,
                          int above, int below) {
    public static int piece(int layers, int form, int turn) {
        return layers | form << 4 | turn << 6;
    }

    public static int layers(int piece) {
        return piece & 15;
    }

    public List<Box> geometry() {
        var boxes = new ArrayList<>(base(center));
        if (layers(center) == 8 && above != 0) {
            boxes.replaceAll(box -> box.minY() == 0
                    ? new Box(box.minX(), 0, box.minZ(), box.maxX(), 16, box.maxZ()) : box);
        }
        for (int direction = 0; direction < 4; direction++) {
            int neighbor = switch (direction) {
                case 0 -> north;
                case 1 -> east;
                case 2 -> south;
                default -> west;
            };
            int height = Math.min(layers(center), layers(neighbor)) * 2;
            for (int y = 0; y < height; y += 4) {
                int top = Math.min(y + 4, height);
                double bevel = top == height && !(height == 16 && above != 0) ? 2 : 0;
                Box bridge = switch (direction) {
                    case 0 -> new Box(edge(west, northWest, top, bevel), y, 0,
                            16 - edge(east, northEast, top, bevel), top, 8);
                    case 1 -> new Box(8, y, edge(north, northEast, top, bevel), 16, top,
                            16 - edge(south, southEast, top, bevel));
                    case 2 -> new Box(edge(west, southWest, top, bevel), y, 8,
                            16 - edge(east, southEast, top, bevel), top, 16);
                    default -> new Box(0, y, edge(north, northWest, top, bevel), 8, top,
                            16 - edge(south, southWest, top, bevel));
                };
                boxes.add(bridge);
            }
        }
        if (layers(center) == 8 && above != 0 && above != center) {
            boxes.add(footprint(above, 8, 16));
        }
        if (layers(below) == 8 && below != center) {
            boxes.add(footprint(center, 0, Math.min(4, layers(center) * 2)));
        }
        return List.copyOf(boxes);
    }

    private static double edge(int side, int diagonal, int top, double bevel) {
        return Math.max(layers(side), layers(diagonal)) * 2 >= top ? 0 : bevel;
    }

    private static List<Box> base(int piece) {
        return BoulderCatalog.INSTANCE.geometry((piece >> 4) & 3, layers(piece), (piece >> 6) & 3);
    }

    private static Box footprint(int piece, double bottom, double top) {
        var base = base(piece).stream().filter(box -> box.minY() == 0).toList();
        return new Box(base.stream().mapToDouble(Box::minX).min().orElseThrow(), bottom,
                base.stream().mapToDouble(Box::minZ).min().orElseThrow(),
                base.stream().mapToDouble(Box::maxX).max().orElseThrow(), top,
                base.stream().mapToDouble(Box::maxZ).max().orElseThrow());
    }
}
