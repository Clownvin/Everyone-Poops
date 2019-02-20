package com.clownvin.everyonepoops;

import com.clownvin.everyonepoops.blocks.BlockPoopLayer;
import com.clownvin.everyonepoops.blocks.BlockPoopBlock;
import com.clownvin.everyonepoops.config.PoopConfig;
import com.clownvin.everyonepoops.entity.projectile.EntityPoop;
import com.clownvin.everyonepoops.items.ItemPoop;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityPolarBear;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(EveryonePoops.MODID)
@Mod.EventBusSubscriber(modid = EveryonePoops.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EveryonePoops {
    public static final String MODID = "everyonepoops";

    public static Block BLOCK_POOP_BLOCK;
    public static Block BLOCK_POOP_LAYER;

    public static Item ITEM_POOP;
    public static Item ITEM_POOP_BLOCK;
    public static Item ITEM_POOP_LAYER;

    public EveryonePoops() {
        init();
    }

    private void init() {
        PoopConfig.init();
        BLOCK_POOP_BLOCK = new BlockPoopBlock();
        BLOCK_POOP_LAYER = new BlockPoopLayer();
        ITEM_POOP = new ItemPoop();
        ITEM_POOP_BLOCK = new ItemBlock(BLOCK_POOP_BLOCK, new Item.Properties().group(ItemGroup.BUILDING_BLOCKS)).setRegistryName(MODID, "poop_block");
        ITEM_POOP_LAYER = new ItemBlock(BLOCK_POOP_LAYER, new Item.Properties().group(ItemGroup.DECORATIONS)).setRegistryName(MODID, "poop_layer");
        EntityPoop.init();
        //OreDictionary.registerOre("poop", ITEM_POOP);
        //TODO this
        //EntityRegistry.registerModEntity(new ResourceLocation(MODID, "poop"), EntityPoop.class, "poop", 0, instance, 100, 1, true);
        //proxy.preInit(event);
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    private static class Registers {
        @SubscribeEvent
        public static void registerBlocks(RegistryEvent.Register<Block> event) {
            event.getRegistry().registerAll(BLOCK_POOP_BLOCK, BLOCK_POOP_LAYER);
        }

        @SubscribeEvent
        public static void registerItems(RegistryEvent.Register<Item> event) {
            event.getRegistry().registerAll(ITEM_POOP, ITEM_POOP_BLOCK, ITEM_POOP_LAYER);
        }
    }

    public static int getPoopRate(EntityLivingBase animal) {
        if (animal instanceof EntityPig)
            return 6000;
        if (animal instanceof EntityCow || animal instanceof EntityMooshroom || animal instanceof EntitySheep || animal instanceof EntityDonkey || animal instanceof EntityHorse || animal instanceof EntityMule)
            return 18000;
        if (animal instanceof EntityPolarBear || animal instanceof EntityLlama)
            return 36000;
        if (animal instanceof EntityOcelot)
            return 48000;
        if (animal instanceof EntityChicken || animal instanceof EntityParrot)
            return 72000;
        if (animal instanceof EntityWolf || animal instanceof EntityPlayer || animal instanceof EntityAnimal)
            return 24000;
        return 0;
    }

    @SubscribeEvent
    public static void entityUpdate(LivingEvent.LivingUpdateEvent event) {
        EntityLivingBase animal = event.getEntityLiving();
        if (animal.world.isRemote)
            return;
        if (animal instanceof EntityPlayer && !PoopConfig.playersPoop.get())
            return;
        int poopRate;
        if ((poopRate = (int) (getPoopRate(animal) * PoopConfig.globalPoopRateModifier.get())) == 0) // Animal doesn't poop
            return;
        if (Math.random() * poopRate > 1.0D) //Failed roll
            return;
        World world = animal.getEntityWorld();
        Block whereAnimalStanding = world.getBlockState(animal.getPosition()).getBlock();
        if (whereAnimalStanding != Blocks.AIR && whereAnimalStanding != Blocks.TALL_GRASS && !(whereAnimalStanding instanceof BlockPoopLayer)) //Can't poop here
            return;
        BlockPos pos = animal.getPosition();
        IBlockState state = world.getBlockState(pos.down());
        while (state.getBlock() == Blocks.AIR || state.getBlock() == Blocks.TALL_GRASS) {
            pos = pos.down();
            state = world.getBlockState(pos.down());
        }
        //System.out.println(event.getEntityLiving().getDisplayName()+" is pooping...");
        if (world.getBlockState(pos).getBlock() instanceof BlockPoopLayer) {
            state = world.getBlockState(pos);
            if (state.get(BlockPoopLayer.LAYERS) >= 8) {
                world.setBlockState(pos, BLOCK_POOP_BLOCK.getDefaultState());
                BlockPos up = pos.up();
                if (world.getBlockState(up).getBlock() != Blocks.AIR || world.getBlockState(up).getBlock() != Blocks.TALL_GRASS)
                    return;
                world.setBlockState(up, BLOCK_POOP_LAYER.getDefaultState());
                //animal.setPositionAndUpdate(up.getX(), up.getY(), up.getZ());
                return;
            }
            world.setBlockState(pos, BLOCK_POOP_LAYER.getDefaultState().with(BlockPoopLayer.LAYERS, world.getBlockState(pos).get(BlockPoopLayer.LAYERS) + 1));
            return;
        }
        world.setBlockState(pos, BLOCK_POOP_LAYER.getDefaultState());
    }
}
