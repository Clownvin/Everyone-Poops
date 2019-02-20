package com.clownvin.everyonepoops.blocks;

import com.clownvin.everyonepoops.EveryonePoops;
import com.clownvin.everyonepoops.config.PoopConfig;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.stats.StatList;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.*;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockPoopLayer extends Block {
    public static final IntegerProperty LAYERS = BlockStateProperties.LAYERS_1_8;
    protected static final VoxelShape[] SHAPES = new VoxelShape[]{VoxelShapes.empty(), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 10.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)};

    public BlockPoopLayer() {
        super(Properties.create(Material.CAKE).needsRandomTick().sound(SoundType.SLIME).hardnessAndResistance(0.5f, 1.0f));
        this.setDefaultState(this.stateContainer.getBaseState().with(LAYERS, Integer.valueOf(1)));
        this.setRegistryName(EveryonePoops.MODID, "poop_layer");
    }

    public boolean allowsMovement(IBlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
        switch(type) {
            case LAND:
                return state.get(LAYERS) < 5;
            case WATER:
                return false;
            case AIR:
                return false;
            default:
                return false;
        }
    }

    /**
     * @deprecated call via {@link IBlockState#isFullCube()} whenever possible. Implementing/overriding is fine.
     */
    public boolean isFullCube(IBlockState state) {
        return state.get(LAYERS) == 8;
    }

    /**
     * Get the geometry of the queried face at the given position and state. This is used to decide whether things like
     * buttons are allowed to be placed on the face, or how glass panes connect to the face, among other things.
     * <p>
     * Common values are {@code SOLID}, which is the default, and {@code UNDEFINED}, which represents something that does
     * not fit the other descriptions and will generally cause other things not to connect to the face.
     *
     * @return an approximation of the form of the given face
     * @deprecated call via ??? deprecated whenever possible.
     * Implementing/overriding is fine.
     */
    public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return face == EnumFacing.DOWN ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
    }

    public VoxelShape getShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
        return SHAPES[state.get(LAYERS)];
    }

    public VoxelShape getCollisionShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
        return SHAPES[state.get(LAYERS) - 1];
    }

    public boolean isValidPosition(IBlockState state, IWorldReaderBase worldIn, BlockPos pos) {
        IBlockState iblockstate = worldIn.getBlockState(pos.down());
        Block block = iblockstate.getBlock();
        if (block != Blocks.ICE && block != Blocks.PACKED_ICE && block != Blocks.BARRIER) {
            BlockFaceShape blockfaceshape = iblockstate.getBlockFaceShape(worldIn, pos.down(), EnumFacing.UP);
            return blockfaceshape == BlockFaceShape.SOLID || iblockstate.isIn(BlockTags.LEAVES) || block == this && iblockstate.get(LAYERS) == 8;
        } else {
            return false;
        }
    }

    /**
     * Update the provided state given the provided neighbor facing and neighbor state, returning a new state.
     * For example, fences make their connections to the passed in state if possible, and wet concrete powder immediately
     * returns its solidified counterpart.
     * Note that this method should ideally consider only the specific face passed in.
     *
     * @param facingState The state that is currently at the position offset of the provided face to the stateIn at
     * currentPos
     */
    public IBlockState updatePostPlacement(IBlockState stateIn, EnumFacing facing, IBlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        return !stateIn.isValidPosition(worldIn, currentPos) ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    /**
     * Spawns the block's drops in the world. By the time this is called the Block has possibly been set to air via
     * Block.removedByPlayer
     */
    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack) {
        Integer integer = state.get(LAYERS);
        net.minecraft.util.NonNullList<ItemStack> items = net.minecraft.util.NonNullList.create();
        int fortune = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, stack);
        float chance = 1.0f;

        if (this.canSilkHarvest(state, worldIn, pos, player) && EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, stack) > 0) {
            if (integer == 8) {
                items.add(new ItemStack(EveryonePoops.BLOCK_POOP_BLOCK));
            } else {
                for(int i = 0; i < integer; ++i) {
                    items.add(this.getSilkTouchDrop(state));
                }
            }
            chance = net.minecraftforge.event.ForgeEventFactory.fireBlockHarvesting(items, worldIn, pos, state, 0, 1.0f, true, player);
        } else {
            getDrops(state, items, worldIn, pos, fortune);
            chance = net.minecraftforge.event.ForgeEventFactory.fireBlockHarvesting(items, worldIn, pos, state, fortune, 1.0f, false, player);
        }

        for (ItemStack item : items) {
            if (worldIn.rand.nextFloat() <= chance)
                spawnAsEntity(worldIn, pos, item);
        }

        worldIn.removeBlock(pos);
        player.addStat(StatList.BLOCK_MINED.get(this));
        player.addExhaustion(0.005F);
    }

    @Override
    public IItemProvider getItemDropped(IBlockState state, World worldIn, BlockPos pos, int fortune) {
        return EveryonePoops.ITEM_POOP;
    }

    @Override
    public int getItemsToDropCount(IBlockState state, int i, World world, BlockPos pos, Random random)
    {
        return state.get(LAYERS) + 1;
    }

    @Override
    public int quantityDropped(IBlockState state, Random random) {
        return state.get(LAYERS) + 1;
    }

    public void randomTick(IBlockState state, World worldIn, BlockPos pos, Random random) {
        //System.out.println("Ticking....");
        if (worldIn.isRemote)
            return;
        int chance = (int) (9 * PoopConfig.globalPoopDespawnRateModifier.get());
        if (worldIn.isRainingAt(pos.up()))
            chance /= 3;
        if (Math.random() * chance > 1.0D) //Failed roll
            return;
        //System.out.println("REMOVING");
        if (state.get(LAYERS).intValue() <= 1)
            worldIn.removeBlock(pos);
        else
            worldIn.setBlockState(pos, getDefaultState().with(LAYERS, state.get(LAYERS).intValue() - 1));
    }

    public boolean isReplaceable(IBlockState state, BlockItemUseContext useContext) {
        int i = state.get(LAYERS);
        if (useContext.getItem().getItem() == this.asItem() && i < 8) {
            if (useContext.replacingClickedOnBlock()) {
                return useContext.getFace() == EnumFacing.UP;
            } else {
                return true;
            }
        } else {
            return i == 1;
        }
    }

    @Nullable
    public IBlockState getStateForPlacement(BlockItemUseContext context) {
        IBlockState iblockstate = context.getWorld().getBlockState(context.getPos());
        if (iblockstate.getBlock() == this) {
            int i = iblockstate.get(LAYERS);
            return iblockstate.with(LAYERS, Integer.valueOf(Math.min(8, i + 1)));
        } else {
            return super.getStateForPlacement(context);
        }
        //mthis.updatePostPlacement()
    }

    protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
        builder.add(LAYERS);
    }

    protected boolean canSilkHarvest() {
        return true;
    }
}
/*
    public static final PropertyInteger LAYERS = PropertyInteger.create("layers", 1, 8);
    protected static final AxisAlignedBB[] POOP_AABB = new AxisAlignedBB[] {new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.0D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.125D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.25D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.375D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.625D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.75D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.875D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D)};

    public BlockPoopLayer() {
        super(Material.CAKE);
        this.setSoundType(SoundType.SLIME);
        this.setUnlocalizedName("poop_layer");
        this.setRegistryName(EveryonePoops.MODID, "poop_layer");
        this.setDefaultState(this.blockState.getBaseState().withProperty(LAYERS, Integer.valueOf(1)));
        this.setTickRandomly(true);
        this.setCreativeTab(CreativeTabs.DECORATIONS);
        this.setHardness(0.5f);
        this.setResistance(1.0f);
        setHarvestLevel("shovel", 0);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return POOP_AABB[(state.getValue(LAYERS)).intValue()];
    }

    @Override
    public boolean isPassable(IBlockAccess worldIn, BlockPos pos)
    {
        return (worldIn.getBlockState(pos).getValue(LAYERS)).intValue() < 7;
    }

    @Override
    public boolean isTopSolid(IBlockState state)
    {
        return (state.getValue(LAYERS)).intValue() == 8;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face)
    {
        return face == EnumFacing.DOWN ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
    }

    @Override
    @Nullable
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
    {
        int i = ((Integer)blockState.getValue(LAYERS)).intValue() - 1;
        float f = 0.125F;
        AxisAlignedBB axisalignedbb = blockState.getBoundingBox(worldIn, pos);
        return new AxisAlignedBB(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ, axisalignedbb.maxX, (double)((float)i * 0.125F), axisalignedbb.maxZ);
    }

    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
        return (worldIn.getBlockState(pos).getBlock() instanceof BlockAir || worldIn.getBlockState(pos).getBlock() instanceof  BlockTallGrass) && worldIn.getBlockState(pos.down()).isNormalCube();//.isNormalCube(worldIn, pos.getX(), pos.getY(), pos.getZ());//!(worldIn.getBlockState(pos.down()).getBlock() instanceof BlockAir) && (worldIn.getBlockState(pos.down()).isFullBlock() || worldIn.getBlockState(pos.down()).getBlock() instanceof BlockTallGrass);
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        if (!canPlaceBlockAt(worldIn, pos))
            worldIn.setBlockToAir(pos);
    }

    @Override
    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack)
    {
        super.harvestBlock(worldIn, player, pos, state, te, stack);
        worldIn.setBlockToAir(pos);
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return EveryonePoops.ITEM_POOP;
    }

    @Override
    public int quantityDropped(Random random)
    {
        return 1;
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        if (worldIn.isRemote)
            return;
        int chance = (int) (9 * PoopConfig.globalPoopDespawnRateModifier);
        if (worldIn.isRainingAt(pos.up()))
            chance /= 3;
        if (Math.random() * chance > 1.0D) //Failed roll
            return;
        if (state.getValue(LAYERS).intValue() <= 1)
            worldIn.setBlockToAir(pos);
        else
            worldIn.setBlockState(pos, getDefaultState().withProperty(LAYERS, state.getValue(LAYERS).intValue() - 1));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
    {
        if (side == EnumFacing.UP)
        {
            return true;
        }
        else
        {
            IBlockState iblockstate = blockAccess.getBlockState(pos.offset(side));
            return iblockstate.getBlock() == this && (iblockstate.getValue(LAYERS)).intValue() >= (blockState.getValue(LAYERS)).intValue() ? false : super.shouldSideBeRendered(blockState, blockAccess, pos, side);
        }
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(LAYERS, (meta & 7) + 1);
    }

    @Override
    public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos) {
        return (worldIn.getBlockState(pos).getValue(LAYERS)).intValue() == 1;
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return (state.getValue(LAYERS)).intValue() - 1;
    }

    @Override
    public int quantityDropped(IBlockState state, int fortune, Random random) {
        return (state.getValue(LAYERS)) + (fortune > 0 ? random.nextInt(fortune) : 0);
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {LAYERS});
    }
}
*/