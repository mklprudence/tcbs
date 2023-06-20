package com.mklprudence.tcbs.tcbscontroller.gui;

import com.mklprudence.tcbs.tcbscontroller.setup.Registration;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

public class ControllerMenu extends AbstractContainerMenu {
    private Player playerEntity;
    private IItemHandler playerInv;
    private ControllerScreen parent;

    public ControllerMenu(int windowId, Inventory inv) {
        super(Registration.CONTROLLER_MENU.get(), windowId);
        playerEntity = inv.player;
        playerInv = new InvWrapper(inv);
    }

    public void setParent(ControllerScreen parent) {
        this.parent = parent;
    }

    private void trackFuel() {
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return parent.selectedTurtle.getFuelLevel();
            }

            @Override
            public void set(int pValue) {
                // DONT LET SERVER / CLIENT SET FUEL? LET BACKEND DO IT
            }
        });
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        return null;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return true;
    }
}
