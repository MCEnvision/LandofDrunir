package org.mcenvision.drunir.worldgen;

import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseRouter;

public final class IslandDensityFunction implements DensityFunction {
    private final DensityFunction original;
    private final IslandControl control;

    public IslandDensityFunction(DensityFunction original, IslandControl control) {
        this.original = original;
        this.control = control;
    }

    public static NoiseRouter wrap(NoiseRouter router, IslandControl control) {
        return new NoiseRouter(router.barrierNoise(), router.fluidLevelFloodednessNoise(),
                router.fluidLevelSpreadNoise(), router.lavaNoise(), router.temperature(), router.vegetation(),
                router.continents(), router.erosion(), router.depth(), router.ridges(),
                new IslandDensityFunction(router.initialDensityWithoutJaggedness(), control),
                new IslandDensityFunction(router.finalDensity(), control),
                router.veinToggle(), router.veinRidged(), router.veinGap());
    }

    @Override
    public double compute(FunctionContext position) {
        IslandContext context = control.context();
        if (context == null || !context.region().affects(position.blockX(), position.blockZ())) {
            return original.compute(position);
        }
        IslandRegion region = context.region();
        double weight = region.influence(position.blockX(), position.blockZ());
        double density = (region.surfaceHeight(position.blockX(), position.blockZ()) - position.blockY()) / 8;
        if (weight == 1) return density;
        double vanilla = original.compute(position);
        return vanilla + (density - vanilla) * weight;
    }

    @Override
    public void fillArray(double[] values, ContextProvider contexts) { contexts.fillAllDirectly(values, this); }

    @Override
    public DensityFunction mapAll(Visitor visitor) {
        return visitor.apply(new IslandDensityFunction(original.mapAll(visitor), control));
    }

    @Override
    public double minValue() { return Double.NEGATIVE_INFINITY; }

    @Override
    public double maxValue() { return Double.POSITIVE_INFINITY; }

    @Override
    public KeyDispatchDataCodec<? extends DensityFunction> codec() {
        throw new UnsupportedOperationException("Runtime island density is derived from world data and is not serialized.");
    }
}
