package com.leclowndu93150.create_aeronautics_ftb_chunks.mixin;

import com.leclowndu93150.create_aeronautics_ftb_chunks.protect.ProtectionHelper;
import com.simibubi.create.content.contraptions.wrench.RadialWrenchMenuSubmitPacket;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = RadialWrenchMenuSubmitPacket.class, remap = false)
public class MixinRadialWrenchMenuSubmitPacket {

    @Inject(method = "handle", at = @At("HEAD"), cancellable = true)
    private void onHandle(ServerPlayer player, CallbackInfo ci) {
        RadialWrenchMenuSubmitPacket self = (RadialWrenchMenuSubmitPacket) (Object) this;
        if (!ProtectionHelper.isAllowed(player, self.blockPos())) ci.cancel();
    }
}
