package com.leclowndu93150.create_aeronautics_ftb_chunks;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ModConfig {

    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.BooleanValue ALLOW_PHYSICS_FORCE_LOAD;
    public static final ModConfigSpec.BooleanValue ALLOW_PLOT_CHUNK_FORCE_LOAD;

    static {
        BUILDER.comment("Contraption force loading settings. These are dangerous — misuse can cause severe server lag.");
        BUILDER.push("force_loading");

        ALLOW_PLOT_CHUNK_FORCE_LOAD = BUILDER
                .comment(
                        "Allow players to force-load the plot chunks (the real-world anchor chunks) of a claimed contraption via FTB Chunks.",
                        "This keeps the contraption from being serialized/frozen when no players are nearby.",
                        "Respects FTB Chunks force-load limits and offline force-load settings.",
                        "WARNING: Force-loading chunks can cause significant server lag if overused."
                )
                .define("allow_plot_chunk_force_load", false);

        ALLOW_PHYSICS_FORCE_LOAD = BUILDER
                .comment(
                        "Allow players to keep a contraption's physics simulation running even when no players are nearby.",
                        "This injects a fake tracking UUID into Sable's tracking system so the contraption keeps ticking.",
                        "Only works if allow_plot_chunk_force_load is also enabled (physics needs the chunks loaded).",
                        "WARNING: This is VERY dangerous. Physics simulation runs every tick regardless of player presence."
                )
                .define("allow_physics_force_load", false);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
