package com.clownvin.everyonepoops;

import com.clownvin.everyonepoops.blocks.BlockPoopLayer;
import com.clownvin.everyonepoops.blocks.BlockPoopBlock;
import com.clownvin.everyonepoops.config.PoopConfig;
import com.clownvin.everyonepoops.entity.projectile.EntityPoop;
import com.clownvin.everyonepoops.items.ItemPoop;
import com.clownvin.everyonepoops.proxy.CommonProxy;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityPolarBear;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

@Mod(name = EveryonePoops.NAME, modid = EveryonePoops.MODID, version = EveryonePoops.VERSION)
@Mod.EventBusSubscriber(modid = EveryonePoops.MODID)
public class EveryonePoops {
    public static final String MODID = "everyonepoops";
    public static final String VERSION = "1.2.0";
    public static final String NAME = "Everyone Poops";

    public static Block BLOCK_POOP_BLOCK;
    public static Block BLOCK_POOP_LAYER;

    public static Item ITEM_POOP;
    public static Item ITEM_POOP_BLOCK;
    public static Item ITEM_POOP_LAYER;

    @Mod.Instance
    public static EveryonePoops instance;

    @SidedProxy(clientSide = "com.clownvin.everyonepoops.proxy.ClientProxy", serverSide = "com.clownvin.everyonepoops.proxy.ServerProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        BLOCK_POOP_BLOCK = new BlockPoopBlock();
        BLOCK_POOP_LAYER = new BlockPoopLayer();
        ITEM_POOP = new ItemPoop();
        ITEM_POOP_BLOCK = new ItemBlock(BLOCK_POOP_BLOCK).setCreativeTab(CreativeTabs.MATERIALS).setUnlocalizedName("poop_block").setRegistryName(MODID, "poop_block");
        ITEM_POOP_LAYER = new ItemBlock(BLOCK_POOP_LAYER).setCreativeTab(CreativeTabs.DECORATIONS).setUnlocalizedName("poop_layer").setRegistryName(MODID, "poop_layer");
        OreDictionary.registerOre("poop", ITEM_POOP);
        EntityRegistry.registerModEntity(new ResourceLocation(MODID, "poop"), EntityPoop.class, "poop", 0, instance, 100, 1, true);
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(BLOCK_POOP_BLOCK, BLOCK_POOP_LAYER);
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(ITEM_POOP, ITEM_POOP_BLOCK, ITEM_POOP_LAYER);
    }

    public static int getPoopRate(EntityLivingBase animal) {
        if (animal instanceof EntityPig)
            return 6000;
        if (animal instanceof EntityCow || animal instanceof EntityMooshroom || animal instanceof EntitySheep || animal instanceof EntityDonkey || animal instanceof EntityHorse || animal instanceof EntityMule)
            return 18000;
        if (animal instanceof EntityPolarBear || animal instanceof EntityLlama)
            return 36000;
        if (animal instanceof EntityWolf || animal instanceof EntityPlayer || animal instanceof EntityAnimal)
            return 24000;
        if (animal instanceof EntityOcelot)
            return 48000;
        if (animal instanceof EntityChicken || animal instanceof EntityParrot)
            return 72000;
        return 0;
    }

    @SubscribeEvent
    public static void entityUpdate(LivingEvent.LivingUpdateEvent event) {
        EntityLivingBase animal = event.getEntityLiving();
        if (animal.world.isRemote)
            return;
        if (animal instanceof EntityPlayer && !PoopConfig.playersPoop)
            return;
        int poopRate;
        if ((poopRate = (int) (getPoopRate(animal) * PoopConfig.globalPoopRateModifier)) == 0) // Animal doesn't poop
            return;
        if (Math.random() * poopRate > 1.0D) //Failed roll
            return;
        World world = animal.getEntityWorld();
        if (!BLOCK_POOP_LAYER.canPlaceBlockAt(world, animal.getPosition()) && !(world.getBlockState(animal.getPosition()).getBlock() instanceof BlockPoopLayer)) //Can't poop here
            return;
        //System.out.println(event.getEntityLiving().getDisplayName()+" is pooping...");
        if (world.getBlockState(animal.getPosition()).getBlock() instanceof BlockPoopLayer) {
            IBlockState state = world.getBlockState(animal.getPosition());
            if (state.getValue(BlockPoopLayer.LAYERS) >= 8) {
                world.setBlockState(animal.getPosition(), BLOCK_POOP_BLOCK.getDefaultState());
                BlockPos up = animal.getPosition().up();
                world.setBlockState(up, BLOCK_POOP_LAYER.getDefaultState());
                animal.setPositionAndUpdate(up.getX(), up.getY(), up.getZ());
                return;
            }
            world.setBlockState(animal.getPosition(), BLOCK_POOP_LAYER.getDefaultState().withProperty(BlockPoopLayer.LAYERS, world.getBlockState(animal.getPosition()).getValue(BlockPoopLayer.LAYERS) + 1));
            return;
        }
        world.setBlockState(animal.getPosition(), BLOCK_POOP_LAYER.getDefaultState());
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        ModelLoader.setCustomModelResourceLocation(ITEM_POOP, 0, new ModelResourceLocation(ITEM_POOP.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ITEM_POOP_BLOCK, 0, new ModelResourceLocation(ITEM_POOP_BLOCK.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ITEM_POOP_LAYER, 0, new ModelResourceLocation(ITEM_POOP_LAYER.getRegistryName(), "inventory"));
    }
}
