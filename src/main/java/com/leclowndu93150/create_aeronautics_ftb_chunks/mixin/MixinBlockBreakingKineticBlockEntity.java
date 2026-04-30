package com.leclowndu93150.create_aeronautics_ftb_chunks.mixin;

import com.leclowndu93150.create_aeronautics_ftb_chunks.protect.ProtectionHelper;
import com.simibubi.create.content.kinetics.base.BlockBreakingKineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BlockBreakingKineticBlockEntity.class, remap = false)
public class MixinBlockBreakingKineticBlockEntity {

    @Shadow protected BlockPos breakingPos;

    @Inject(method = "canBreak", at = @At("HEAD"), cancellable = true)
    private void onCanBreak(BlockState state, float hardness, CallbackInfoReturnable<Boolean> cir) {
        if (breakingPos == null) return;
        var be = (BlockBreakingKineticBlockEntity) (Object) this;
        if (be.getLevel() instanceof ServerLevel level && ProtectionHelper.isChunkClaimed(level, breakingPos))
            cir.setReturnValue(false);
    }
}
