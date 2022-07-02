package com.primetoxinz.coralreef.worldgen;

import com.mojang.serialization.Codec;
import com.primetoxinz.coralreef.*;
import dev.architectury.registry.registries.*;
import net.minecraft.core.*;
import net.minecraft.data.*;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.*;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.Random;

public class ReefCoralFeature extends Feature<NoneFeatureConfiguration> {

    public ReefCoralFeature(Codec<NoneFeatureConfiguration> p_65429_) {
        super(p_65429_);
    }

    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> featurePlaceContext) {
        WorldGenLevel worldGenLevel = featurePlaceContext.level();
        BlockPos blockPos = featurePlaceContext.origin();
        RandomSource random = featurePlaceContext.random();
        int j = worldGenLevel.getHeight(Heightmap.Types.OCEAN_FLOOR, blockPos.getX(), blockPos.getZ());
        final BlockPos blockPos2 = new BlockPos(blockPos.getX(), j, blockPos.getZ());
        BlockState belowState = worldGenLevel.getBlockState(blockPos2.below());
        BlockState state = worldGenLevel.getBlockState(blockPos2);
        BlockState aboveState = worldGenLevel.getBlockState(blockPos2.above());
        if (belowState.is(CoralReef.REEF_BASE_BLOCK_TAG) && state.is(Blocks.WATER) && aboveState.is(Blocks.WATER)) {
            return Utils.getRandomTagBlockState(Registry.BLOCK, CoralReef.REEF_CORAL_TAG, random, Block::defaultBlockState).map(blockState2 -> {
                worldGenLevel.setBlock(blockPos, blockState2, 2);
                return true;
            }).orElse(false);
        }
        return false;
    }
}

