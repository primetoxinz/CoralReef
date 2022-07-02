package com.primetoxinz.coralreef.blocks;

import com.primetoxinz.coralreef.*;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.core.particles.*;
import net.minecraft.server.level.*;
import net.minecraft.tags.*;
import net.minecraft.util.*;
import net.minecraft.world.item.context.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.*;
import net.minecraft.world.phys.shapes.*;
import org.jetbrains.annotations.*;

public class CoralBlock extends BushBlock implements LiquidBlockContainer {
    protected static final float AABB_OFFSET = 6.0F;
    protected static final VoxelShape SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 12.0, 14.0);

    public CoralBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }
    @Override
    public void animateTick(BlockState blockState, Level world, BlockPos pos, RandomSource rand) {
        // render bubbles
        if (world.getBlockState(pos.above()).is(Blocks.WATER)) {
            double offset = 0.0625D;
            for (int i = 0; i < 6; i++) {
                double x1 = (pos.getX() + rand.nextDouble());
                double y1 = (pos.getY() + rand.nextDouble());
                double z1 = (pos.getZ() + rand.nextDouble());
                if (i == 0 && !world.getBlockState(pos.above()).isSolidRender(world,pos)) {
                    y1 = (double) (pos.getY() + 1) + offset;
                }

                if (i == 1 && !world.getBlockState(pos.below()).isSolidRender(world,pos)) {
                    y1 = (double) (pos.getY()) - offset;
                }

                if (i == 2 && !world.getBlockState(pos.south()).isSolidRender(world,pos)) {
                    z1 = (double) (pos.getZ() + 1) + offset;
                }

                if (i == 3 && !world.getBlockState(pos.north()).isSolidRender(world,pos)) {
                    z1 = (double) (pos.getZ()) - offset;
                }

                if (i == 4 && !world.getBlockState(pos.east()).isSolidRender(world,pos)) {
                    x1 = (double) (pos.getX() + 1) + offset;
                }

                if (i == 5 && !world.getBlockState(pos.west()).isSolidRender(world,pos)) {
                    x1 = (double) (pos.getX()) - offset;
                }

                if (x1 < (double) pos.getX() || x1 > (double) (pos.getY() + 1) || y1 < 0.0D || y1 > (double) (pos.getY() + 1) || z1 < (double) pos.getZ() || z1 > (double) (pos.getZ() + 1)) {
                    world.addParticle(ParticleTypes.BUBBLE, x1, y1, z1, 0, 0, 0);
                }
            }
        }
    }

    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return SHAPE;
    }



    protected boolean mayPlaceOn(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return blockState.isFaceSturdy(blockGetter, blockPos, Direction.UP) && blockState.is(CoralReef.REEF_BASE_BLOCK_TAG);
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        FluidState fluidState = blockPlaceContext.getLevel().getFluidState(blockPlaceContext.getClickedPos());
        return fluidState.is(FluidTags.WATER) && fluidState.getAmount() == 8 ? super.getStateForPlacement(blockPlaceContext) : null;
    }

    public BlockState updateShape(BlockState blockState, Direction direction, BlockState blockState2, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos2) {
        BlockState blockState3 = super.updateShape(blockState, direction, blockState2, levelAccessor, blockPos, blockPos2);
        if (!blockState3.isAir()) {
            levelAccessor.scheduleTick(blockPos, Fluids.WATER, Fluids.WATER.getTickDelay(levelAccessor));
        }

        return blockState3;
    }


    public FluidState getFluidState(BlockState blockState) {
        return Fluids.WATER.getSource(false);
    }

    public boolean canPlaceLiquid(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState, Fluid fluid) {
        return false;
    }

    public boolean placeLiquid(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState, FluidState fluidState) {
        return false;
    }
}

