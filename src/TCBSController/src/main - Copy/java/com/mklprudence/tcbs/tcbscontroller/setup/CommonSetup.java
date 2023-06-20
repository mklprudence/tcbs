package com.mklprudence.tcbs.tcbscontroller.setup;

import com.mklprudence.tcbs.tcbscontroller.TCBSController;
import com.mklprudence.tcbs.tcbscontroller.network.Networking;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = TCBSController.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = {Dist.CLIENT, Dist.DEDICATED_SERVER})
public class CommonSetup {
    @SubscribeEvent
    public static void init(final FMLCommonSetupEvent event) {
        TCBSController.LOGGER.info("Hello from INIT");
        Networking.register();
    }
}
