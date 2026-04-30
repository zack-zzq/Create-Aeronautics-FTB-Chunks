package com.leclowndu93150.create_aeronautics_ftb_chunks.network;

import com.leclowndu93150.create_aeronautics_ftb_chunks.CreateAeronauticsFTBChunks;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ContraptionClaimActionPacket(Action action) implements CustomPacketPayload {

    public enum Action { CLAIM, UNCLAIM, FORCE_LOAD, UNFORCE_LOAD, ACCESS_PRIVATE, ACCESS_ALLIES, ACCESS_PUBLIC, PHYSICS_FORCE_LOAD, PHYSICS_UNFORCE_LOAD }

    public static final Type<ContraptionClaimActionPacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(CreateAeronauticsFTBChunks.MODID, "contraption_claim_action")
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, ContraptionClaimActionPacket> STREAM_CODEC = StreamCodec.of(
            (buf, p) -> buf.writeEnum(p.action()),
            buf -> new ContraptionClaimActionPacket(buf.readEnum(Action.class))
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
