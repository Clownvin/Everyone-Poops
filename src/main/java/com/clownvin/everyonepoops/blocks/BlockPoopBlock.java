package com.clownvin.everyonepoops.blocks;

import com.clownvin.everyonepoops.EveryonePoops;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

import java.util.Random;

public class BlockPoopBlock extends Block {

    public BlockPoopBlock() {
        super(Properties.create(Material.CAKE).sound(SoundType.SLIME).hardnessAndResistance(0.5f, 1.0f));
        this.setRegistryName(EveryonePoops.MODID, "poop_block");
    }

    @Override
    public int getHarvestLevel(IBlockState state) {
        return 0;
    }

    @Override
    public ToolType getHarvestTool(IBlockState state) {
        return ToolType.SHOVEL;
    }

    public Item getItemDropped(IBlockState state, World world, BlockPos pos, int fortune)
    {
        return EveryonePoops.ITEM_POOP;
    }

    public int getItemsToDropCount(IBlockState state, int i, World world, BlockPos pos, Random random)
    {
        return 8;
    }
}
