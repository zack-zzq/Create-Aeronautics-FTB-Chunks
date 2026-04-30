package com.leclowndu93150.create_aeronautics_ftb_chunks.mixin;

import com.leclowndu93150.create_aeronautics_ftb_chunks.protect.ProtectionHelper;
import com.simibubi.create.content.contraptions.glue.SuperGlueRemovalPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SuperGlueRemovalPacket.class, remap = false)
public class MixinSuperGlueRemovalPacket {

    @Inject(method = "handle", at = @At("HEAD"), cancellable = true)
    private void onHandle(ServerPlayer player, CallbackInfo ci) {
        SuperGlueRemovalPacket self = (SuperGlueRemovalPacket) (Object) this;
        Entity entity = player.level().getEntity(self.entityId());
        if (entity != null && !ProtectionHelper.isAllowed(player, entity.blockPosition()))
            ci.cancel();
    }
}
