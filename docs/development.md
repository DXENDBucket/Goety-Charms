# Goety Charms implementation and validation

## Boundary

Independent shared-source project. NeoForge uses Goety 3.2.2, Java21 and NeoForge21.1.250. Forge uses Goety2.5.59.2, Java17 and Forge47.4.0. No Ars, Conflux or Nouveau source/build dependency is present. Local upstream JARs are ignored development dependencies, never embedded.

`RitualCharms` maps native factory identities to summon/conversion adapters and accepts exact native execution classes registered under other factory IDs. Explicit adapters are required for custom execution subclasses. Adapter registration must occur during setup on both sides; native metadata still supplies the target entity kind.

## Derivation and altar lifecycle

A narrow RecipeManager.apply RETURN injection rebuilds derived recipes from the already decoded, condition-filtered recipe collection. Generated IDs are `goety_charms:sealing/<source namespace>/<source path>`; existing IDs win, and SealingRecipe is never recursively wrapped. The original recipe remains unchanged. The native ritual recipe type is retained, with a dedicated nested-native-source serializer for client synchronization and data codecs.

The source catalyst becomes the first additional ingredient. Blank charms become the activation ingredient. Native dark altar code handles ingredient predicates, item remainders, soul charges, full duration, research, sacrifice, interrupted progress and save/load. Source selection is stack-local. The altar interception consumes a selection interaction without starting a ritual; item useOn provides the secondary-use fallback when another offhand item bypasses block interaction.

During sealing, isSummoning/isConversion remain false so no conversion creature or summon capacity is required. Native completion callbacks see the source entity kind only inside a try/finally capture scope, preserving callbacks such as Sabbath's soul-fire consumption at the preparation site. The first actually consumed stack is retained as a native catalyst snapshot; successful completion replaces the altar blank with a charged charm. No material is minted from recipe display alternatives during real crafting.

## Release

A one-second native item-use action supplies the actual clipped block/entity hit. Conversion requires a live matching Mob; player targets are not substituted. Native capacity, loaded position, world border and collision checks precede execution. The public `CharmRelease.activate` accepts a real player and explicit origin/target so a later optional Ars integration can call the same path.

Release constructs a non-ticking, unplaced native altar context. It is not installed as a block, does not tick and does not process inventories or payment. The original registered ritual object's finish method performs spawning/conversion, including tame/no-variant/equipment behavior and native utility entities. A narrowly scoped base Ritual.finish cancellation prevents the already-paid ritual-type completion hooks from running twice; source subclass effects remain native.

A caller-owned thread-local scope observes entity-join attempts and checks accepted entities after native completion. Conversion success must match its declared result type; unrelated visual lightning does not count. Summoning also accepts living variants. An unsuccessful or canceled spawn does not consume the charm; native partial conversion side effects cannot be transactionally rolled back. Source/catalyst and kind are revalidated against current server recipes. No second resource fee is charged. Source research is checked at sealing and is not demanded again from the later user.

Apostle release uses the native warning flag in the Nether, rejects Peaceful, and retains the original SummonApostle entity's delayed visuals and boss spawn. No forced Nether/biome/night restriction is invented. Source recipe configuration/exclusion can disable release.

## Verification

Both platforms provide six compile-checked native-path cases: derived recipes/metadata, sealing completion + item save/network data, owned native summon, conversion + wrong target retention, removed source retention, derived-recipe network synchronization. The direct completion fixture does not claim to test an entire running altar payment cycle.

Only compilation and packaging are authorized for this batch. No client, dedicated server or GameTest was started. Production Forge reobfuscation, declared refmap and RecipeManager.apply SRG target are checked by verifyRelease. Final artifact fingerprints and exact results are recorded separately in build-receipt.json. Actual UI, item animation, full altar operation, Apostle presentation and addon coexistence remain unverified.

## Commands

NeoForge, Java21:

```powershell
./gradlew.bat :compileGameTestJava :jar :sourcesJar --offline --max-workers=1 --no-parallel --no-daemon
```

Forge directory, Java17:

```powershell
./gradlew.bat :compileGameTestJava :jar :sourcesJar :verifyRelease --offline --max-workers=1 --no-parallel --no-daemon
```

A runtime check can later use :runGameTestServer, after the user authorizes a game JVM and memory is available. GameTest passing would not prove client visuals.
