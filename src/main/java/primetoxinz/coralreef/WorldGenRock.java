package primetoxinz.coralreef;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Random;

/**
 * Created by primetoxinz on 7/16/17.
 */
public class WorldGenRock extends WorldGenerator {
    private final IBlockState state;

    public WorldGenRock(IBlockState state) {
        super(false);
        this.state = state;
    }

    // this generates larger coral block structures on top of existing coral

    public boolean generate(World worldIn, Random rand, BlockPos position) {
        for (; position.getY() > 3; position = position.down()) {
            // search for a reef going downwards to Y = 3 (bedrock start?)

            if (worldIn.isAirBlock(position.down()) || worldIn.isAirBlock(position))
                continue;

            Block block = worldIn.getBlockState(position.down()).getBlock();
            if (block != CoralReef.REEF) continue;

            int b = rand.nextInt(2); // : {0,1} // smaller or larger box

            // do a few rounds of random placement around position
            for (int n = 0; n < 3; ++n) {
                int i = b + rand.nextInt(2); // : {0,1,2}
                int j = b + rand.nextInt(2);
                int k = b + rand.nextInt(2);
                double f = (double) (i + j + k) / 3 + 0.5; // avg, rounded up?

                // do not place box if it will center will touch air (hack, arguably should be any part of box)
                if (worldIn.getBlockState(position.up(j+1)).getMaterial() != Material.WATER) {
                    continue;
                }

                for (BlockPos bp : BlockPos.getAllInBox(position.add(-i, -j, -k), position.add(i, j, k))) {
                    if (bp.distanceSq(position) <= f * f) {
                        worldIn.setBlockState(bp, this.state);
                    }
                }

                // 2+b*2 : {2,4}
                // rand(2+b*2) : {0,1} or {0,1,2,3}
                // rand(2+b*2) - 1 : {-1,0} or {-1,0,1,2}
                // rand(2+b*2) - 1 - b : {-2,-1,0} or {-2,-1,0,1,2}
                int xzstep = rand.nextInt(2 + b * 2) - b - 1;

                position = position.add(xzstep, -rand.nextInt(2), xzstep);
            }

            return true;
        }
        return false;
    }
}
