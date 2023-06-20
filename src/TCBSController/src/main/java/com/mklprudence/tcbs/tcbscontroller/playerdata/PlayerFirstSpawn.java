package com.mklprudence.tcbs.tcbscontroller.playerdata;

import net.minecraft.nbt.CompoundTag;

public class PlayerFirstSpawn {
    private boolean playerFirstSpawn = true;

    public PlayerFirstSpawn() {}

    public boolean isPlayerFirstSpawn() {
        return playerFirstSpawn;
    }

    public void setPlayerFirstSpawn(boolean playerFirstSpawn) {
        this.playerFirstSpawn = playerFirstSpawn;
    }

    public void copyFrom(PlayerFirstSpawn source) {
        playerFirstSpawn = source.playerFirstSpawn;
    }


    public void saveNBTData(CompoundTag compound) {
        compound.putBoolean("firstSpawn", playerFirstSpawn);
    }

    public void loadNBTData(CompoundTag compound) {
        playerFirstSpawn = compound.getBoolean("firstSpawn");
    }
}
