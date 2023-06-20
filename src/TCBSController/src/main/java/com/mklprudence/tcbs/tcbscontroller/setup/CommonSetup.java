package com.mklprudence.tcbs.tcbscontroller.setup;

import com.mklprudence.tcbs.tcbscontroller.TCBSController;
import com.mklprudence.tcbs.tcbscontroller.backend.Backend;
import com.mklprudence.tcbs.tcbscontroller.data.Data;
import com.mklprudence.tcbs.tcbscontroller.network.Networking;
import com.mklprudence.tcbs.tcbscontroller.playerdata.PlayerDataEvents;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = TCBSController.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = {Dist.CLIENT, Dist.DEDICATED_SERVER})
public class CommonSetup {
    public static void setup() {
        IEventBus bus = MinecraftForge.EVENT_BUS;
        bus.addGenericListener(Entity.class, PlayerDataEvents::onAttachCapabilitiesPlayer);
        bus.addListener(PlayerDataEvents::onPlayerCloned);
        bus.addListener(PlayerDataEvents::onRegisterCapabilities);
    }

    @SubscribeEvent
    public static void init(final FMLCommonSetupEvent event) {
        TCBSController.LOGGER.info("Hello from INIT");
        Networking.register();
    }
}
