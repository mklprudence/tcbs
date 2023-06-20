package com.mklprudence.tcbs.tcbscontroller.network;

import com.mklprudence.tcbs.tcbscontroller.TCBSController;
import com.mklprudence.tcbs.tcbscontroller.backend.Backend;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketAction {
    private final String clientID;
    private final ActionType type;

    public PacketAction(String clientID, ActionType type) {
        this.clientID = clientID;
        this.type = type;
    }

    public PacketAction(FriendlyByteBuf buf) {
        this.clientID = buf.readUtf();
        this.type = ActionType.fromOrdinal(buf.readInt());
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(clientID);
        buf.writeInt(type.ordinal());
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            // SERVER-SIDE
            Backend.INSTANCE.sendAction(clientID, type);
        });
        return true;
    }
}
