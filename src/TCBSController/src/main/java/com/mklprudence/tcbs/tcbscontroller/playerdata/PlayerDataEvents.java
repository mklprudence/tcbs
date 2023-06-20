package com.mklprudence.tcbs.tcbscontroller.playerdata;

import com.mklprudence.tcbs.tcbscontroller.TCBSController;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class PlayerDataEvents {
    // Whenever a new object of some type is created the AttachCapabilitiesEvent will fire. In our case we want to know
    // when a new player arrives so that we can attach our capability here
    public static void onAttachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            if (!event.getObject().getCapability(PlayerFirstSpawnProvider.PLAYER_FIRST_SPAWN).isPresent()) {
                event.addCapability(new ResourceLocation(TCBSController.MODID, "playerfirstspawn"), new PlayerFirstSpawnProvider());
            }
        }
    }

    // When a player dies or teleports from the end capabilities are cleared. Using the PlayerEvent.Clone event
    // we can detect this and copy our capability from the old player to the new one
    public static void onPlayerCloned(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            event.getOriginal().getCapability(PlayerFirstSpawnProvider.PLAYER_FIRST_SPAWN).ifPresent(oldStore -> {
                event.getEntity().getCapability(PlayerFirstSpawnProvider.PLAYER_FIRST_SPAWN).ifPresent(newStore -> {
                    newStore.copyFrom(oldStore);
                });
            });
        }
    }

    // Finally we need to register our capability in a RegisterCapabilitiesEvent
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.register(PlayerFirstSpawn.class);
    }
}
