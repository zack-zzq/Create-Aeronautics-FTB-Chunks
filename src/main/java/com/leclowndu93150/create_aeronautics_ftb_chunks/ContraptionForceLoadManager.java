package com.leclowndu93150.create_aeronautics_ftb_chunks;

import com.leclowndu93150.create_aeronautics_ftb_chunks.mixin.ServerSubLevelAccessor;
import dev.ftb.mods.ftbchunks.api.ClaimedChunk;
import dev.ftb.mods.ftbchunks.api.ClaimedChunkManager;
import dev.ftb.mods.ftbchunks.api.ChunkTeamData;
import dev.ftb.mods.ftbchunks.api.FTBChunksAPI;
import dev.ftb.mods.ftblibrary.math.ChunkDimPos;
import dev.ftb.mods.ftbteams.api.FTBTeamsAPI;
import dev.ftb.mods.ftbteams.api.Team;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.plot.LevelPlot;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ContraptionForceLoadManager {

    public static final UUID FAKE_TRACKING_UUID = UUID.fromString("deadbeef-cafe-babe-feed-c0ffee000001");

    private static final Map<UUID, UUID> physicsForceLoaded = new ConcurrentHashMap<>();
    private static final Map<UUID, UUID> plotForceLoaded = new ConcurrentHashMap<>();

    public static boolean enablePlotForceLoad(MinecraftServer server, UUID subLevelUUID, UUID ownerUUID) {
        if (!ModConfig.ALLOW_PLOT_CHUNK_FORCE_LOAD.get()) return false;

        Optional<Team> teamOpt = FTBTeamsAPI.api().getManager().getTeamForPlayerID(ownerUUID);
        if (teamOpt.isEmpty()) return false;

        Team team = teamOpt.get();
        ClaimedChunkManager manager = FTBChunksAPI.api().getManager();
        ChunkTeamData teamData = manager.getOrCreateData(team);

        if (!teamData.canDoOfflineForceLoading()) return false;

        ServerLevel overworld = server.overworld();
        SubLevelContainer container = SubLevelContainer.getContainer(overworld);
        if (container == null) return false;

        ServerSubLevel subLevel = (ServerSubLevel) container.getSubLevel(subLevelUUID);
        if (subLevel == null) return false;

        LevelPlot plot = subLevel.getPlot();
        var source = server.createCommandSourceStack().withSuppressedOutput();
        int claimed = 0;

        for (var holder : plot.getLoadedChunks()) {
            ChunkPos chunkPos = holder.getPos();
            ChunkDimPos dimPos = new ChunkDimPos(overworld.dimension(), chunkPos);
            ClaimedChunk existing = manager.getChunk(dimPos);

            if (existing == null || !existing.getTeamData().getTeam().getId().equals(team.getId())) continue;
            if (teamData.getForceLoadedChunks().size() >= teamData.getMaxForceLoadChunks()) continue;

            if (teamData.forceLoad(source, dimPos, false).isSuccess()) claimed++;
        }

        if (claimed > 0) {
            plotForceLoaded.put(subLevelUUID, ownerUUID);
            return true;
        }
        return false;
    }

    public static void disablePlotForceLoad(MinecraftServer server, UUID subLevelUUID) {
        UUID ownerUUID = plotForceLoaded.remove(subLevelUUID);
        if (ownerUUID == null) return;

        Optional<Team> teamOpt = FTBTeamsAPI.api().getManager().getTeamForPlayerID(ownerUUID);
        if (teamOpt.isEmpty()) return;

        ClaimedChunkManager manager = FTBChunksAPI.api().getManager();
        ChunkTeamData teamData = manager.getOrCreateData(teamOpt.get());
        ServerLevel overworld = server.overworld();
        SubLevelContainer container = SubLevelContainer.getContainer(overworld);
        if (container == null) return;

        ServerSubLevel subLevel = (ServerSubLevel) container.getSubLevel(subLevelUUID);
        if (subLevel == null) return;

        var source = server.createCommandSourceStack().withSuppressedOutput();
        for (var holder : subLevel.getPlot().getLoadedChunks()) {
            teamData.unForceLoad(source, new ChunkDimPos(overworld.dimension(), holder.getPos()), false);
        }

        physicsForceLoaded.remove(subLevelUUID);
    }

    public static boolean enablePhysicsForceLoad(MinecraftServer server, UUID subLevelUUID, UUID ownerUUID) {
        if (!ModConfig.ALLOW_PHYSICS_FORCE_LOAD.get()) return false;
        if (!ModConfig.ALLOW_PLOT_CHUNK_FORCE_LOAD.get() || !plotForceLoaded.containsKey(subLevelUUID)) return false;

        ServerLevel overworld = server.overworld();
        SubLevelContainer container = SubLevelContainer.getContainer(overworld);
        if (container == null) return false;

        ServerSubLevel subLevel = (ServerSubLevel) container.getSubLevel(subLevelUUID);
        if (subLevel == null) return false;

        ((ServerSubLevelAccessor) subLevel).getTrackingPlayers().add(FAKE_TRACKING_UUID);
        physicsForceLoaded.put(subLevelUUID, ownerUUID);
        return true;
    }

    public static void disablePhysicsForceLoad(MinecraftServer server, UUID subLevelUUID) {
        if (physicsForceLoaded.remove(subLevelUUID) == null) return;

        ServerLevel overworld = server.overworld();
        SubLevelContainer container = SubLevelContainer.getContainer(overworld);
        if (container == null) return;

        ServerSubLevel subLevel = (ServerSubLevel) container.getSubLevel(subLevelUUID);
        if (subLevel == null) return;

        ((ServerSubLevelAccessor) subLevel).getTrackingPlayers().remove(FAKE_TRACKING_UUID);
    }

    public static boolean isPlotForceLoaded(UUID subLevelUUID) {
        return plotForceLoaded.containsKey(subLevelUUID);
    }

    public static boolean isPhysicsForceLoaded(UUID subLevelUUID) {
        return physicsForceLoaded.containsKey(subLevelUUID);
    }

    public static Set<UUID> getPhysicsForceLoadedSubLevels() {
        return Collections.unmodifiableSet(physicsForceLoaded.keySet());
    }

    public static void cleanup(UUID subLevelUUID) {
        physicsForceLoaded.remove(subLevelUUID);
        plotForceLoaded.remove(subLevelUUID);
    }

    public static void cleanupAll() {
        physicsForceLoaded.clear();
        plotForceLoaded.clear();
    }
}
