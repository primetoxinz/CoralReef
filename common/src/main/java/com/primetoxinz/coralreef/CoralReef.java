package com.primetoxinz.coralreef;

//import com.google.common.collect.ImmutableList;
//import com.google.common.collect.Sets;
//import net.minecraft.core.Registry;
//import net.minecraft.data.BuiltinRegistries;
//import net.minecraft.data.worldgen.Features;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.util.random.SimpleWeightedRandomList;
//import net.minecraft.util.valueproviders.UniformInt;
//import net.minecraft.world.item.BlockItem;
//import net.minecraft.world.item.CreativeModeTab;
//import net.minecraft.world.item.Item;
//import net.minecraft.world.level.block.Block;
//import net.minecraft.world.level.block.Blocks;
//import net.minecraft.world.level.block.SoundType;
//import net.minecraft.world.level.block.state.BlockBehaviour;
//import net.minecraft.world.level.block.state.BlockState;
//import net.minecraft.world.level.levelgen.GenerationStep;
//import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
//import net.minecraft.world.level.levelgen.feature.Feature;
//import net.minecraft.world.level.levelgen.feature.blockplacers.SimpleBlockPlacer;
//import net.minecraft.world.level.levelgen.feature.configurations.DiskConfiguration;
//import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
//import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
//import net.minecraft.world.level.levelgen.feature.stateproviders.SimpleStateProvider;
//import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
//import net.minecraft.world.level.levelgen.placement.CarvingMaskDecoratorConfiguration;
//import net.minecraft.world.level.levelgen.placement.FeatureDecorator;
//import net.minecraft.world.level.levelgen.placement.NoiseCountFactorDecoratorConfiguration;
//import net.minecraft.world.level.material.Material;
//import net.minecraft.world.level.material.MaterialColor;
//import net.minecraftforge.common.MinecraftForge;
//import net.minecraftforge.event.world.BiomeLoadingEvent;
//import net.minecraftforge.eventbus.api.SubscribeEvent;
//import net.minecraftforge.fml.common.Mod;
//import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
//import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
//import net.minecraftforge.fmllegacy.RegistryObject;
//import net.minecraftforge.registries.DeferredRegister;
//import net.minecraftforge.registries.ForgeRegistries;

import com.google.common.collect.*;
import com.primetoxinz.coralreef.blocks.*;
import com.primetoxinz.coralreef.blocks.CoralBlock;
import com.primetoxinz.coralreef.worldgen.*;
import dev.architectury.hooks.level.biome.*;
import dev.architectury.registry.level.biome.*;
import dev.architectury.registry.registries.*;
import java.util.*;
import java.util.function.*;
import net.minecraft.core.*;
import net.minecraft.data.worldgen.placement.*;
import net.minecraft.resources.*;
import net.minecraft.tags.*;
import net.minecraft.util.valueproviders.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.feature.*;
import net.minecraft.world.level.levelgen.feature.configurations.*;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraft.world.level.material.*;
import org.apache.logging.log4j.*;

public class CoralReef {
    public static final String MOD_ID = "coralreef";
    private static final Logger LOGGER = LogManager.getLogger();

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(MOD_ID, Registry.ITEM_REGISTRY);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(MOD_ID, Registry.BLOCK_REGISTRY);
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(MOD_ID, Registry.FEATURE_REGISTRY);
    public static final DeferredRegister<ConfiguredFeature<?, ?>> CONFIGURED_FEATURES = DeferredRegister.create(MOD_ID, Registry.CONFIGURED_FEATURE_REGISTRY);
    public static final DeferredRegister<PlacedFeature> PLACED_FEATURES = DeferredRegister.create(MOD_ID, Registry.PLACED_FEATURE_REGISTRY);
    // Blocks
    public static final RegistrySupplier<Block> REEF1_BLOCK = BLOCKS.register("reef1", () ->
            new Block(
                    BlockBehaviour.Properties
                            .of(Material.STONE, MaterialColor.COLOR_YELLOW)
                            .requiresCorrectToolForDrops()
                            .strength(1.5F, 10.0F)
                            .sound(SoundType.STONE)
            )
    );
    //TUBE_CORAL_BLOCK = register("tube_coral_block", );

    public static final RegistrySupplier<Block> REEF2_BLOCK = BLOCKS.register("reef2", () ->
            new Block(
                    BlockBehaviour.Properties
                            .of(Material.STONE, MaterialColor.COLOR_YELLOW)
                            .requiresCorrectToolForDrops()
                            .strength(1.5F, 10.0F)
                            .sound(SoundType.STONE)
            )
    );

    record Coral(String name, MaterialColor materialColor, int maxHeight) {
        @Override
        public String name() {
            return "coral_" + name;
        }
    }

    // Tags

    public static final TagKey<Block> REEF_BASE_BLOCK_TAG = TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation(CoralReef.MOD_ID, "reef_base_blocks"));
    public static final TagKey<Block> REEF_CORAL_TAG = TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation(CoralReef.MOD_ID, "reef_coral"));
    public static final TagKey<Biome> HAS_REEF = TagKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(CoralReef.MOD_ID, "has_reef"));


    public static final RegistrySupplier<Feature<DiskConfiguration>> REEF_BASE = FEATURES.register("reef_base", () -> new ReefBaseFeature(DiskConfiguration.CODEC));
    public static final RegistrySupplier<ConfiguredFeature<?, ?>> REEF_DISK_CONFIG = CoralReef.CONFIGURED_FEATURES.register("reef_disk", () ->
            new ConfiguredFeature<>(REEF_BASE.get(), new DiskConfiguration(REEF1_BLOCK.get().defaultBlockState(), UniformInt.of(2, 6), 2, List.of(Blocks.SAND.defaultBlockState(), Blocks.GRAVEL.defaultBlockState(), Blocks.DIRT.defaultBlockState(), Blocks.GRASS_BLOCK.defaultBlockState())))
    );

    public static final RegistrySupplier<PlacedFeature> REEF_FEATURE_PLACEMENT = PLACED_FEATURES.register("reef", () -> {
        Holder<ConfiguredFeature<?, ?>> holder = Holder.direct(REEF_DISK_CONFIG.get());
        return new PlacedFeature(holder, ImmutableList.of(InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_TOP_SOLID, BiomeFilter.biome()));
    });

    public static List<RegistrySupplier<Block>> CORALS = Lists.newArrayList();

    public static void init() {
        var lightLevel = 15;
        var corals = Lists.newArrayList(
                new Coral("orange", MaterialColor.COLOR_ORANGE, 1),
                new Coral("magenta", MaterialColor.COLOR_MAGENTA, 1),
                new Coral("pink", MaterialColor.COLOR_PINK, 1),
                new Coral("cyan", MaterialColor.COLOR_CYAN, 1),
                new Coral("lime", MaterialColor.COLOR_LIGHT_GREEN, 3),
                new Coral("brown", MaterialColor.COLOR_BROWN, 3)
        );
        for (var coral : corals) {
            var name = coral.name();
            var materialColor = coral.materialColor();
            Supplier<Block> blockSupplier;

            var behavior = BlockBehaviour.Properties.of(Material.WATER_PLANT, materialColor).lightLevel(value -> 15).noCollission().instabreak().sound(SoundType.WET_GRASS);

            if (coral.maxHeight() > 1) {
                blockSupplier = () -> new GrowableCoralBlock(coral.maxHeight(), behavior);
            } else {
                blockSupplier = () -> new CoralBlock(behavior);
            }
            RegistrySupplier<Block> block = BLOCKS.register(name, blockSupplier);
            CORALS.add(block);
            ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS)));
        }
        //Item Blocks
        ITEMS.register("reef1", () -> new BlockItem(REEF1_BLOCK.get(), new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS)));
        ITEMS.register("reef2", () -> new BlockItem(REEF2_BLOCK.get(), new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS)));

        BLOCKS.register();
        ITEMS.register();
        FEATURES.register();
        CONFIGURED_FEATURES.register();

        PLACED_FEATURES.register();
        var categories = Lists.newArrayList(Biome.BiomeCategory.OCEAN, Biome.BiomeCategory.RIVER);

        BiomeModifications.addProperties(biomeContext -> {
            var properties = biomeContext.getProperties();
            return categories.contains(properties.getCategory());
        }, (biomeContext, mutable) -> {
            GenerationProperties.Mutable mut = mutable.getGenerationProperties();
            try {
                mut.addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, Holder.direct(REEF_FEATURE_PLACEMENT.get()));
            } catch (Exception e) {
            }
        });

    }

}
