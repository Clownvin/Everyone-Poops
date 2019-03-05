package com.clownvin.everyonepoops;

import com.clownvin.everyonepoops.blocks.BlockPoopLayer;
import com.clownvin.everyonepoops.blocks.BlockPoopBlock;
import com.clownvin.everyonepoops.client.renderer.entity.RenderPoop;
import com.clownvin.everyonepoops.config.PoopConfig;
import com.clownvin.everyonepoops.entity.projectile.EntityPoop;
import com.clownvin.everyonepoops.items.ItemPoop;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityPolarBear;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.VersionChecker;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forgespi.language.IModInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(EveryonePoops.MODID)
@Mod.EventBusSubscriber(modid = EveryonePoops.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EveryonePoops {
    public static final String MODID = "everyonepoops";

    public static Block BLOCK_POOP_BLOCK;
    public static Block BLOCK_POOP_LAYER;

    public static Item ITEM_POOP;
    public static Item ITEM_POOP_BLOCK;
    public static Item ITEM_POOP_LAYER;

    public static final Logger LOGGER = LogManager.getLogger("Everyone Poops");

    public EveryonePoops() {
        init();
    }

    private void init() {
        DistExecutor.runWhenOn(Dist.CLIENT, () -> (() -> {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(RenderPoop::registerRender);
            MinecraftForge.EVENT_BUS.addListener(this::onJoinGame);
        }));
        PoopConfig.init();
        BLOCK_POOP_BLOCK = new BlockPoopBlock();
        BLOCK_POOP_LAYER = new BlockPoopLayer();
        ITEM_POOP = new ItemPoop();
        ITEM_POOP_BLOCK = new ItemBlock(BLOCK_POOP_BLOCK, new Item.Properties().group(ItemGroup.BUILDING_BLOCKS)).setRegistryName(MODID, "poop_block");
        ITEM_POOP_LAYER = new ItemBlock(BLOCK_POOP_LAYER, new Item.Properties().group(ItemGroup.DECORATIONS)).setRegistryName(MODID, "poop_layer");
        EntityPoop.init();
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

    private boolean isNewerVersion(String v1, String v2) {
        if (v1 == null || v2 == null) {
            LOGGER.warn("Can't compare versions: local: "+v1+", remote: "+v2);
            return false;
        }
        String[] v1s = v1.split("\\.");
        String[] v2s = v2.split("\\.");
        if (v2s.length > v1s.length)
            return true;
        //(v2s.length+", "+v1s.length);
        for (int i = 0; i < v2s.length; i++) {
            if (v2s[i].length() > v1s[i].length()) {
                return true;
            }
            if (v2s[i].compareTo(v1s[i]) > 0) {
                return true;
            }
        }
        return false;
    }

    public void onJoinGame(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent event) {
        if (!PoopConfig.showNewUpdateNotifications.get())
            return;
        LOGGER.info("Checking for update on join...");
        IModInfo info = ModList.get().getModContainerById(MODID).get().getModInfo();
        VersionChecker.CheckResult result = VersionChecker.getResult(info);
        if (result.target == null)
            return;
        LOGGER.info("Comparing versions "+info.getVersion().toString()+" and "+result.target.toString());
        if (!isNewerVersion(info.getVersion().toString(), result.target.toString())) {//result.target.compareTo(Loader.instance().activeModContainer().getVersion()) <= 0) {
            return;
        }
        LOGGER.info("Update available for Everyone Poops");
        event.getPlayer().sendMessage(new TextComponentTranslation("text.new_update_notification", "Everyone Poops, version "+result.target.toString()));
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
