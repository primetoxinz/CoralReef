package primetoxinz.coralreef;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Created by tyler on 8/17/16.
 */
@Mod.EventBusSubscriber(modid = CoralReef.MODID)
@Mod(modid = CoralReef.MODID, name = CoralReef.NAME, version = CoralReef.VERSION, acceptedMinecraftVersions = "[1.12, 1.13)")
public class CoralReef {

    public static final String MODID = "coralreef";
    public static final String NAME = "CoralReef";
    public static final String VERSION = "2.0";

    @Config(modid = MODID)
    public static class ConfigHandler {

        @Config.Comment("Array of dimension ids in which Coral Reefs will spawn. Empty will allow all dimensions")
        public static int[] dimensions = new int[]{0};

        @Config.Comment("Light level of coral")
        @Config.RequiresMcRestart
        public static int coralLightLevel = 15;

        @Config.Comment(value = "Array of biomes which will allow coral reefs to spawn if there is water. Empty will allow all biomes")
        public static String[] biomes = new String[]{"ocean", "deep ocean", "beach"};

        @Config.Comment("Bubble Particles from coral")
        public static boolean bubbles = true;

        public static Reef reef = new Reef();

        public static Rock rock = new Rock();

        public static class Reef {
            @Config.RangeDouble(min = 0, max = 1)
            @Config.Comment("Percentage of the sparsity of the coral on a reef")
            public double coralSparsity = 0.9;

            @Config.RangeDouble(min = 0, max = 1)
            @Config.Comment("Percentage of the sparsity of the reef blocks")
            public double reefSparsity = 0.9;
        }

        public static class Rock {
            @Config.Comment("Chance for a Dry Reef to spawn")
            public double chance = 0.5;
        }

    }


    public static BlockCoral CORAL = (BlockCoral) new BlockCoral().setRegistryName("coral").setUnlocalizedName("coral");
    public static BlockReef REEF = (BlockReef) new BlockReef().setRegistryName("reef").setUnlocalizedName("reef");

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        GameRegistry.registerWorldGenerator(new GeneratorReef(), 1);
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> e) {
    	e.getRegistry().registerAll(CORAL,REEF);
    }

    @SubscribeEvent
    public static void registerItem(RegistryEvent.Register<Item> e) {
	    //noinspection ConstantConditions
	    e.getRegistry().registerAll(new ItemBlockMeta(CORAL).setRegistryName(CORAL.getRegistryName()),
                                    new ItemBlockMeta(REEF).setRegistryName(REEF.getRegistryName()));
	    proxy.registerItems();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        OreDictionary.registerOre("dyeOrange", new ItemStack(CORAL, 1, 0));
        OreDictionary.registerOre("dyeMagenta", new ItemStack(CORAL, 1, 1));
        OreDictionary.registerOre("dyePink", new ItemStack(CORAL, 1, 2));
        OreDictionary.registerOre("dyeCyan", new ItemStack(CORAL, 1, 3));
        OreDictionary.registerOre("dyeGreen", new ItemStack(CORAL, 1, 4));
        OreDictionary.registerOre("dyeGray", new ItemStack(CORAL, 1, 5));
    }

    @SidedProxy(clientSide = "primetoxinz.coralreef.CoralReef$ClientProxy",
                serverSide = "primetoxinz.coralreef.CoralReef$CommonProxy")
    public static CommonProxy proxy;

    @SubscribeEvent
    public static void onConfigChange(ConfigChangedEvent event) {
        if (event.getModID().equals(MODID)) {
            ConfigManager.sync(MODID, Config.Type.INSTANCE);
        }
    }

    public static class CommonProxy {
        public void registerItems() {}
    }

    public static class ClientProxy extends CommonProxy {
        @Override
        public void registerItems() {
            ModelLoader.setCustomStateMapper(CORAL, new StateMap.Builder().ignore(BlockLiquid.LEVEL).build());
            for (int i = 0; i <= 5; i++) {
                registerItemModel(Item.getItemFromBlock(CORAL), i, "coralreef:coral"+i,"inventory");
            }
            for (int i = 0; i <= 1; i++) {
                registerItemModel(Item.getItemFromBlock(REEF), i, "coralreef:reef","types="+i);
            }
        }

        public void registerItemModel(Item item, int meta, String name, String type) {
            ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(name, type));
        }
    }
}
