package com.mklprudence.tcbs.tcbscontroller.network;

import com.mklprudence.tcbs.tcbscontroller.TCBSController;
import com.mklprudence.tcbs.tcbscontroller.data.Data;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketSyncDataToServer {
    private final String json;

    public PacketSyncDataToServer() {
        this.json = Data.CLIENT.toJson();
    }

    public PacketSyncDataToServer(FriendlyByteBuf buf) {
        this.json = buf.readUtf();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(json);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            // SERVER-SIDE
            Data.fromJson(Data.Side.SERVER, json);
            Data.SERVER.sync();
            TCBSController.LOGGER.debug("[DATA] PacketSyncDataToServer received");
            TCBSController.LOGGER.debug("[DATA] JSON: " + json);
        });
        return true;
    }
}
