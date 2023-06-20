package com.mklprudence.tcbs.tcbscontroller.gui.component;

import com.mklprudence.tcbs.tcbscontroller.gui.ControllerScreen;
import com.mklprudence.tcbs.tcbscontroller.network.Networking;
import com.mklprudence.tcbs.tcbscontroller.network.PacketLua;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

public class LuaButton {
    private ControllerScreen parent;
    private EditBox box;

    public LuaButton(EditBox box, ControllerScreen parent) {
        this.box = box;
        this.parent = parent;
    }

    public void onClick(Button pButton) {
        onClick();
    }

    public void onClick() {
        Networking.sendToServer(new PacketLua(parent.selectedTurtle, box.getValue()));
    }

    public Button build() {
        return Button.builder(Component.translatable("screen.controller.button.Lua"), this::onClick)
                .pos(this.parent.width / 2 - 40, 96)
                .size(20, 20)
                .build();
    }
}
