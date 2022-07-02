package com.primetoxinz.coralreef.worldgen;

import com.mojang.serialization.*;
import com.primetoxinz.coralreef.*;
import com.primetoxinz.coralreef.blocks.*;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.tags.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.levelgen.feature.*;
import net.minecraft.world.level.levelgen.feature.configurations.*;
import net.minecraft.world.level.material.*;


public class ReefBaseFeature extends Feature<DiskConfiguration> {

    private static double coralSparsity = 0.9;

    public ReefBaseFeature(Codec<DiskConfiguration> codec) {
        super(codec);
    }

    public boolean place(FeaturePlaceContext<DiskConfiguration> arg) {
        if (!arg.level().getFluidState(arg.origin()).is(FluidTags.WATER)) {
            return false;
        }

        DiskConfiguration diskConfiguration = arg.config();
        BlockPos blockPos = arg.origin();
        WorldGenLevel worldGenLevel = arg.level();
        boolean bl = false;
        int i = blockPos.getY();
        int j = i + diskConfiguration.halfHeight();
        int k = i - diskConfiguration.halfHeight() - 1;
        boolean bl2 = diskConfiguration.state().getBlock() instanceof FallingBlock;
        int l = diskConfiguration.radius().sample(arg.random());
        Random random = arg.random();
        for (int m = blockPos.getX() - l; m <= blockPos.getX() + l; ++m) {
            for (int n = blockPos.getZ() - l; n <= blockPos.getZ() + l; ++n) {
                int o = m - blockPos.getX();
                int p = n - blockPos.getZ();
                if (o * o + p * p <= l * l) {
                    boolean bl3 = false;

                    for (int q = j; q >= k; --q) {
                        BlockPos blockPos2 = new BlockPos(m, q, n);
                        BlockState blockState = worldGenLevel.getBlockState(blockPos2);
                        Block block = blockState.getBlock();
                        boolean bl4 = false;
                        if (q > k) {
                            Iterator var21 = diskConfiguration.targets().iterator();

                            while (var21.hasNext()) {
                                BlockState blockState2 = (BlockState) var21.next();
                                if (blockState2.is(block) && worldGenLevel.getBlockState(blockPos2.above()).getFluidState().is(Fluids.WATER)) {
                                    worldGenLevel.setBlock(blockPos2, diskConfiguration.state(), 2);
                                    this.markAboveForPostProcessing(worldGenLevel, blockPos2);
                                    BlockState belowState = worldGenLevel.getBlockState(blockPos2);
                                    BlockState state = worldGenLevel.getBlockState(blockPos2.above());
                                    BlockState aboveState = worldGenLevel.getBlockState(blockPos2.above(2));

                                    if (belowState.is(CoralReef.REEF_BASE_BLOCK_TAG) && state.is(Blocks.WATER) &&  aboveState.is(Blocks.WATER)) {
                                        if (random.nextDouble() <= coralSparsity) {
                                            placeCoral(random, worldGenLevel, blockPos2.above());
                                        }
                                    }

                                    bl = true;
                                    bl4 = true;
                                    break;
                                }
                            }
                        }

                        bl3 = bl4;
                    }
                }
            }
        }

        return bl;
    }

    public void placeCoral(Random random, WorldGenLevel worldGenLevel, BlockPos blockPos) {
        Utils.getRandomTagBlockState(Registry.BLOCK, CoralReef.REEF_CORAL_TAG, random, Block::defaultBlockState).ifPresent(blockState2 -> {
            worldGenLevel.setBlock(blockPos, blockState2, Block.UPDATE_NONE);

            if(blockState2.getBlock() instanceof GrowableCoralBlock) {
                var height = random.nextInt(0,3);
                for(int i = 0; i <= height; i++) {
                    var pos = blockPos.above(i);
                    if(worldGenLevel.getBlockState(pos).is(Blocks.WATER) && worldGenLevel.getBlockState(pos.above()).is(Blocks.WATER)) {
                        worldGenLevel.setBlock(pos, blockState2, 2);
                    }
                }
            }

        });
    }

}
