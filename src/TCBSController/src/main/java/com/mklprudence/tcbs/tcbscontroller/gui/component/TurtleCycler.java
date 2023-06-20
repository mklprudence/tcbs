package com.mklprudence.tcbs.tcbscontroller.gui.component;

import com.mklprudence.tcbs.tcbscontroller.gui.ControllerScreen;
import com.mklprudence.tcbs.tcbscontroller.data.Data;
import com.mklprudence.tcbs.tcbscontroller.data.Turtle;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.network.chat.Component;

import java.util.List;

public class TurtleCycler {
    private ControllerScreen parent;

    public TurtleCycler(ControllerScreen parent) {
        this.parent = parent;
    }

    public CycleButton<String> build() {
        CycleButton.ValueListSupplier<String> values = new CycleButton.ValueListSupplier<String>() {
            public List<String> getSelectedList() {
                return Data.CLIENT.getTurtleClients().stream().map(s -> Turtle.getIDfromClientID(s)).toList();
            }

            public List<String> getDefaultList() {
                return Data.CLIENT.getTurtleClients().stream().map(s -> Turtle.getIDfromClientID(s)).toList();
            }
        };

        return new CycleButton.Builder<String>((e) -> Component.literal(e.toString()))
                .withValues(values)
                .create(0, 0, 160, 20, Component.translatable("screen.controller.cycler"), (but, val) -> {
                    parent.selectedTurtle = Turtle.getClientIDfromID(val);
                    parent.progressBar.setProgress();
                });
    }

    public CycleButton<String> build(String prevValue) {
        CycleButton<String> button = build();
        if (prevValue == null) return button;
        if (Data.CLIENT.getTurtleClients().contains(prevValue)) button.setValue(Turtle.getIDfromClientID(prevValue));
        return button;
    }
}
