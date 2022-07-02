package com.primetoxinz.coralreef.worldgen;

import com.mojang.serialization.*;
import com.primetoxinz.coralreef.*;
import com.primetoxinz.coralreef.blocks.*;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.tags.*;
import net.minecraft.util.*;
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

    protected boolean placeColumn(DiskConfiguration diskConfiguration, WorldGenLevel worldGenLevel, RandomSource randomSource, int i, int j, BlockPos.MutableBlockPos mutableBlockPos) {
        boolean bl = false;
        BlockState blockState = null;

        for (int k = i; k > j; --k) {
            mutableBlockPos.setY(k);
            if (diskConfiguration.target().test(worldGenLevel, mutableBlockPos)) {
                BlockState blockState2 = diskConfiguration.stateProvider().getState(worldGenLevel, randomSource, mutableBlockPos);
                worldGenLevel.setBlock(mutableBlockPos, blockState2, 2);
                this.markAboveForPostProcessing(worldGenLevel, mutableBlockPos);
                bl = true;
            }
        }

        if (bl) {
            BlockState belowState = worldGenLevel.getBlockState(mutableBlockPos);
            BlockState state = worldGenLevel.getBlockState(mutableBlockPos.above());
            BlockState aboveState = worldGenLevel.getBlockState(mutableBlockPos.above(2));

            if (belowState.is(CoralReef.REEF_BASE_BLOCK_TAG) && state.is(Blocks.WATER) && aboveState.is(Blocks.WATER)) {
                if (randomSource.nextDouble() <= coralSparsity) {
                    placeCoral(randomSource, worldGenLevel, mutableBlockPos.above());
                }
            }
        }
        return bl;
    }

    public boolean place(FeaturePlaceContext<DiskConfiguration> featurePlaceContext) {

        DiskConfiguration diskConfiguration = (DiskConfiguration) featurePlaceContext.config();
        BlockPos blockPos = featurePlaceContext.origin();
        WorldGenLevel worldGenLevel = featurePlaceContext.level();
        RandomSource randomSource = featurePlaceContext.random();
        boolean bl = false;
        int i = blockPos.getY();
        int j = i + diskConfiguration.halfHeight();
        int k = i - diskConfiguration.halfHeight() - 1;
        int l = diskConfiguration.radius().sample(randomSource);
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        Iterator var12 = BlockPos.betweenClosed(blockPos.offset(-l, 0, -l), blockPos.offset(l, 0, l)).iterator();

        while (var12.hasNext()) {
            BlockPos blockPos2 = (BlockPos) var12.next();
            int m = blockPos2.getX() - blockPos.getX();
            int n = blockPos2.getZ() - blockPos.getZ();
            if (m * m + n * n <= l * l) {
                bl |= this.placeColumn(diskConfiguration, worldGenLevel, randomSource, j, k, mutableBlockPos.set(blockPos2));
            }
        }

        return bl;
    }

    public void placeCoral(RandomSource random, WorldGenLevel worldGenLevel, BlockPos blockPos) {
        Utils.getRandomTagBlockState(Registry.BLOCK, CoralReef.REEF_CORAL_TAG, random, Block::defaultBlockState).ifPresent(blockState2 -> {
            worldGenLevel.setBlock(blockPos, blockState2, Block.UPDATE_NONE);

            if (blockState2.getBlock() instanceof GrowableCoralBlock) {
                var height = random.nextInt(0, 3);
                for (int i = 0; i <= height; i++) {
                    var pos = blockPos.above(i);
                    if (worldGenLevel.getBlockState(pos).is(Blocks.WATER) && worldGenLevel.getBlockState(pos.above()).is(Blocks.WATER)) {
                        worldGenLevel.setBlock(pos, blockState2, 2);
                    }
                }
            }

        });
    }

}
