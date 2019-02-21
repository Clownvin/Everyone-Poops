package com.clownvin.everyonepoops.proxy;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.oredict.OreDictionary;

import static com.clownvin.everyonepoops.EveryonePoops.ITEM_POOP;

public abstract class CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {

    }

    public void init(FMLInitializationEvent event) {
        //Crafting
    }

    public void postInit(FMLPostInitializationEvent event) {
        OreDictionary.registerOre("poop", ITEM_POOP);
    }
}