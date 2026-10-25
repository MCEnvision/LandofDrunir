package org.mcenvision.drunir.boulder;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public record BoulderCatalog(List<Material> materials, List<Size> sizes, List<Double> widths, List<Form> forms) {
    public static final BoulderCatalog INSTANCE = load();
    public static final int MAX_LAYERS = 8;

    public record Material(String id, int weight) {}
    public record Size(String id, int layers, int weight) {}
    public record Form(String id, List<List<Double>> boxes) {}
    public record Box(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        public Box turn() {
            return new Box(16 - maxZ, minY, minX, 16 - minZ, maxY, maxX);
        }
    }

    public List<Box> geometry(int form, int layers, int turns) {
        if (layers < 1 || layers > MAX_LAYERS) {
            throw new IllegalArgumentException("Boulder layers must be between 1 and 8.");
        }
        double width = widths.get(layers - 1) / 16;
        double height = layers / 8.0;
        return forms.get(form).boxes.stream().map(box -> {
            Box result = new Box(8 + (box.get(0) - 8) * width, box.get(1) * height,
                    8 + (box.get(2) - 8) * width, 8 + (box.get(3) - 8) * width,
                    box.get(4) * height, 8 + (box.get(5) - 8) * width);
            for (int turn = 0; turn < Math.floorMod(turns, 4); turn++) {
                result = result.turn();
            }
            return result;
        }).toList();
    }

    public static String blockId(String material) {
        return material + "_boulder_piece";
    }

    public static String itemId(String material, String size, String form) {
        return material + "_" + size + "_" + form + "_boulder";
    }

    private static BoulderCatalog load() {
        try (var stream = BoulderCatalog.class.getResourceAsStream("/assets/landofdrunir/boulders.json")) {
            if (stream == null) {
                throw new IllegalStateException("The boulder catalog is missing.");
            }
            var catalog = new Gson().fromJson(new InputStreamReader(stream, StandardCharsets.UTF_8), BoulderCatalog.class);
            if (catalog.forms.size() != 4 || catalog.widths.size() != MAX_LAYERS) {
                throw new IllegalStateException("The boulder catalog requires four forms and eight widths.");
            }
            return catalog;
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to read the boulder catalog.", exception);
        }
    }
}
