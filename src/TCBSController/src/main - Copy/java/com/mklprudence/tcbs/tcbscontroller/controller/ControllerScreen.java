package com.mklprudence.tcbs.tcbscontroller.gui;

import com.mklprudence.tcbs.tcbscontroller.TCBSController;
import com.mklprudence.tcbs.tcbscontroller.backend.Backend;
import com.mklprudence.tcbs.tcbscontroller.gui.component.ActionButton;
import com.mklprudence.tcbs.tcbscontroller.gui.component.LuaButton;
import com.mklprudence.tcbs.tcbscontroller.gui.component.LuaGrid;
import com.mklprudence.tcbs.tcbscontroller.gui.component.TurtleCycler;
import com.mklprudence.tcbs.tcbscontroller.data.Turtle;
import com.mklprudence.tcbs.tcbscontroller.network.ActionType;
import com.mklprudence.tcbs.tcbscontroller.network.Networking;
import com.mklprudence.tcbs.tcbscontroller.network.PacketAction;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.SpacerElement;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;

public class ControllerScreen extends AbstractContainerScreen<ControllerMenu> {
    GridLayout turtleSelectorGrid, buttonGrid;
    LuaGrid luaGrid;
    public Turtle selectedTurtle;

    public ControllerScreen(ControllerMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.inventoryLabelX = 10000;
        this.inventoryLabelY = 10000;
        this.titleLabelX = 10000;
        this.titleLabelY = 10000;
        pMenu.setParent(this);
    }

    @Override
    protected void init() {
        super.init();
        Backend.init();

        turtleSelectorGrid = new GridLayout();
        turtleSelectorGrid.rowSpacing(10).columnSpacing(10);
        CycleButton<String> cycler = new TurtleCycler(this).build();
        this.selectedTurtle = Turtle.getTurtleFromID(cycler.getValue());
        turtleSelectorGrid.addChild(cycler, 0, 0, 1, 1);
        turtleSelectorGrid.addChild(new ActionButton(ActionType.Terminate, this).build(), 0, 1, 1, 1);
        turtleSelectorGrid.arrangeElements();
        FrameLayout.alignInRectangle(turtleSelectorGrid, 0, 10, this.width, this.height, 0.5f, 0);
        turtleSelectorGrid.visitWidgets(this::addRenderableWidget);


        buttonGrid = new GridLayout();
        buttonGrid.rowSpacing(10).columnSpacing(10);
        buttonGrid.addChild(new ActionButton(ActionType.Forward, this).build(), 1, 1, 1, 1);
        buttonGrid.addChild(new ActionButton(ActionType.Backward, this).build(), 2, 2, 1, 1);
        buttonGrid.addChild(new ActionButton(ActionType.Up, this).build(), 0, 1, 1, 1);
        buttonGrid.addChild(new ActionButton(ActionType.Down, this).build(), 2, 1, 1, 1);
        buttonGrid.addChild(new ActionButton(ActionType.TurnLeft, this).build(), 1, 0, 1, 1);
        buttonGrid.addChild(new ActionButton(ActionType.TurnRight, this).build(), 1, 2, 1, 1);

        buttonGrid.addChild(new SpacerElement(20, 20), 1, 3, 1, 1);

        buttonGrid.addChild(new ActionButton(ActionType.Dig, this).build(), 1, 4, 1, 1);
        buttonGrid.addChild(new ActionButton(ActionType.DigUp, this).build(), 0, 4, 1, 1);
        buttonGrid.addChild(new ActionButton(ActionType.DigDown, this).build(), 2, 4, 1, 1);

        buttonGrid.addChild(new SpacerElement(20, 20), 1, 5, 1, 1);

        buttonGrid.addChild(new ActionButton(ActionType.Place, this).build(), 1, 6, 1, 1);
        buttonGrid.addChild(new ActionButton(ActionType.PlaceUp, this).build(), 0, 6, 1, 1);
        buttonGrid.addChild(new ActionButton(ActionType.PlaceDown, this).build(), 2, 6, 1, 1);

        buttonGrid.addChild(new SpacerElement(20, 20), 1, 7, 1, 1);

        buttonGrid.addChild(new ActionButton(ActionType.Attack, this).build(), 1, 8, 1, 1);
        buttonGrid.addChild(new ActionButton(ActionType.AttackUp, this).build(), 0, 8, 1, 1);
        buttonGrid.addChild(new ActionButton(ActionType.AttackDown, this).build(), 2, 8, 1, 1);

        buttonGrid.addChild(new SpacerElement(20, 20), 1, 9, 1, 1);

        buttonGrid.addChild(new ActionButton(ActionType.Suck, this).build(), 1, 10, 1, 1);
        buttonGrid.addChild(new ActionButton(ActionType.SuckUp, this).build(), 0, 10, 1, 1);
        buttonGrid.addChild(new ActionButton(ActionType.SuckDown, this).build(), 2, 10, 1, 1);

        buttonGrid.addChild(new SpacerElement(20, 20), 1, 11, 1, 1);

        buttonGrid.addChild(new ActionButton(ActionType.Inspect, this).build(), 1, 12, 1, 1);
        buttonGrid.addChild(new ActionButton(ActionType.InspectUp, this).build(), 0, 12, 1, 1);
        buttonGrid.addChild(new ActionButton(ActionType.InspectDown, this).build(), 2, 12, 1, 1);

        buttonGrid.arrangeElements();
        FrameLayout.alignInRectangle(buttonGrid, 0, 40, this.width, this.height, 0.5f, 0);
        buttonGrid.visitWidgets(this::addRenderableWidget);


        luaGrid = new LuaGrid(this);
        luaGrid.build();
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pPoseStack);
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        this.renderTooltip(pPoseStack, pMouseX, pMouseY);
    }

    @Override
    protected void renderBg(PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY) {}

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        InputConstants.Key mouseKey = InputConstants.getKey(pKeyCode, pScanCode);
        if (luaGrid.keyPressed(pKeyCode, pScanCode, pModifiers)) return true;

        if (pKeyCode == InputConstants.KEY_W) {
            Networking.sendToServer(new PacketAction(this.selectedTurtle.getClientID(), ActionType.Forward));
            return true;
        } else if (pKeyCode == InputConstants.KEY_A) {
            Networking.sendToServer(new PacketAction(this.selectedTurtle.getClientID(), ActionType.TurnLeft));
            return true;
        } else if (pKeyCode == InputConstants.KEY_S) {
            Networking.sendToServer(new PacketAction(this.selectedTurtle.getClientID(), ActionType.Backward));
            return true;
        } else if (pKeyCode == InputConstants.KEY_D) {
            Networking.sendToServer(new PacketAction(this.selectedTurtle.getClientID(), ActionType.TurnRight));
            return true;
        } else if (pKeyCode == InputConstants.KEY_SPACE) {
            Networking.sendToServer(new PacketAction(this.selectedTurtle.getClientID(), ActionType.Up));
            return true;
        } else if (pKeyCode == InputConstants.KEY_LSHIFT) {
            Networking.sendToServer(new PacketAction(this.selectedTurtle.getClientID(), ActionType.Down));
            return true;
        }

        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    /*
    @Override
    public Optional<GuiEventListener> getChildAt(double pMouseX, double pMouseY) {
        return super.getChildAt(pMouseX, pMouseY);
    }

    @Override
    public void mouseMoved(double pMouseX, double pMouseY) {
        super.mouseMoved(pMouseX, pMouseY);
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        return super.mouseScrolled(pMouseX, pMouseY, pDelta);
    }

    @Override
    public boolean keyReleased(int pKeyCode, int pScanCode, int pModifiers) {
        return super.keyReleased(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    public boolean charTyped(char pCodePoint, int pModifiers) {
        return super.charTyped(pCodePoint, pModifiers);
    }

    @Override
    public void setFocused(boolean p_265504_) {
        super.setFocused(p_265504_);
    }

    @Override
    public boolean isFocused() {
        return super.isFocused();
    }

    @Nullable
    @Override
    public ComponentPath getCurrentFocusPath() {
        return super.getCurrentFocusPath();
    }

    @Override
    public void magicalSpecialHackyFocus(@Nullable GuiEventListener pEventListener) {
        super.magicalSpecialHackyFocus(pEventListener);
    }

    @Nullable
    @Override
    public ComponentPath nextFocusPath(FocusNavigationEvent p_265668_) {
        return super.nextFocusPath(p_265668_);
    }

    @Override
    public int getTabOrderGroup() {
        return super.getTabOrderGroup();
    }

     */

    public Font getFontRenderer() {
        return this.font;
    }

    public <T extends GuiEventListener & Renderable & NarratableEntry> T addRenderableWidget(T pWidget) {
        this.renderables.add(pWidget);
        return this.addWidget(pWidget);
    }
}
