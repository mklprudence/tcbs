package com.mklprudence.tcbs.tcbscontroller.network;

import com.mklprudence.tcbs.tcbscontroller.TCBSController;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class Networking {
    private static SimpleChannel INSTANCE;

    private static int packetId = 0;
    private static int id() {
        return packetId++;
    }

    public static void register() {
        INSTANCE = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(TCBSController.MODID, "messages"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE.messageBuilder(PacketAction.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(PacketAction::new)
                .encoder(PacketAction::toBytes)
                .consumerNetworkThread(PacketAction::handle)
                .add();

        INSTANCE.messageBuilder(PacketLua.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(PacketLua::new)
                .encoder(PacketLua::toBytes)
                .consumerNetworkThread(PacketLua::handle)
                .add();
    }

    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }
}
