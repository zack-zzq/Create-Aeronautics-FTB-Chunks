package com.leclowndu93150.create_aeronautics_ftb_chunks.mixin;

import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;
import java.util.UUID;

@Mixin(value = ServerSubLevel.class, remap = false)
public interface ServerSubLevelAccessor {

    @Accessor("trackingPlayers")
    Set<UUID> getTrackingPlayers();
}
