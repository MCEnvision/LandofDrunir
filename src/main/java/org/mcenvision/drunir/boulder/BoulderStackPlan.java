package org.mcenvision.drunir.boulder;

public record BoulderStackPlan(int lowerLayers, int upperLayers) {
    public static BoulderStackPlan add(int existing, int added) {
        if (existing < 0 || existing > 8 || added < 1 || added > 8) {
            throw new IllegalArgumentException("Invalid boulder layer count.");
        }
        int total = existing + added;
        return new BoulderStackPlan(Math.min(8, total), Math.max(0, total - 8));
    }
}
