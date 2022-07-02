package com.primetoxinz.coralreef;


import com.google.common.collect.*;
import com.primetoxinz.coralreef.blocks.CoralBlock;
import com.primetoxinz.coralreef.blocks.*;
import com.primetoxinz.coralreef.worldgen.*;
import dev.architectury.hooks.level.biome.*;
import dev.architectury.registry.level.biome.*;
import dev.architectury.registry.registries.*;
import java.util.*;
import java.util.function.*;
import net.minecraft.core.*;
import net.minecraft.data.*;
import net.minecraft.data.worldgen.features.*;
import net.minecraft.data.worldgen.placement.*;
import net.minecraft.resources.*;
import net.minecraft.tags.*;
import net.minecraft.util.valueproviders.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.blockpredicates.*;
import net.minecraft.world.level.levelgen.feature.*;
import net.minecraft.world.level.levelgen.feature.configurations.*;
import net.minecraft.world.level.levelgen.feature.stateproviders.*;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraft.world.level.material.*;
import org.apache.logging.log4j.*;

public class CoralReef {
    public static final String MOD_ID = "coralreef";
    private static final Logger LOGGER = LogManager.getLogger();

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(MOD_ID, Registry.ITEM_REGISTRY);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(MOD_ID, Registry.BLOCK_REGISTRY);
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(MOD_ID, Registry.FEATURE_REGISTRY);
    //    public static final DeferredRegister<ConfiguredFeature<?, ?>> CONFIGURED_FEATURES = DeferredRegister.create(MOD_ID, Registry.CONFIGURED_FEATURE_REGISTRY);
//    public static final DeferredRegister<PlacedFeature> PLACED_FEATURES = DeferredRegister.create(MOD_ID, Registry.PLACED_FEATURE_REGISTRY);
    // Blocks
    public static final RegistrySupplier<Block> REEF1_BLOCK = BLOCKS.register("reef1", () ->
            new Block(
                    BlockBehaviour.Properties
                            .of(Material.STONE, MaterialColor.COLOR_YELLOW)
                            .requiresCorrectToolForDrops()
                            .strength(1.5F, 6.0F)
                            .sound(SoundType.STONE)
            )
    );
    public static final RegistrySupplier<Block> REEF2_BLOCK = BLOCKS.register("reef2", () ->
            new Block(
                    BlockBehaviour.Properties
                            .of(Material.STONE, MaterialColor.COLOR_YELLOW)
                            .requiresCorrectToolForDrops()
                            .strength(1.5F, 6.0F)
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
    public static final RegistrySupplier<Feature<BlockStateConfiguration>> REEF_ROCK = FEATURES.register("reef_rock", () -> new ReefRockFeature(BlockStateConfiguration.CODEC));
//    public static final RegistrySupplier<ConfiguredFeature<?, ?>> REEF_DISK_CONFIG = CoralReef.CONFIGURED_FEATURES.register("reef_disk", CoralReef::createReefDisk);
//    public static final RegistrySupplier<ConfiguredFeature<?, ?>> REEF_ROCK_CONFIG = CoralReef.CONFIGURED_FEATURES.register("reef_rock", CoralReef::createReefRock);
//    public static final RegistrySupplier<PlacedFeature> REEF_FEATURE_PLACEMENT = PLACED_FEATURES.register("reef", () -> CoralReef.createReef(Utils.holder(REEF_DISK_CONFIG)));
//    public static final RegistrySupplier<PlacedFeature> REEF_ROCK_PLACEMENT = PLACED_FEATURES.register("reef_rock", () -> CoralReef.createReefRock(Utils.holder(REEF_ROCK_CONFIG)));

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

            var behavior = BlockBehaviour.Properties
                    .of(Material.WATER_PLANT, materialColor)
                    .lightLevel(value -> 15)
                    .noCollission()
                    .instabreak().sound(SoundType.WET_GRASS);

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
    }

    public static void postInit() {

        Holder<ConfiguredFeature<?, ?>> reefDisk = BuiltinRegistries.registerExact(BuiltinRegistries.CONFIGURED_FEATURE, "reef_disk", new ConfiguredFeature<>(
                REEF_BASE.get(),
                new DiskConfiguration(RuleBasedBlockStateProvider.simple(REEF1_BLOCK.get()), BlockPredicate.matchesBlocks(List.of(Blocks.DIRT, Blocks.GRASS_BLOCK, Blocks.SAND, Blocks.GRAVEL)), UniformInt.of(2, 3), 1)
        ));
        Holder<ConfiguredFeature<?, ?>> reefRock = BuiltinRegistries.registerExact(BuiltinRegistries.CONFIGURED_FEATURE, "reef_rock", new ConfiguredFeature<>(REEF_ROCK.get(), new BlockStateConfiguration(REEF2_BLOCK.get().defaultBlockState())));
        Holder<PlacedFeature> reefPlaced = BuiltinRegistries.registerExact(BuiltinRegistries.PLACED_FEATURE, "reef", new PlacedFeature(reefDisk, ImmutableList.of(InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_TOP_SOLID, BiomeFilter.biome())));
        Holder<PlacedFeature> reefRockPlaced = BuiltinRegistries.registerExact(BuiltinRegistries.PLACED_FEATURE, "reef_rock", new PlacedFeature(reefRock, ImmutableList.of(CountPlacement.of(2), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome())));
        BiomeModifications.addProperties(biomeContext -> biomeContext.hasTag(HAS_REEF), (biomeContext, mutable) -> {
            GenerationProperties.Mutable mut = mutable.getGenerationProperties();
            mut.addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, reefPlaced);
            mut.addFeature(GenerationStep.Decoration.LOCAL_MODIFICATIONS, reefRockPlaced);
        });
    }
}
