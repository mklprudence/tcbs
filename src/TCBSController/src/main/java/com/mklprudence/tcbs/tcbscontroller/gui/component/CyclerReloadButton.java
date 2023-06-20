package com.mklprudence.tcbs.tcbscontroller.gui.component;

import com.mklprudence.tcbs.tcbscontroller.gui.ControllerScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class CyclerReloadButton {
    private ControllerScreen parent;

    public CyclerReloadButton(ControllerScreen parent) {
        this.parent = parent;
    }

    public void onClick(Button pButton) {
        parent.reload();
    }

    public Button build() {
        return Button.builder(Component.translatable("screen.controller.button.Reload"), this::onClick)
                .pos(this.parent.width / 2 - 40, 96)
                .size(20, 20)
                .build();
    }
}
