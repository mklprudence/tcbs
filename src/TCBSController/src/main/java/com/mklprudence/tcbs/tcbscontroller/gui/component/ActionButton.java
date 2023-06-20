package com.mklprudence.tcbs.tcbscontroller.gui.component;

import com.mklprudence.tcbs.tcbscontroller.gui.ControllerScreen;
import com.mklprudence.tcbs.tcbscontroller.network.ActionType;
import com.mklprudence.tcbs.tcbscontroller.network.Networking;
import com.mklprudence.tcbs.tcbscontroller.network.PacketAction;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class ActionButton {
    private ActionType action;
    private ControllerScreen parent;

    public ActionButton(ActionType action, ControllerScreen parent) {
        this.action = action;
        this.parent = parent;
    }

    public void onClick(Button pButton) {
        Networking.sendToServer(new PacketAction(parent.selectedTurtle, action));
    }

    public Button build() {
        return Button.builder(Component.translatable("screen.controller.button." + action), this::onClick)
                .pos(this.parent.width / 2 - 40, 96)
                .size(20, 20)
                .build();
    }
}
