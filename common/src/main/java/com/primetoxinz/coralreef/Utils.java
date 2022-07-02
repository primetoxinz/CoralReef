package com.primetoxinz.coralreef;

import dev.architectury.hooks.level.biome.*;
import dev.architectury.registry.registries.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;
import net.minecraft.core.*;
import net.minecraft.tags.*;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;

public class Utils {


    public static <T> Optional<T> getRandomTagElement(MappedRegistry<T> r, TagKey<T> tag, Random random) {
        return r.getTag(tag).flatMap((argx) -> argx.getRandomElement(random)).map(Holder::value);
    }

    public static Optional<BlockState> getRandomTagBlockState(MappedRegistry<Block> r, TagKey<Block> tag, Random random, Function<Block, BlockState> state) {
        return getRandomTagElement(r, tag, random).map(state);
    }

    private static Optional<Biome> getBiome(BiomeProperties biomeProperties) {
        try {
            Field field = biomeProperties.getClass().getDeclaredField("biome");
            field.setAccessible(true);
            Biome biome = (Biome) field.get(biomeProperties);
            return Optional.of(biome);
        } catch (NoSuchFieldException | IllegalAccessException | ClassCastException e) {
            return Optional.empty();
        }
    }

    public static <T> Holder.Direct<T> holder(RegistrySupplier<T> registrySupplier) {
        return new Holder.Direct<>(registrySupplier.get());
    }

    private static boolean hasTag(Biome biome, TagKey<Biome> tag) {
        return false;
    }

}
