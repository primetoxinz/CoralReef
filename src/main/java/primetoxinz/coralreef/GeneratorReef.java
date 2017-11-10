package primetoxinz.coralreef;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.NoiseGeneratorOctaves;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Random;

/**
 * Created by primetoxinz on 7/16/17.
 */
public class GeneratorReef implements IWorldGenerator {

	protected static final NoiseGeneratorOctaves CORAL_REEF_NOISE = new NoiseGeneratorOctaves(new Random(3364), 1);

	public static final int OVERWORLD = 0;
	public static final int CHUNK_SIZE = 16;

	private WorldGenerator genReef, genReefRock;
	private double[] noise = new double[256];

	public GeneratorReef() {
		this.genReef = new WorldGenReef(CoralReef.REEF.getDefaultState().withProperty(BlockReef.TYPES, 0));
		this.genReefRock = new WorldGenRock(CoralReef.REEF.getDefaultState().withProperty(BlockReef.TYPES, 1));
	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
		if (world.provider.getDimension() == OVERWORLD) {
			reef(world, random, chunkX, chunkZ);
			rock(world, random, chunkX, chunkZ);
		}
	}

	private void rock(World world, Random rand, int chunkX, int chunkZ) {
		int x = (chunkX * CHUNK_SIZE) + 8 + rand.nextInt(16), z = (chunkZ * CHUNK_SIZE) + 8 + rand.nextInt(16);
		BlockPos pos = getTop(world, x, z);
		this.genReefRock.generate(world, rand, pos.up());
	}

	private void reef(World world, Random rand, int chunkX, int chunkZ) {
		noise = CORAL_REEF_NOISE.generateNoiseOctaves(noise, chunkX * CHUNK_SIZE, 0, chunkZ * CHUNK_SIZE, 16, 1, 16, 0.03125D, 0.03125D, 1.0D);
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				double d = this.noise[x + z * CHUNK_SIZE] + rand.nextDouble() * 0.2D;
				if (d > 0)
					genReef.generate(world, rand, getTop(world,(chunkX * CHUNK_SIZE) + x + 8,  (chunkZ * CHUNK_SIZE) + z + 8));
			}
		}
	}

	public static BlockPos getTop(World world, int x, int z) {
		Chunk chunk = world.getChunkFromBlockCoords(new BlockPos(x, 0, z));
		BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos(x, chunk.getTopFilledSegment(), z);
		for (; blockPos.getY() >= 0; blockPos.setY(blockPos.getY() - 1)) {
			IBlockState state = chunk.getBlockState(blockPos);
			if (state.getMaterial().isSolid() && !state.getBlock().isLeaves(state, world, blockPos) && !state.getBlock().isFoliage(world, blockPos)) {
				break;
			}
		}
		return blockPos;
	}

}