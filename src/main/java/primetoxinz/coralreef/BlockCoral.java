package primetoxinz.coralreef;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Random;

import static net.minecraft.block.BlockLiquid.LEVEL;
import static net.minecraft.util.EnumFacing.WEST;

/**
 * Created by tyler on 8/17/16.
 */
public class BlockCoral extends Block implements IPlantable {
    public static final EnumPlantType CORAL = EnumPlantType.getPlantType("Coral");
    public static final PropertyInteger VARIANTS = PropertyInteger.create("variants", 0, 5);
    protected static final AxisAlignedBB CORAL_AABB = new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 1.0D, 0.875D);

    public BlockCoral() {
        super(Material.WATER);
        setTickRandomly(true);
        setCreativeTab(CreativeTabs.MISC);
        setHardness(0.0F);
        setSoundType(SoundType.PLANT);
        setUnlocalizedName("coral");
        setRegistryName("coral");
        GameRegistry.register(this);
        GameRegistry.register(new ItemBlockMeta(this), getRegistryName());
        setDefaultState(getDefaultState().withProperty(VARIANTS, 0).withProperty(BlockLiquid.LEVEL, 15));
    }

    public boolean placeAt(World world, BlockPos bottom) {
        boolean placed = false;
        if (canPlaceBlockAt(world, bottom)) {
            int variant = world.rand.nextInt(6);
            if (variant > 3) {
                int height = world.rand.nextInt(4);
                for (int i = 0; i < height; i++) {
                    BlockPos next = bottom.up(i);
                    if (world.getBlockState(next.up()).getMaterial() == Material.WATER)
                        placed = world.setBlockState(bottom.up(i), getDefaultState().withProperty(VARIANTS, variant), 3);
                }
            } else {
                placed = world.setBlockState(bottom, getDefaultState().withProperty(VARIANTS, variant), 3);
            }
        }
        return placed;
    }


    @Override
    public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos) {
        return false;
    }

    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> list) {
        for (int i = 0; i <= 5; ++i) {
            list.add(new ItemStack(this, 1, i));
        }
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        return new ItemStack(this, 1, state.getValue(VARIANTS));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[]{LEVEL, VARIANTS});
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(VARIANTS, meta);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(VARIANTS);
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return Block.NULL_AABB;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return CORAL_AABB;
    }


    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        this.checkAndDropBlock(worldIn, pos, state);
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        this.checkAndDropBlock(worldIn, pos, state);
    }

    @Override
    public int damageDropped(IBlockState state) {
        return state.getValue(VARIANTS);
    }

    protected boolean checkAndDropBlock(World worldIn, BlockPos pos, IBlockState state) {
        if (this.canBlockStay(worldIn, pos, state)) {
            return true;
        } else {
            this.dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
            return false;
        }
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        IBlockState state = worldIn.getBlockState(pos.down());
        Block block = state.getBlock();

        if (worldIn.getBlockState(pos.up(2)).getMaterial() != Material.WATER) return false;
        if (block.canSustainPlant(state, worldIn, pos.down(), EnumFacing.UP, this)) return true;
        if (block == this) {
            int variant = state.getValue(VARIANTS);
            return variant > 3;
        }
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(IBlockState stateIn, World world, BlockPos pos, Random rand) {
        if (CoralReef.ConfigHandler.bubbles && world.getBlockState(pos.up()).getMaterial() == Material.WATER) {
            double offset = 0.0625D;
            for (int i = 0; i < 6; i++) {
                double x1 = (pos.getX() + rand.nextDouble());
                double y1 = (pos.getY() + rand.nextDouble());
                double z1 = (pos.getZ() + rand.nextDouble());
                if (i == 0 && !world.getBlockState(pos.up()).isBlockNormalCube()) {
                    y1 = (double) (pos.getY() + 1) + offset;
                }

                if (i == 1 && !world.getBlockState(pos.down()).isBlockNormalCube()) {
                    y1 = (double) (pos.getY() + 0) - offset;
                }

                if (i == 2 && !world.getBlockState(pos.offset(EnumFacing.SOUTH)).isBlockNormalCube()) {
                    z1 = (double) (pos.getZ() + 1) + offset;
                }

                if (i == 3 && !world.getBlockState(pos.offset(EnumFacing.NORTH)).isBlockNormalCube()) {
                    z1 = (double) (pos.getZ() + 0) - offset;
                }

                if (i == 4 && !world.getBlockState(pos.offset(EnumFacing.EAST)).isBlockNormalCube()) {
                    x1 = (double) (pos.getX() + 1) + offset;
                }

                if (i == 5 && !world.getBlockState(pos.offset(WEST)).isBlockNormalCube()) {
                    x1 = (double) (pos.getX() + 0) - offset;
                }

                if (x1 < (double) pos.getX() || x1 > (double) (pos.getY() + 1) || y1 < 0.0D || y1 > (double) (pos.getY() + 1) || z1 < (double) pos.getZ() || z1 > (double) (pos.getZ() + 1)) {
                    world.spawnParticle(EnumParticleTypes.WATER_BUBBLE, x1, y1, z1, 0, 0, 0);
                }
            }
        }
    }


    public boolean canBlockStay(World world, BlockPos pos, IBlockState state) {
        return canPlaceBlockAt(world, pos);
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }


    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }


    @SideOnly(Side.CLIENT)
    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        return CoralReef.ConfigHandler.coralLightLevel;
    }

    @Override
    public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
        return CORAL;
    }

    @Override
    public IBlockState getPlant(IBlockAccess world, BlockPos pos) {
        return world.getBlockState(pos);
    }


}
