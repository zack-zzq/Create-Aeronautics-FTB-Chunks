package com.leclowndu93150.create_aeronautics_ftb_chunks.protect;

import dev.ftb.mods.ftbchunks.FTBChunksExpected;
import dev.ftb.mods.ftbchunks.api.FTBChunksAPI;
import dev.ftb.mods.ftbchunks.data.ClaimedChunkManagerImpl;
import dev.ftb.mods.ftblibrary.math.ChunkDimPos;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;

public class ProtectionHelper {

    public static boolean isAllowed(ServerPlayer player, BlockPos pos) {
        if (player == null || pos == null) return true;
        return !ClaimedChunkManagerImpl.getInstance().shouldPreventInteraction(
                player, InteractionHand.MAIN_HAND, pos, FTBChunksExpected.getBlockInteractProtection(), null);
    }

    public static boolean isChunkClaimed(ServerLevel level, BlockPos pos) {
        return FTBChunksAPI.api().getManager().getChunk(new ChunkDimPos(level.dimension(), pos.getX() >> 4, pos.getZ() >> 4)) != null;
    }
}
