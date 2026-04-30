package com.leclowndu93150.create_aeronautics_ftb_chunks.network;

import com.leclowndu93150.create_aeronautics_ftb_chunks.CreateAeronauticsFTBChunks;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ContraptionAllyPacket(String playerName, boolean remove) implements CustomPacketPayload {

    public static final Type<ContraptionAllyPacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(CreateAeronauticsFTBChunks.MODID, "contraption_ally")
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, ContraptionAllyPacket> STREAM_CODEC = StreamCodec.of(
            (buf, p) -> { buf.writeUtf(p.playerName()); buf.writeBoolean(p.remove()); },
            buf -> new ContraptionAllyPacket(buf.readUtf(), buf.readBoolean())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
