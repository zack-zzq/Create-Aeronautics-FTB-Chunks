# Create Aeronautics FTB Chunks

Adds FTB Chunks integration for Create Aeronautics ships (Sable sublevels).

Place a **Contraption Claim Block** on your ship. Right-clicking it opens a management screen where you can:

- Claim or unclaim the ship's chunks for your FTB team
- Toggle force loading to keep chunks loaded when players are away
- Optionally keep physics simulation active with no players nearby (server config)
- Cycle block access between Private, Allies, and Public
- Manage allies (requires a party team)

Once claimed, FTB Chunks protects the ship from other players. Create's wrenches, glue, and kinetic block breakers are also blocked by the claim.

Only one claim block per ship. Breaking the block releases all claims automatically.

## Requirements

- Create Aeronautics
- FTB Chunks
- FTB Teams

## Server Config

Two settings are off by default due to performance impact:

- `allow_plot_chunk_force_load` - keep anchor chunks loaded offline
- `allow_physics_force_load` - keep ship physics ticking with no players nearby
