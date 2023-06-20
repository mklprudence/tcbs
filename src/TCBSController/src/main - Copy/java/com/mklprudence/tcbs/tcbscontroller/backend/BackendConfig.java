package com.mklprudence.tcbs.tcbscontroller.backend;

import net.minecraftforge.common.ForgeConfigSpec;

public class BackendConfig {
    public static ForgeConfigSpec.ConfigValue<String> BACKEND_URL;

    public static void registerServerConfig(ForgeConfigSpec.Builder SERVER_BUILDER) {
        SERVER_BUILDER.comment("Settings for backend communication").push("backend");
        BACKEND_URL = SERVER_BUILDER
                .comment("Base URL for backend communication")
                .define("url", "ws://localhost");
        SERVER_BUILDER.pop();
    }
}
