package com.leclowndu93150.create_aeronautics_ftb_chunks.mixin;

import com.leclowndu93150.create_aeronautics_ftb_chunks.protect.ProtectionHelper;
import com.simibubi.create.content.equipment.toolbox.ToolboxEquipPacket;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ToolboxEquipPacket.class, remap = false)
public class MixinToolboxEquipPacket {

    @Inject(method = "handle", at = @At("HEAD"), cancellable = true)
    private void onHandle(ServerPlayer player, CallbackInfo ci) {
        ToolboxEquipPacket self = (ToolboxEquipPacket) (Object) this;
        if (self.toolboxPos() != null && !ProtectionHelper.isAllowed(player, self.toolboxPos()))
            ci.cancel();
    }
}
