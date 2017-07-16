package primetoxinz.coralreef;

import com.google.common.collect.Sets;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Random;

/**
 * Created by primetoxinz on 7/16/17.
 */
public class GeneratorReef implements IWorldGenerator {

    public static final int OVERWORLD = 0;
    private final int MIN_HEIGHT = 10;
    private final int MAX_HEIGHT = 128;
    public static final int CHUNK_SIZE = 16;
    private WorldGenerator genReef, genReefRock;

    public GeneratorReef() {
        this.genReef = new WorldGenReef(CoralReef.reef.getDefaultState().withProperty(BlockReef.VARIANTS, 0), CoralReef.ConfigHandler.reefCount);
        this.genReefRock = new WorldGenRock(CoralReef.reef.getDefaultState().withProperty(BlockReef.VARIANTS, 1), 0);
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        if (world.provider.getDimension() == OVERWORLD) {
            this.run(this.genReef, world, random, chunkX * CHUNK_SIZE, chunkZ * CHUNK_SIZE, CoralReef.ConfigHandler.reefChance, MIN_HEIGHT, MAX_HEIGHT);
            this.run(this.genReefRock, world, random, chunkX * CHUNK_SIZE, chunkZ * CHUNK_SIZE, 1, MIN_HEIGHT, MAX_HEIGHT);
        }
    }

    private void run(WorldGenerator generator, World world, Random rand, int chunk_X, int chunk_Z, int chancesToSpawn, int minHeight, int maxHeight) {
        if (minHeight < 0 || maxHeight > 256 || minHeight > maxHeight)
            throw new IllegalArgumentException("Illegal Height Arguments for WorldGenerator");
        int heightDiff = maxHeight - minHeight;
        BlockPos pos;
        Biome biome;
        for (int i = 0; i < chancesToSpawn; i++) {
            int x = chunk_X + rand.nextInt(CHUNK_SIZE);
            int y = minHeight + rand.nextInt(heightDiff);
            int z = chunk_Z + rand.nextInt(CHUNK_SIZE);
            pos = new BlockPos(x, y, z);
            biome = world.getBiome(pos);

            if (BiomeDictionary.hasType(biome, CoralReef.ConfigHandler.onlyOcean ? BiomeDictionary.Type.OCEAN : BiomeDictionary.Type.WATER)) {
                generator.generate(world, rand, pos);
            }
        }
    }


}
