package com.mklprudence.tcbs.tcbscontroller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mklprudence.tcbs.tcbscontroller.backend.Backend;
import com.mklprudence.tcbs.tcbscontroller.data.Data;
import com.mklprudence.tcbs.tcbscontroller.network.Networking;
import com.mklprudence.tcbs.tcbscontroller.playerdata.PlayerFirstSpawnProvider;
import com.mklprudence.tcbs.tcbscontroller.setup.CommonSetup;
import com.mklprudence.tcbs.tcbscontroller.setup.Config;
import com.mklprudence.tcbs.tcbscontroller.setup.Registration;
import com.mojang.logging.LogUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import java.lang.reflect.Modifier;

@Mod(TCBSController.MODID)
public class TCBSController {
    public static final String MODID = "tcbscontroller";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final Gson GSON = new GsonBuilder()
            .excludeFieldsWithModifiers(Modifier.TRANSIENT)
            .create();
    public static final Gson GSON_PRETTY = new GsonBuilder()
            .setPrettyPrinting()
            .create();
    public static MinecraftServer MINECRAFT_SERVER = null;

    public TCBSController() {
        MinecraftForge.EVENT_BUS.register(this);
        CommonSetup.setup();
        Registration.init();
        Config.register();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerTabs);
        //test();
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

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        TCBSController.LOGGER.info("HELLO from server starting");
        Backend.init();
        TCBSController.MINECRAFT_SERVER = event.getServer();
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (TCBSController.isServerSide()) Data.SERVER.sync();

        event.getEntity().getCapability(PlayerFirstSpawnProvider.PLAYER_FIRST_SPAWN).ifPresent(store -> {
            if (store.isPlayerFirstSpawn()) {
                if (event.getEntity().getInventory().add(new ItemStack(Registration.CONTROLLER_ITEM.get()))) {
                    store.setPlayerFirstSpawn(false);
                }
            }
        });
    }

    public static void logSided(String key) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> LOGGER.debug(String.format("[SIDED] %s: Client", key)));
        DistExecutor.unsafeRunWhenOn(Dist.DEDICATED_SERVER, () -> () -> LOGGER.debug(String.format("[SIDED] %s: Server", key)));
    }

    public static boolean isServerSide() {
        return MINECRAFT_SERVER != null;
    }

    public static void test() {
        String a = "";
        LOGGER.debug(String.format("[TEST] %s", a.split(":").length));
    }
}
