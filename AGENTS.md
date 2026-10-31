# Goety Charms

Independent Goety ritual-sealing addon. Mod ID goety_charms. Goety is required; Ars/Conflux are not required and are not modified.

- Shared policy and native lifecycle calls belong in common/. Loader, recipe codecs, item components/NBT and event differences belong in platform directories.
- Derive sealing recipes from actually loaded native summon/convert factories across namespaces. Never clone their entity spawning or conversion implementations. Custom execution factories need explicit adapters.
- Preserve research, source ritual environment, native ingredient predicates/remainders, soul charges, duration and sacrifice requirements while sealing. Defer target and summon capacity checks to release. Source recipe IDs remain authoritative on release.
- Only release after a full item-use charge; failed release keeps the charm. Successful survival release consumes one. Preserve native variants, owner, mob equipment and summon lifetime through native Ritual.finish.
- Thread-local paid-completion scopes must restore in finally. Native completion callbacks are performed once at sealing, not again at release. No fake player, invisible inventory storage block, ticking fake altar or substitute world.
- Datapack reload rebuilds derived recipes; synchronize their serializer to clients and use real Goety JEI ritual recipes.
- Teleport and locate results also use native executors. Preserve the actual bound waystone; teleport has only native destination/dimension rules, no extra landing policy. Location starts native asynchronous search at release, never at sealing, and delivers the native map. Keep the charm with the player until successful delivery; cancel pending searches on invalid item/source, disconnect, dimension change or server stop.
- Art follows the established pixel_assets.py pipeline. Retain source/provenance records; shared silhouettes distinguish blank/summoning/conversion through central seals.
- Build only unless the user authorizes runtime tests. No automatic remote publishing or push. Shared pack deployment is a coordinated action after game exit.
