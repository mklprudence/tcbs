package com.mklprudence.tcbs.tcbscontroller.playerdata;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerFirstSpawnProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    public static Capability<PlayerFirstSpawn> PLAYER_FIRST_SPAWN = CapabilityManager.get(new CapabilityToken<PlayerFirstSpawn>() {});

    private PlayerFirstSpawn playerFirstSpawn = null;
    private final LazyOptional<PlayerFirstSpawn> opt = LazyOptional.of(this::createPlayerFirstSpawn);

    private @NotNull PlayerFirstSpawn createPlayerFirstSpawn() {
        if (playerFirstSpawn == null) playerFirstSpawn = new PlayerFirstSpawn();
        return playerFirstSpawn;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap) {
        if (cap == PLAYER_FIRST_SPAWN) return opt.cast();
        return LazyOptional.empty();
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return getCapability(cap);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        createPlayerFirstSpawn().saveNBTData(nbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        createPlayerFirstSpawn().loadNBTData(nbt);
    }
}
