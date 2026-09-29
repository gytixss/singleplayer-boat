# Singleplayer Boat

A lightweight Fabric mod that lets you spawn a boat exactly where you're standing, facing the direction you're looking.

## Commands

`/boat` — spawns a boat (default: oak) at your position and puts you in it immediately.
`/b` — shorthand alias for `/boat`, works identically.
`/boat <type>` — spawns a specific wood type or chest boat variant.
`/boat default <type>` — sets which type `/boat` and `/b` spawn by default when run with no argument.

### Available types

`oak`, `spruce`, `birch`, `jungle`, `acacia`, `dark_oak`, `mangrove`, `cherry`, `pale_oak`, `bamboo`

Add `_chest` to any of the above (e.g. `oak_chest`, `cherry_chest`) for the chest boat variant.

**Examples:**
/boat cherry
/boat default mangrove_chest
/b

## Requirements

Minecraft 1.21.4
[Fabric Loader](https://fabricmc.net/use/) 0.19.5+
[Fabric API](https://modrinth.com/mod/fabric-api)

## Installation

1. Install Fabric Loader for Minecraft 1.21.4.
2. Download and place [Fabric API](https://modrinth.com/mod/fabric-api) in your `mods` folder.
3. Download this mod's jar and place it in the same `mods` folder.
4. Launch the game.

## License

This project is licensed under [CC0-1.0](LICENSE) — public domain, do whatever you like with it.