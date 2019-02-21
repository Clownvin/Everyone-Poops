package com.clownvin.everyonepoops.config;

import com.clownvin.everyonepoops.EveryonePoops;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = EveryonePoops.MODID)
@Config.LangKey("everyonepoops.config.title")
public class PoopConfig {

    @Mod.EventBusSubscriber(modid = EveryonePoops.MODID)
    private static class EventHandler {
        @SubscribeEvent
        public static void onConfigChanged(final ConfigChangedEvent.OnConfigChangedEvent event) {
            if (event.getModID().equals(EveryonePoops.MODID))
                ConfigManager.sync(EveryonePoops.MODID, Config.Type.INSTANCE);
        }
    }
    @Config.Name("Players Poop Too")
    @Config.Comment("Whether or not players poop too.")
    public static boolean playersPoop = false;
    @Config.Name("Global Poop Rate Modifier (Smaller Is Faster)")
    @Config.Comment("Changes how often all animals poop.")
    @Config.RangeDouble(min = 0, max = 1000)
    public static float globalPoopRateModifier = 1.0f;
    @Config.Name("Global Poop Despawn Rate Modifier (Smaller Is Faster)")
    @Config.Comment("Changes how fast poop despawns.")
    @Config.RangeDouble(min = 0, max = 1000)
    public static float globalPoopDespawnRateModifier = 1.0f;
    @Config.Name("Poop Causes Nausea")
    @Config.Comment("Changes whether or not getting hit with thrown dung causes nausea.")
    public static boolean poopCausesNausea = true;
    @Config.Name("Show Ingame Update Notifications")
    @Config.Comment("Changes whether or not the mod will alert you ingame to new updates for your version.")
    public static boolean showUpdateNotifications = true;
}
