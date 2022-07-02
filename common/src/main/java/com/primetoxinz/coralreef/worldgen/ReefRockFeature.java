package com.primetoxinz.coralreef.worldgen;

import com.mojang.serialization.*;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.levelgen.feature.*;
import net.minecraft.world.level.levelgen.feature.configurations.*;
import net.minecraft.world.level.material.*;

public class ReefRockFeature extends Feature<BlockStateConfiguration> {
    public ReefRockFeature(Codec<BlockStateConfiguration> codec) {
        super(codec);
    }

    public boolean place(FeaturePlaceContext<BlockStateConfiguration> featurePlaceContext) {
        BlockPos blockPos = featurePlaceContext.origin();
        WorldGenLevel worldGenLevel = featurePlaceContext.level();
        Random randomSource = featurePlaceContext.random();

        BlockStateConfiguration blockStateConfiguration;
        for (blockStateConfiguration = (BlockStateConfiguration) featurePlaceContext.config(); blockPos.getY() > worldGenLevel.getMinBuildHeight() + 3; blockPos = blockPos.below()) {
            if (!worldGenLevel.isEmptyBlock(blockPos.below())) {
                BlockState blockState = worldGenLevel.getBlockState(blockPos.below());
                if (isDirt(blockState) || isStone(blockState)) {
                    break;
                }
            }
        }

        if (blockPos.getY() <= worldGenLevel.getMinBuildHeight() + 3) {
            return false;
        } else {
            for (int i = 0; i < 3; ++i) {
                int j = randomSource.nextInt(2);
                int k = randomSource.nextInt(2);
                int l = randomSource.nextInt(2);
                float f = (float) (j + k + l) * 0.333F + 0.5F;
                Iterator var11 = BlockPos.betweenClosed(blockPos.offset(-j, -k, -l), blockPos.offset(j, k, l)).iterator();

                while (var11.hasNext()) {
                    BlockPos blockPos2 = (BlockPos) var11.next();
                    if (blockPos2.distSqr(blockPos) <= (double) (f * f)) {
                        if (worldGenLevel.getBlockState(blockPos2).getFluidState().is(Fluids.WATER) && worldGenLevel.getBlockState(blockPos2.above()).is(Blocks.WATER)) {
                            worldGenLevel.setBlock(blockPos2, blockStateConfiguration.state, 4);
                        }
                    }
                }
                blockPos = blockPos.offset(-1 + randomSource.nextInt(2), -randomSource.nextInt(2), -1 + randomSource.nextInt(2));
            }

            return true;
        }
    }
}
