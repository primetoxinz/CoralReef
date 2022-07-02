package com.primetoxinz.coralreef.blocks;

import com.primetoxinz.coralreef.*;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.server.level.*;
import net.minecraft.tags.*;
import net.minecraft.world.item.context.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.*;
import net.minecraft.world.phys.shapes.*;
import org.jetbrains.annotations.*;

public class GrowableCoralBlock extends Block implements LiquidBlockContainer {
    public static final IntegerProperty AGE;
    protected static final float AABB_OFFSET = 6.0F;
    protected static final VoxelShape SHAPE;
    public final int maxAge;
    public GrowableCoralBlock(int maxAge, BlockBehaviour.Properties properties) {
        super(properties);
        this.maxAge = maxAge;
        this.registerDefaultState((BlockState) ((BlockState) this.stateDefinition.any()).setValue(AGE, 0));
    }

    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return SHAPE;
    }

    public void tick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, Random random) {
        if (!blockState.canSurvive(serverLevel, blockPos)) {
            serverLevel.destroyBlock(blockPos, true);
        }
    }

    public void randomTick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, Random random) {
        if (serverLevel.isEmptyBlock(blockPos.above())) {
            int i;
            for (i = 1; serverLevel.getBlockState(blockPos.below(i)).is(this); ++i) {
            }

            if (i < this.maxAge) {
                int j = (Integer) blockState.getValue(AGE);
                if (j == 15) {
                    serverLevel.setBlockAndUpdate(blockPos.above(), this.defaultBlockState());
                    serverLevel.setBlock(blockPos, (BlockState) blockState.setValue(AGE, 0), 4);
                } else {
                    serverLevel.setBlock(blockPos, (BlockState) blockState.setValue(AGE, j + 1), 4);
                }
            }
        }

    }


    public boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
        if(true) {
            return true;
        }
        BlockState blockState2 = levelReader.getBlockState(blockPos.below());
        if (blockState2.is(this) && blockState2.getFluidState().is(Fluids.WATER)) {
            return true;
        } else {
            if (blockState2.is(CoralReef.REEF_BASE_BLOCK_TAG) && blockState.getFluidState().is(Fluids.WATER)) {
                return true;
            }

            return false;
        }
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    static {
        AGE = BlockStateProperties.AGE_15;
        SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 16.0, 14.0);
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

