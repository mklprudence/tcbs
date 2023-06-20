package com.mklprudence.tcbs.tcbscontroller.network;

import com.mklprudence.tcbs.tcbscontroller.backend.Backend;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketLua {
    private final String clientID;
    private final String cmd;

    public PacketLua(String clientID, String cmd) {
        this.clientID = clientID;
        this.cmd = cmd;
    }

    public PacketLua(FriendlyByteBuf buf) {
        this.clientID = buf.readUtf();
        this.cmd = buf.readUtf();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(clientID);
        buf.writeUtf(cmd);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            // SERVER-SIDE
            Backend.INSTANCE.sendLua(clientID, cmd);
        });
        return true;
    }
}
