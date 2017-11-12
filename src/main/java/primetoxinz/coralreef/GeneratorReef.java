package primetoxinz.coralreef;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.NoiseGeneratorOctaves;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Random;

/**
 * Created by primetoxinz on 7/16/17.
 */
public class GeneratorReef implements IWorldGenerator {

    protected static final int OCTAVES = 2;
    protected static final double NOISE_SCALE = 0.03125D;

    protected static final NoiseGeneratorOctaves CORAL_REEF_NOISE = new NoiseGeneratorOctaves(new Random(3364), OCTAVES);

    public static final int CHUNK_SIZE = 16;

    private WorldGenerator genReef, genReefRock;
    private double[] noise = new double[CHUNK_SIZE * CHUNK_SIZE];

    public GeneratorReef() {
        this.genReef = new WorldGenReef(CoralReef.REEF.getDefaultState().withProperty(BlockReef.TYPES, 0));
        this.genReefRock = new WorldGenRock(CoralReef.REEF.getDefaultState().withProperty(BlockReef.TYPES, 1));
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        if (ArrayUtils.isEmpty(CoralReef.ConfigHandler.dimensions) || ArrayUtils.contains(CoralReef.ConfigHandler.dimensions, world.provider.getDimension())) {
            reef(world, random, chunkX, chunkZ);
            if (random.nextDouble() <= CoralReef.ConfigHandler.rock.chance)
                rock(world, random, chunkX, chunkZ);
        }
    }

    private void rock(World world, Random rand, int chunkX, int chunkZ) {
        int x = (chunkX * CHUNK_SIZE) + 8 + rand.nextInt(16);
        int z = (chunkZ * CHUNK_SIZE) + 8 + rand.nextInt(16);
        BlockPos pos = getTop(world, x, z);
        this.genReefRock.generate(world, rand, pos.up());
    }

    // figure out for current chunk of noise whether it should spawn a reef or not
    private boolean reefFromNoise(int x, int y, int z, Random rand) {
        double d = this.noise[x * CHUNK_SIZE + z] / OCTAVES; // : (-1,1)

        // tune based on height; at sea level probability should be higher,
        // since that's where corals like to live, but then probability drops
        // until reducing to zero at depth 50 below sea level
        int seaLevel = 64;
        double ybelow = (double) Math.max(0, seaLevel - y);

        // exponential decay, fast falloff (0 change at depth 50, 0.25 chance at depth 35, 4x chance at depth 0)
        d *= 0.0001810*ybelow*ybelow - 0.1705*ybelow + 4;

        d -= rand.nextDouble() * 0.1; // add a bit of fine noise to blur edges
        d = Math.pow(d, 5); // isolate outliers (magnitude < 0.5 tends to 0 as power increases)
        return (d > 0.005); // cut away what ended up near 0, creating "islands" of reef
    }

    private void reef(World world, Random rand, int chunkX, int chunkZ) {
        noise = CORAL_REEF_NOISE.generateNoiseOctaves(noise,
                chunkX * CHUNK_SIZE, 0, chunkZ * CHUNK_SIZE,    // noise offset (for matching edges)
                CHUNK_SIZE, 1, CHUNK_SIZE,                      // array size
                NOISE_SCALE, 1.0, NOISE_SCALE);                 // noise scale
        for (int x = 0; x < CHUNK_SIZE; x++) {
            for (int z = 0; z < CHUNK_SIZE; z++) {
                BlockPos pos = getTop(world, (chunkX * CHUNK_SIZE) + x + 8, (chunkZ * CHUNK_SIZE) + z + 8);
                if (reefFromNoise(x, pos.getY(), z, rand)) {
                    if (ArrayUtils.isEmpty(CoralReef.ConfigHandler.biomes) || ArrayUtils.contains(CoralReef.ConfigHandler.biomes, world.getBiome(pos).getRegistryName().getResourcePath().toLowerCase()))
                        genReef.generate(world, rand, pos);
                }
            }
        }
    }

    public static BlockPos getTop(World world, int x, int z) {

        Chunk chunk = world.getChunkFromBlockCoords(new BlockPos(x, 0, z));
        BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos(world.getTopSolidOrLiquidBlock(
                new BlockPos(x, 0, z)));

        // proceed as per getTopSolidOrLiquidBlock, but additionally walk through ice
        for (; blockPos.getY() >= 0; blockPos.setY(blockPos.getY() - 1)) {
            IBlockState state = chunk.getBlockState(blockPos);
            if (state.getMaterial().blocksMovement()
                    && state.getMaterial() != Material.ICE
                    && state.getMaterial() != Material.LEAVES
                    && !state.getBlock().isFoliage(world, blockPos)) {
                break;
            }
        }
        return blockPos;
    }
}
