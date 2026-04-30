package com.leclowndu93150.create_aeronautics_ftb_chunks.mixin;

import com.mojang.authlib.GameProfile;
import dev.ftb.mods.ftbteams.data.PartyTeam;
import net.minecraft.commands.CommandSourceStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Collection;

@Mixin(value = PartyTeam.class, remap = false)
public interface PartyTeamAccessor {

    @Invoker("addAlly")
    int invokeAddAlly(CommandSourceStack source, Collection<GameProfile> players) throws com.mojang.brigadier.exceptions.CommandSyntaxException;

    @Invoker("removeAlly")
    int invokeRemoveAlly(CommandSourceStack source, Collection<GameProfile> players);
}
