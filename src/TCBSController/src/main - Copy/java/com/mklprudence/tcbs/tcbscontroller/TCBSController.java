package com.mklprudence.tcbs.tcbscontroller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mklprudence.tcbs.tcbscontroller.backend.Backend;
import com.mklprudence.tcbs.tcbscontroller.setup.Config;
import com.mklprudence.tcbs.tcbscontroller.setup.Registration;
import com.mojang.logging.LogUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(TCBSController.MODID)
public class TCBSController {
    public static final String MODID = "tcbscontroller";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final Gson GSON = new Gson(), GSON_PRETTY = new GsonBuilder().setPrettyPrinting().create();

    public TCBSController() {
        MinecraftForge.EVENT_BUS.register(this);
        Registration.init();
        Config.register();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerTabs);
    }

    /*
    private void addCreative(CreativeModeTabEvent.BuildContents event) {
        if (event.getTab() == CreativeModeTabs.BUILDING_BLOCKS)
            //event.accept(EXAMPLE_BLOCK_ITEM);
            ITEMS.getEntries().forEach((e) -> event.accept(e.get()));
    }
     */
    @SubscribeEvent
    public void registerTabs(CreativeModeTabEvent.Register event) {
        CreativeModeTab tab = event.registerCreativeModeTab(new ResourceLocation(TCBSController.MODID), builder -> builder
                .title(Component.translatable("TCBS Controller"))
                .icon(() -> new ItemStack(Blocks.PLAYER_HEAD))
                .displayItems((featureFlags, output) -> {
                    Registration.ITEMS.getEntries().forEach(e -> output.accept(e.get()));
                })
        );
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        TCBSController.LOGGER.info("HELLO from server starting");
        Backend.init(event);
    }

    public static void logSided(String key) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> LOGGER.debug(String.format("[SIDED] %s: Client", key)));
        DistExecutor.unsafeRunWhenOn(Dist.DEDICATED_SERVER, () -> () -> LOGGER.debug(String.format("[SIDED] %s: Server", key)));
    }
}
