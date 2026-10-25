# Developer guide and design direction

**EnVy is the head developer of LandofDrunir.** EnVy leads the technical work and the island's creative direction, working with Sammy on the continuation of the original project and its artwork. Discuss changes to the island's identity, scale, art direction, or compatibility with EnVy before implementing a different direction.

These notes describe the intended player experience and how the current systems produce it. They are a guide for extending the code, not a claim that every original LandOfDrunir feature has been restored. Start with the [technical overview](../general/documentation.md) for the complete current scope.

## EnVy's direction

The island should feel mystical, alive, and slightly forbidden, like somewhere the player was not supposed to find. Golden foliage, familiar Minecraft silhouettes, quiet surroundings, coastal mist, and unreliable navigation work together. More effects or larger objects are not automatically better.

- **Keep the island focused.** The nominal coast is roughly 500 blocks across, with a single saved location per Overworld. The surrounding reef and ocean give it separation from ordinary terrain.
- **Make the forest feel natural.** The outer woods are dense and irregular, then thin toward a 10 block radius clearing at the center. Avoid straight rows, repeated spacing, or a uniform grid appearance. Roots and whole crowns must stay outside the clearing.
- **Keep walking space.** Use the two established rounded and branching birch styles with varied sizes, curves, forks, and crown widths. Leaves must leave three clear blocks above nearby dirt. More unusual branching shapes appear inland without replacing every familiar tree.
- **Keep rocks small and believable.** Natural spikes should be short and skinny, built with full blocks, stairs, and slabs using existing stone textures. Avoid oversized piles, smooth sculpted meshes, thin floating fragments, or lighting that breaks Minecraft's visual language. Player built modular boulders are a separate system and may form larger connected rocks.
- **Connect land and water.** Sand should sink into the water, blend toward yellow grass, and lead into reefs and seagrass. The shoreline should not look like a disconnected wall or a broad flat sand desert.
- **Keep the atmosphere intentional.** Coastal fog starts over the reef and warm beach and ends 16 blocks into full yellow grass. Compass and F3 distortion are visual only. Sun Hollies replace ordinary land plants. Natural mob generation is blocked; deer remains the only creature in the creative tab. Six sunken wrecks are deliberate exceptions to the ordinary structure ban.

## Where to make changes

All Java paths below are relative to `src/main/java/org/mcenvision/drunir/`.

| Area | Main source | Responsibility |
| --- | --- | --- |
| Content and initialization | `DrunirCollection`, `CollectionCreatures` | Load the content catalog and register blocks, items, armor, tabs, entities, and eggs. Internal class names are retained despite the public project rename. |
| Island outline and terrain | `worldgen/IslandRegion`, `IslandControl`, `IslandDensityFunction` | Seed selected outline, coast height profile, ocean belt, bounded density blending. |
| World lifecycle | `worldgen/IslandWorldgen`, `IslandSavedData`, `IslandContext` | Load the one anchor, bind generator state, surface rules, biome migration, command, and client snapshot. |
| Tree distribution | `worldgen/GroveLayout` | Deterministic irregular candidates, density gradient, inward variety, and central clearing checks. |
| Tree shape and placement | `worldgen/GoldenTreeShape`, `GoldenGroveFeature` | Connected trunks and branches, leaf support, whole crown validation, and slope clearance. |
| Shore and reefs | `ShorePalette`, `worldgen/ShoreBlendFeature`, `tools/worldgen/` | Stored shoreline tones, warm sand to grass transition, and aquatic feature templates. |
| Natural rocks and wrecks | `worldgen/BoulderFormation`, `IslandBoulderFeature`, `IslandWreckSites`, `SunkenShipFeature` | Small spike profiles and the six seeded submerged wreck sites. |
| Player built rocks | `boulder/`, `client/ConnectedBoulderModel` | Layer conservation, overflow placement, shared joins, collision, and exposed render surfaces. |
| Atmosphere and navigation | `worldgen/IslandVeil`, `IslandBoundaryPayload`, `IslandBoundaryState`, `client/IslandAtmosphere`, `MysticNavigation` | Share the boundary with clients and apply fog, compass, and debug display effects. |
| Original creature presentation | `client/DrunirGeometry`, `DrunirAnimations`, `DrunirClient` | Original part geometry, supplied walk cycles, model layers, and renderer registration. |
| Protection hooks | `worldgen/IslandSpawnProtection`, `mixin/` | Spawn restrictions and narrow connections to biome, density, surface, carver, structure, and compass behavior. |

The worldgen layout is deterministic. Candidate tree sites are jittered inside small cells and filtered by neighbor priority and clearance, so generation order does not create rows or chunk border discrepancies. Tree shapes are built separately from placement. Leaves have valid log distances; placement lifts the trunk when slopes would block the walking space. Rock profiles are generated once from a fixed seed and deduplicated under rotation before runtime selection.

## Sources of truth

`src/main/resources/assets/landofdrunir/content.json` owns catalog content and texture paths. `boulders.json` beside it owns modular rock materials, forms, sizes, and layer definitions. Original PNGs live under `textures/`. Preserve the supplied artwork and discuss intentional art changes with EnVy and Sammy.

`tools/generate_resources.py`, `boulder_resources.py`, and `landscape_resources.py` generate output under `src/generated/resources`. Worldgen templates are in `tools/worldgen/`. Change these sources and regenerate; direct edits to generated JSON will be overwritten. Generation rejects escaping paths and symlinks and removes obsolete generated JSON only after successful generation. Keep hand maintained resources in `src/main/resources`.

The server owns world generation, saved anchors, placement, and restrictions. Client classes stay isolated from common startup. The only custom payload is the bounded server to client island boundary snapshot, sent on login and data pack synchronization. It contains an enabled flag, center X and Z, and shape seed. It has no client mutation request and no per tick stream. The connection scoped client state clears on disconnect. Compasses and F3 use that state without changing authoritative positions or saved targets.

## Preserve these contracts

- Keep `landofdrunir` registry IDs, the `/drunir island` command, and `data/landofdrunir_golden_island.dat` stable. The anchor stores schema 1, `center_x`, `center_z`, and `shape_seed`. Do not delete or silently replace an invalid anchor.
- Keep the legacy `golden_birch_island` biome readable. Existing chunks migrate to `yellow_tree_forest` without changing their blocks. Hidden creature IDs, eggs, old slab rocks, and former configured tree IDs remain for compatibility.
- Keep the 10 block clearing, canopy clearance, supported leaves, continuous coast, and separation between tiny natural formations and expandable building rocks. Changing radius also affects grove sampling, wrecks, fog, sanctuary bounds, and generation tests.
- Keep collision and rendered boulder joins consistent. Joining must not create items, lose layers, overwrite occupied overflow space, or consume an item on failed placement. Neighbor geometry is bounded and must not request server chunks from lighting work.
- Keep display distortion on the client. Real coordinates, commands, networking, and world saves must stay accurate.
- Keep the original MIT license and credits. A public name change is not a registry migration or permission to replace the supplied artwork.

## Working on a change

Use Java 21, Python 3, and the existing wrapper and pinned NeoForge version. Run `./gradlew build`; do not independently bump Minecraft, NeoForge, ModDevGradle, or the wrapper to solve an unrelated issue. Test Gson stays matched to Minecraft. JUnit platform and Jupiter upgrades need to be coordinated.

The build checks resources and Java behavior, including island continuity, shore descent, tree density and clearing, leaf support, wreck placement, navigation, model channels, boulder joins, and item conservation. Unit test runtime files belong beneath `build/test-runtime`. Add focused regressions for changed behavior.

For terrain edits, inspect a new disposable world because existing chunks retain their blocks. For geometry, textures, fog, or renderer edits, also inspect a matching client. Confirm the forest is walkable from ground level as well as attractive from above. For persistence changes, verify a save and reload without relocating the anchor. Update affected documentation with the actual behavior, and send contributions to the retained `neoforge-1.21.1/0.1.0` product branch through a pull request.

Return to the [documentation index](../README.md). Use [private reporting](../security/documentation.md) for exploitable issues.
