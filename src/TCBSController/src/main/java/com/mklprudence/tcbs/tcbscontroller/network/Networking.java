package com.mklprudence.tcbs.tcbscontroller.network;

import com.mklprudence.tcbs.tcbscontroller.TCBSController;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.List;

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

        INSTANCE.messageBuilder(PacketBackendInit.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(PacketBackendInit::new)
                .encoder(PacketBackendInit::toBytes)
                .consumerNetworkThread(PacketBackendInit::handle)
                .add();

        INSTANCE.messageBuilder(PacketSyncDataToClient.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(PacketSyncDataToClient::new)
                .encoder(PacketSyncDataToClient::toBytes)
                .consumerNetworkThread(PacketSyncDataToClient::handle)
                .add();

        INSTANCE.messageBuilder(PacketSyncDataToServer.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(PacketSyncDataToServer::new)
                .encoder(PacketSyncDataToServer::toBytes)
                .consumerNetworkThread(PacketSyncDataToServer::handle)
                .add();
    }

    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    public static <MSG> void broadcastToPlayer(MSG message) {
        if (TCBSController.MINECRAFT_SERVER == null) throw new RuntimeException("Dont use broadcastToPlayer in CLIENT-SIDE");
        TCBSController.LOGGER.debug(String.format("[DATA] starts broadcasting %s", message.getClass().getName()));
        TCBSController.MINECRAFT_SERVER.getPlayerList().getPlayers().forEach(p -> {
            sendToPlayer(message, p);
            TCBSController.LOGGER.debug(String.format("[DATA] sends %s to %s", message.getClass().getName(), p.getName().getString()));
        });
    }
}
