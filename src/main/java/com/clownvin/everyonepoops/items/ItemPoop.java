package com.clownvin.everyonepoops.items;

import com.clownvin.everyonepoops.EveryonePoops;
import com.clownvin.everyonepoops.entity.projectile.EntityPoop;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemPoop extends Item {

    public ItemPoop() {
        super(new Properties().maxStackSize(64).group(ItemGroup.MISC));
        this.setRegistryName(EveryonePoops.MODID, "poop");
    }

    @Override
    public EnumActionResult onItemUse(ItemUseContext context) {

        if (!context.getPlayer().canPlayerEdit(context.getPos().offset(context.getFace()), context.getFace(), context.getItem())) {
            return EnumActionResult.FAIL;
        } else if (applyBonemeal(context.getItem(), context.getWorld(), context.getPos(), context.getPlayer())) {
            if (!context.getWorld().isRemote) {
                context.getWorld().playEvent(2005, context.getPos(), 0);
            }
            return EnumActionResult.SUCCESS;
        }
        return EnumActionResult.PASS;
    }

    public static boolean applyBonemeal(ItemStack stack, World worldIn, BlockPos target, EntityPlayer player)
    {
        IBlockState iblockstate = worldIn.getBlockState(target);

        int hook = net.minecraftforge.event.ForgeEventFactory.onApplyBonemeal(player, worldIn, target, iblockstate, stack);
        if (hook != 0) return hook > 0;

        if (iblockstate.getBlock() instanceof IGrowable)
        {
            IGrowable igrowable = (IGrowable)iblockstate.getBlock();

            if (igrowable.canGrow(worldIn, target, iblockstate, worldIn.isRemote))
            {
                if (!worldIn.isRemote)
                {
                    if (igrowable.canUseBonemeal(worldIn, worldIn.rand, target, iblockstate))
                    {
                        igrowable.grow(worldIn, worldIn.rand, target, iblockstate);
                    }

                    stack.shrink(1);
                }

                return true;
            }
        }

        return false;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
    {
        ItemStack itemstack = playerIn.getHeldItem(handIn);

        if (!playerIn.abilities.isCreativeMode)
        {
            itemstack.shrink(1);
        }

        worldIn.playSound(null, playerIn.posX, playerIn.posY, playerIn.posZ, SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / ((float) Math.random() * 0.4F + 0.8F));

        if (!worldIn.isRemote)
        {
            EntityPoop entityPoop = new EntityPoop(worldIn, playerIn);
            entityPoop.shoot(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, 0.0F, 1.5F, 1.0F);
            playerIn.getEntityWorld().spawnEntity(entityPoop);
        }

        //playerIn.addStat(StatList.getObjectUseStats(this));
        return new ActionResult<>(EnumActionResult.SUCCESS, itemstack);
    }

}
