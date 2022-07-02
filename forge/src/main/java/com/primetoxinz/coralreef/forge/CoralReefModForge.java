package com.primetoxinz.coralreef.forge;

import com.primetoxinz.coralreef.*;
import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.common.*;
import net.minecraftforge.eventbus.api.*;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.*;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(CoralReef.MOD_ID)
public class CoralReefModForge {
    public CoralReefModForge() {
        // Submit our event bus to let architectury register our content on the right time
        IEventBus MOD_BUS = FMLJavaModLoadingContext.get().getModEventBus();
        EventBuses.registerModEventBus(CoralReef.MOD_ID, MOD_BUS);
        CoralReef.init();
        MOD_BUS.addListener(this::commonSetup);
        MOD_BUS.addListener(this::clientSetup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(CoralReef::postInit);
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(CoralReefClient::init);
    }
}
