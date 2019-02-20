package com.clownvin.everyonepoops.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

public class PoopConfig {

    public static PoopConfig COMMON;
    public static ForgeConfigSpec COMMON_SPEC;

    public static void init() {
        Pair<PoopConfig, ForgeConfigSpec> commonPair = new ForgeConfigSpec.Builder().configure(PoopConfig::new);
        COMMON_SPEC = commonPair.getRight();
        COMMON = commonPair.getLeft();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, COMMON_SPEC);
    }

    //@Config.Name("Players Poop Too")
    //@Config.Comment("Whether or not players poop too.")
    public static ForgeConfigSpec.BooleanValue playersPoop;
    //@Config.Name("Global Poop Rate Modifier (Smaller Is Faster)")
    //@Config.Comment("Changes how often all animals poop.")
    //@Config.RangeDouble(min = 0, max = 1000)
    public static ForgeConfigSpec.DoubleValue globalPoopRateModifier;
    //@Config.Name("Global Poop Despawn Rate Modifier (Smaller Is Faster)")
    //@Config.Comment("Changes how fast poop despawns.")
    //@Config.RangeDouble(min = 0, max = 1000)
    public static ForgeConfigSpec.DoubleValue globalPoopDespawnRateModifier;
    //@Config.Name("Poop Causes Nausea")
    //@Config.Comment("Changes whether or not getting hit with thrown dung causes nausea.")
    public static ForgeConfigSpec.BooleanValue poopCausesNausea;

    public static ForgeConfigSpec.IntValue nauseaDuration;

    public PoopConfig(ForgeConfigSpec.Builder builder) {
        builder.push("General");
        playersPoop = builder.comment("Whether or not players poop too.")
                .translation("text.config.players_poop")
                .define("players_poop", false);
        poopCausesNausea = builder.comment("Whether or not getting hit with dung causes nausea.")
                .translation("text.config.poop_causes_nausea")
                .define("poop_causes_nausea", true);
        globalPoopRateModifier = builder.comment("How fast animals poop.\nSmaller is faster")
                .translation("text.config.global_poop_rate_modifier")
                .defineInRange("global_poop_rate_modifier", 1.0D, 0.0D, 1000.0D);
        globalPoopDespawnRateModifier = builder.comment("How fast dung decays.\nSmaller is faster")
                .translation("text.config.global_poop_despawn_rate_modifier")
                .defineInRange("global_poop_despawn_rate_modifier", 1.0D, 0.0D, 1000.0D);
        nauseaDuration = builder.comment("How long nausea from dung lasts, in ticks.")
                .translation("text.config.nausea_duration")
                .defineInRange("nausea_duration", 300, 0, 1000000);
    }


}
