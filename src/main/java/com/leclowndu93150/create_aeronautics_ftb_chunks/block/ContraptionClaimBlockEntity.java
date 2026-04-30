package com.leclowndu93150.create_aeronautics_ftb_chunks.block;

import com.leclowndu93150.create_aeronautics_ftb_chunks.CreateAeronauticsFTBChunks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class ContraptionClaimBlockEntity extends BlockEntity {

    @Nullable
    private UUID ownerUUID;
    @Nullable
    private UUID subLevelUUID;

    public ContraptionClaimBlockEntity(BlockPos pos, BlockState blockState) {
        super(CreateAeronauticsFTBChunks.CONTRAPTION_CLAIM_BLOCK_ENTITY.get(), pos, blockState);
    }

    @Nullable
    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    public void setOwnerUUID(UUID uuid) {
        this.ownerUUID = uuid;
        setChanged();
    }

    @Nullable
    public UUID getSubLevelUUID() {
        return subLevelUUID;
    }

    public void setSubLevelUUID(UUID uuid) {
        this.subLevelUUID = uuid;
        setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (ownerUUID != null) tag.putUUID("Owner", ownerUUID);
        if (subLevelUUID != null) tag.putUUID("SubLevel", subLevelUUID);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.hasUUID("Owner")) ownerUUID = tag.getUUID("Owner");
        if (tag.hasUUID("SubLevel")) subLevelUUID = tag.getUUID("SubLevel");
    }
}
