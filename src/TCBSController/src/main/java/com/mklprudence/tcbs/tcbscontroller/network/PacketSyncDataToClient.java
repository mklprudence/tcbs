package com.mklprudence.tcbs.tcbscontroller.network;

import com.mklprudence.tcbs.tcbscontroller.TCBSController;
import com.mklprudence.tcbs.tcbscontroller.backend.Backend;
import com.mklprudence.tcbs.tcbscontroller.data.Data;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketSyncDataToClient {
    private final String json;

    public PacketSyncDataToClient() {
        this.json = Data.SERVER.toJson();
    }

    public PacketSyncDataToClient(FriendlyByteBuf buf) {
        this.json = buf.readUtf();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(json);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            // CLIENT-SIDE
            Data.fromJson(Data.Side.CLIENT, json);
            TCBSController.LOGGER.debug("[DATA] PacketSyncDataToClient received");
            TCBSController.LOGGER.debug("[DATA] JSON: " + json);
        });
        return true;
    }
}
