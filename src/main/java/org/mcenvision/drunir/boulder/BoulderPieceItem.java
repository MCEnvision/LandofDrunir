package org.mcenvision.drunir.boulder;

import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;

import java.util.List;
import java.util.Map;

public final class BoulderPieceItem extends BlockItem {
    private final int layers;
    private final int form;
    private final boolean primary;

    public BoulderPieceItem(Block block, int layers, int form, boolean primary, Properties properties) {
        super(block, properties);
        this.layers = layers;
        this.form = form;
        this.primary = primary;
    }

    @Override
    public void registerBlocks(Map<Block, Item> items, Item item) {
        if (primary) {
            super.registerBlocks(items, item);
        }
    }

    @Override
    public String getDescriptionId() {
        return getOrCreateDescriptionId();
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> text, TooltipFlag flag) {
        super.appendHoverText(stack, context, text, flag);
        text.add(Component.translatable("tooltip.landofdrunir.boulder.build"));
        text.add(Component.translatable("tooltip.landofdrunir.boulder.separate"));
    }

    @Override
    protected BlockState getPlacementState(BlockPlaceContext context) {
        var existing = context.getLevel().getBlockState(context.getClickedPos());
        var state = existing.is(getBlock())
                ? existing.setValue(BoulderPieceBlock.LAYERS, BoulderStackPlan.add(existing.getValue(BoulderPieceBlock.LAYERS), layers).lowerLayers())
                : piece(context, layers);
        return canPlace(context, state) ? state : null;
    }

    private BlockState piece(BlockPlaceContext context, int height) {
        return getBlock().defaultBlockState().setValue(BoulderPieceBlock.LAYERS, height)
                .setValue(BoulderPieceBlock.FORM, form)
                .setValue(BoulderPieceBlock.FACING, context.getHorizontalDirection().getOpposite())
                .setValue(BoulderPieceBlock.WATERLOGGED,
                        context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER);
    }

    @Override
    protected boolean placeBlock(BlockPlaceContext context, BlockState state) {
        var level = context.getLevel();
        var pos = context.getClickedPos();
        var previous = level.getBlockState(pos);
        int oldLayers = previous.is(getBlock()) ? previous.getValue(BoulderPieceBlock.LAYERS) : 0;
        var plan = BoulderStackPlan.add(oldLayers, layers);
        if (plan.upperLayers() == 0) {
            return super.placeBlock(context, state);
        }

        var above = pos.above();
        var upperContext = BlockPlaceContext.at(context, above, Direction.UP);
        var upperPrevious = level.getBlockState(above);
        var upper = piece(upperContext, plan.upperLayers());
        var player = context.getPlayer();
        var collision = player == null ? CollisionContext.empty() : CollisionContext.of(player);
        // Reserve the overflow before changing either block or consuming the item.
        if (upperPrevious.getBlock() instanceof BoulderPieceBlock || !upperPrevious.canBeReplaced(upperContext)
                || level.isOutsideBuildHeight(above) || !level.getWorldBorder().isWithinBounds(above)
                || !upper.canSurvive(level, above) || !level.isUnobstructed(upper, above, collision)
                || player != null && (!level.mayInteract(player, above)
                    || !player.mayUseItemAt(above, Direction.UP, context.getItemInHand()))) {
            return false;
        }
        if (!super.placeBlock(context, state)) {
            return false;
        }
        if (!level.setBlock(above, upper, 11)) {
            level.setBlock(pos, previous, 11);
            return false;
        }
        return true;
    }
}
