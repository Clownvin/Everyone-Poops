package com.clownvin.everyonepoops.blocks;

import com.clownvin.everyonepoops.EveryonePoops;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

import java.util.Random;

public class BlockPoopBlock extends Block {
    public BlockPoopBlock() {
        super(Material.CAKE);
        this.setSoundType(SoundType.SLIME);
        this.setUnlocalizedName("poop_block");
        this.setRegistryName(EveryonePoops.MODID, "poop_block");
        this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
        this.setHardness(0.5f);
        this.setResistance(1.0f);
        setHarvestLevel("shovel", 0);
    }

    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return EveryonePoops.ITEM_POOP;
    }

    public int quantityDropped(Random random)
    {
        return 8;
    }
}
