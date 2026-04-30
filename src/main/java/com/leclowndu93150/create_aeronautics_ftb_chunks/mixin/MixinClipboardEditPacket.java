package com.leclowndu93150.create_aeronautics_ftb_chunks.mixin;

import com.leclowndu93150.create_aeronautics_ftb_chunks.protect.ProtectionHelper;
import com.simibubi.create.content.equipment.clipboard.ClipboardEditPacket;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ClipboardEditPacket.class, remap = false)
public class MixinClipboardEditPacket {

    @Inject(method = "handle", at = @At("HEAD"), cancellable = true)
    private void onHandle(ServerPlayer player, CallbackInfo ci) {
        ClipboardEditPacket self = (ClipboardEditPacket) (Object) this;
        if (self.targetedBlock() != null && !ProtectionHelper.isAllowed(player, self.targetedBlock()))
            ci.cancel();
    }
}
