package com.mklprudence.tcbs.tcbscontroller.gui.component;

import com.mklprudence.tcbs.tcbscontroller.gui.ControllerScreen;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.network.chat.Component;

public class LuaGrid {
    private ControllerScreen parent;
    private EditBox luaBox;
    private LuaButton luaButton;

    public LuaGrid(ControllerScreen parent) {
        this.parent = parent;
        luaBox = new EditBox(parent.getFontRenderer(), 0, 0, parent.width - 70, 20, Component.translatable("screen.controller.luabox"));
        luaButton = new LuaButton(luaBox, parent);
    }

    public GridLayout build() {
        GridLayout grid = new GridLayout();
        grid.rowSpacing(10).columnSpacing(10);
        grid.addChild(luaBox, 0, 0, 1, 1);
        grid.addChild(luaButton.build(), 0, 1, 1, 1);
        grid.arrangeElements();
        FrameLayout.alignInRectangle(grid, 0, parent.height - 30, parent.width, parent.height, 0.5f, 0);
        grid.visitWidgets(w -> parent.addRenderableWidget(w));
        return grid;
    }

    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (this.luaBox.isFocused()) {
            if (pKeyCode == InputConstants.KEY_ESCAPE) {
                this.luaBox.setFocused(false);
                return true;
            } else if (pKeyCode == InputConstants.KEY_RETURN || pKeyCode == InputConstants.KEY_NUMPADENTER) {
                this.luaButton.onClick();
                return true;
            }
            this.luaBox.keyPressed(pKeyCode, pScanCode, pModifiers);
            return true;
        }
        return false;
    }
}
