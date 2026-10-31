# Goety Charms

Prepare the ritual. Carry its power.

**Goety Charms** lets you seal supported **Goety rituals** into single-use charms. Complete the preparation at a Dark Altar, then take the finished charm wherever you need it.

## Four Types of Charm

| Charm | What it does |
| --- | --- |
| **Summoning** | Releases the native summoned creature or ritual entity at the selected location. |
| **Conversion** | Applies the original ritual to the creature you aim at. Its tooltip names the valid target. |
| **Teleportation** | Returns you to the destination stored in the waystone used during preparation, following Goety's native same-dimension teleportation. |
| **Location** | Searches from your current position and produces Goety's native exploration map for the selected structure type. |

Native servant ownership, variants, equipment, lifetimes and applicable summoning limits remain in Goety's hands. A hostile ritual result does not become friendly simply because it was released from a charm.

## Making and Using Charms

Craft **four Blank Charms** with paper in the center and nether quartz above, below, left and right. The corners stay empty.

1. Build the Dark Altar and required ritual environment as usual.
2. Place the original activation item **and** all additional ingredients on the surrounding pedestals.
3. Hold a Blank Charm and use it on the altar. Research, soul energy, ritual duration, environment and sacrifice requirements follow the original recipe.
4. If several rituals match, sneak-use the altar to cycle through them, then use normally to confirm.
5. Take the completed charm and **hold use for one second** to release it.

Conversion targets are only needed when releasing the charm. Teleportation preparation requires an already-bound waystone; its actual destination is preserved. Location charms search when used, rather than when sealed.

A successful survival use consumes one charm. Invalid targets and failed releases retain it; creative use does not consume it. The ritual's soul fee is paid during preparation, **not charged a second time on release**. A recipient can use a finished charm without repeating the maker's research.

Location searches take time. Keep the selected charm in your inventory until the map arrives; it is consumed only after success. Full inventories receive the map as a nearby item drop.

## Recipes and Compatibility

Supported rituals automatically gain real sealing recipes in **Goety's native JEI ritual category**. New datapack or addon recipes can work automatically when they use the supported native summoning, conversion, teleportation or locating executors. Custom ritual implementations may need an explicit adapter.

This does **not** turn every crafting or enchanting ritual into a charm. Goety's native mechanics still control the resulting creatures and effects.

Creative-tab entries without sealed data are previews; craft a charm or use the operator command to obtain a functional one.

## Optional Ars Integration

With **Goety & Nouveau 0.1.13+** and its dependencies installed, **Activate Charm** becomes available as an Ars effect glyph. For example, **Projectile → Activate Charm** releases a carried summoning or conversion charm at the Ars-selected target.

It checks your **offhand first**, then your **hotbar from left to right**, then your **backpack row by row**. A failed attempt keeps the chosen charm and does not try another. Teleportation and location charms are skipped by this glyph.

Learn the glyph through the native Scribe's Table using **Blank Charm + Source Gem**. It adds no second ritual soul fee. **Ars Nouveau, Ars Conflux and Goety & Nouveau are entirely optional for Goety Charms itself.**

## Configuration

The common configuration provides an enable switch, adjustable hold-use duration and a list of excluded source rituals. Reload recipe data after changing recipe exclusions. Source recipes remain authoritative, so a removed or disabled ritual cannot be released from an old charm.

## Requirements

Available for **Minecraft 1.21.1 / NeoForge** and **Minecraft 1.20.1 / Forge**. Install **Goety** and its required dependencies on both the client and server.

An independent addon by **EndXiom**, licensed under **MIT**.
