package com.mklprudence.tcbs.tcbscontroller.setup;

import com.mklprudence.tcbs.tcbscontroller.TCBSController;
import com.mklprudence.tcbs.tcbscontroller.backend.Backend;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;

@Mod.EventBusSubscriber(modid = TCBSController.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.DEDICATED_SERVER)
public class ServerSetup {
    @SubscribeEvent
    public static void onServerSetup(final FMLDedicatedServerSetupEvent event) {
        // Some client setup code
        TCBSController.LOGGER.info("Hello from ServerSetup");
        event.enqueueWork(() -> {
            Backend.init();
        });
    }
}
