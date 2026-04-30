package com.leclowndu93150.create_aeronautics_ftb_chunks.event;

import com.leclowndu93150.create_aeronautics_ftb_chunks.ContraptionForceLoadManager;
import dev.ftb.mods.ftbchunks.api.ClaimedChunk;
import dev.ftb.mods.ftbchunks.api.ClaimedChunkManager;
import dev.ftb.mods.ftbchunks.api.ChunkTeamData;
import dev.ftb.mods.ftbchunks.api.FTBChunksAPI;
import dev.ftb.mods.ftblibrary.math.ChunkDimPos;
import dev.ftb.mods.ftbteams.api.Team;
import dev.ryanhcode.sable.api.sublevel.SubLevelObserver;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.storage.SubLevelRemovalReason;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.ChunkPos;

import java.util.UUID;

public class SubLevelClaimHandler implements SubLevelObserver {

    private final MinecraftServer server;

    public SubLevelClaimHandler(MinecraftServer server) {
        this.server = server;
    }

    @Override
    public void onSubLevelRemoved(SubLevel subLevel, SubLevelRemovalReason reason) {
        ContraptionForceLoadManager.cleanup(subLevel.getUniqueId());
        if (reason != SubLevelRemovalReason.REMOVED) return;

        ClaimedChunkManager manager = FTBChunksAPI.api().getManager();
        var source = server.createCommandSourceStack().withSuppressedOutput();
        var level = subLevel.getLevel();
        var dimension = level.dimension();

        for (var holder : subLevel.getPlot().getLoadedChunks()) {
            ChunkPos chunkPos = holder.getPos();
            ChunkDimPos dimPos = new ChunkDimPos(dimension, chunkPos);
            ClaimedChunk existing = manager.getChunk(dimPos);
            if (existing == null) continue;

            Team team = existing.getTeamData().getTeam();
            UUID teamId = team.getId();
            ChunkTeamData teamData = manager.getOrCreateData(team);

            // Only unclaim chunks that are actually claimed by a team
            if (existing.getTeamData().getTeam().getId().equals(teamId)) {
                teamData.unForceLoad(source, dimPos, false);
                teamData.unclaim(source, dimPos, false);
            }
        }
    }
}
