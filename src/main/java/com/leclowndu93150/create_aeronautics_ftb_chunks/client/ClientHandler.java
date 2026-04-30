package com.leclowndu93150.create_aeronautics_ftb_chunks.client;

import com.leclowndu93150.create_aeronautics_ftb_chunks.client.gui.ContraptionClaimScreen;
import com.leclowndu93150.create_aeronautics_ftb_chunks.network.OpenContraptionScreenPacket;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ClientHandler {

    public static void handleOpenContraptionScreen(OpenContraptionScreenPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> ContraptionClaimScreen.open(packet));
    }
}
