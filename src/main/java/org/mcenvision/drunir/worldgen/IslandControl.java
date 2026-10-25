package org.mcenvision.drunir.worldgen;

public final class IslandControl {
    private volatile IslandContext context;

    public IslandContext context() { return context; }
    public void bind(IslandContext context) { this.context = context; }
}
