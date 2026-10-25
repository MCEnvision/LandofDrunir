# LandofDrunir technical overview

LandofDrunir is the NeoForge 1.21.1 continuation of [LandOfDrunir](https://github.com/sammmmy1628/LandOfDrunir). EnVy is the head developer and directs the island, gameplay, and technical work. Sammy is the original project creator and artwork collaborator supplying the original artwork. This overview describes the current build, not every feature of the original project.

## Installation and compatibility

Use Minecraft 1.21.1, Java 21, and NeoForge 21.1.259 or a later 21.1 release. No other mod dependencies are required. Install the same `landofdrunir-neoforge-1.21.1-0.1.0.jar` on clients and servers. Remove the earlier JAR before replacing it.

The mod ID is still `landofdrunir`. Project branding and JAR names do not change registry IDs, world data, or command names. Do not install two mods with this ID together. Back up worlds before updating. Existing generated chunks are not rebuilt; earlier large islands remain large in those chunks. New terrain uses the current boundaries, so partially explored older islands can have a transition where generation resumes. A fresh disposable world is the clearest way to inspect the current layout.

## Yellow Tree Forest

`landofdrunir:yellow_tree_forest` is one connected island per supported Overworld. Its nominal coast is roughly **500 blocks across**, using a 256 block base radius and an irregular outline. This measurement describes the landmass, not the entire surrounding ocean. The forest begins 32 blocks inside the nominal coast. The submerged sandy shelf continues up to 96 blocks beyond it. The sanctuary extends 512 blocks from the center, with terrain blending completely back into the ordinary world by 768 blocks.

The island uses yellow grass over regular dirt, vanilla birch logs, and custom yellow leaves. Irregular tree sites create dense outer woodland and a progressively more open interior. A 10 block radius central clearing excludes roots, branches, and overhanging leaves. Two established tree styles, rounded and branching, mix small, medium, and tall variants. Trunks range approximately from 5 to 22 blocks before slope adjustments. Branching shapes become more common inland. Foliage starts above three clear blocks over the dirt, including nearby slopes, and retains normal leaf decay after its supporting logs are removed.

Sun Hollies replace naturally generated land flowers and grass patches and emit light level 4. A 24 block sand to gold transition softens the forest edge. Reefs, short and tall seagrass, and sea pickles break up the submerged shore. Six deliberately submerged vanilla shipwreck variants surround the sandy shelf and use standard supply, treasure, and map chest loot tables.

Natural rock formations use the existing Blueschist and sandstone textures. There are 128 distinct short spike profiles, with a maximum three by three block footprint and approximately 1.5 to 2.5 blocks exposed above ground. Buried shoulders support a narrow tip; full blocks, slabs, and stairs supply normal Minecraft shading and collision. Loose stones have their own placement feature.

### Atmosphere and protection

Fog covers the reef waters, warm beach, and sand transition, fading out over 16 blocks after full yellow grass begins. Drifting mist and muted sky and water colors provide the coastal atmosphere. Underwater, lava, and status effect fog retain their normal handling. Particle settings can reduce visible mist.

Ordinary, lodestone, and recovery compasses spin inside the sanctuary. F3 coordinate lines drift together, while unrelated debug information remains available. These are client presentation effects. They do not teleport players, alter item targets, or change server coordinates. Navigation returns to normal outside the sanctuary or in another dimension.

Natural and spawner generated mobs are blocked, including fish and drowned. Deer is available for intentional placement, not natural population. Commands, spawn eggs, dispensers, buckets, breeding, and conversion remain available; existing mobs can enter from outside. Caves and ordinary structure generation are excluded within the protected area. The six wrecks are an explicit feature exception. Existing structures, mobs, and terrain are not deleted.

### Location and saved worlds

`/drunir island` displays the saved center without operator permission. Vanilla `/locate biome landofdrunir:yellow_tree_forest` also recognizes the biome within its search range. Standard, Large Biomes, and Amplified Overworld noise generators are supported. Nether, End, superflat, and unrelated custom generators are unaffected.

The first location is seed selected, initially around 4,000 to 6,200 blocks from the origin. Existing region files move the bounded search outward to avoid overwriting explored terrain. The center and shape seed persist in `data/landofdrunir_golden_island.dat`, schema 1. Keep this file with the world. An invalid existing record stops loading rather than silently choosing another island; restore a valid backup instead of deleting the anchor.

The old biome ID `landofdrunir:golden_birch_island` remains readable and is remapped to `yellow_tree_forest` as existing chunks load. Blocks and unrelated biome entries are preserved. Shore and water IDs remain `golden_birch_shore` and `golden_birch_waters`.

## Creative content

All five tabs use the LandofDrunir name. Content is obtained through creative inventory, `/give`, and `/summon`.

| Content | Current behavior |
| --- | --- |
| Blocks | 154 catalog blocks, including decorative plants, logs, doors, trapdoors, stones, and five extra grass colors. Saplings are decorative. Decorative sand does not fall. |
| Items | 84 catalog items, including materials, edible food, standard tier tools, swords, and lances using sword behavior. |
| Armor | Drunite, Galeon, and Plasmite armor use their supplied worn textures. Goblin Drip is cosmetic with no protection. Living armor remains inventory artwork pending worn textures. |
| Creatures | Only the Deer Spawn Egg appears in the tab. All 11 passive creature IDs and eggs remain registered for saves and commands. The separate Magmafiend fireball remains summonable. Advanced original behaviors are not implemented. |
| Gallery | One flat sample per source PNG, covering all 296 textures. Trim palettes are previews, not smithing materials. Samples use `landofdrunir:texture/<original path without .png>`. |
| Building rocks | 60 boulder choices across three materials, five sizes, and four forms, plus matching rock slabs and stairs. |

Peach, Coral, Violet, Mint, and Lime Grass Blocks use their supplied textures on every face. They are creative building blocks and neither spread nor generate naturally. Thirty three natural and decorative catalog blocks use four equally weighted horizontal texture rotations. Minecraft selects these consistently by block position; they do not animate or directly depend on the world seed.

Boat, sign, hanging sign, and candle artwork currently appears as inventory items rather than functional placements. There are no crafting recipes, custom ore placement, progression system, custom dimension, or original special abilities in this build.

### Building with boulders

Modular boulders use Blueschist, Brown Sandstone, or Dark Brown Sandstone in Rounded, Crag, Flat, and Cluster forms. Tiny, Small, Medium, Big, and Huge pieces are 2, 4, 8, 12, and 16 pixels high. Add a matching material to grow a placed rock; excess height continues into replaceable space directly above. Blocked overflow leaves both the rock and held item unchanged. Sneak to place a separate piece.

Adjacent pieces of the same material fill their shared seams, including supported vertical joins and four piece junctions. Removing a neighbor restores the exposed edge. Different materials remain separate. Selection, collision, and rendering share the same geometry. Waterlogging is supported. Breaking pieces returns their height as Tiny pieces of the same form and material. Old slab boulder IDs remain compatible.

## Build and contribute

Use Java 21, Python 3, and the checked in Gradle Wrapper:

```sh
./gradlew build
./gradlew runClient
./gradlew runServer
```

`build` produces `build/libs/landofdrunir-neoforge-1.21.1-0.1.0.jar`. It runs Java tests, Python resource tool tests, generation, and validation. Unit test runtime files stay under `build/test-runtime`. Set `JAVA_HOME` if Java 21 is not the system default. Development runtimes use disposable worlds; server use requires accepting the Minecraft EULA.

`./gradlew generateResources` updates generated models, blockstates, translations, tags, loot, and world generation data. Edit source catalogs, templates, or generators instead of generated JSON. `./gradlew validateResources` checks references, identifiers, models, textures, and coverage. See the [developer guide](../dev/documentation.md) for ownership, source locations, and change boundaries.

There are no public configuration files or supported extension API yet. Weekly dependency updates retain the pinned platform and Minecraft matched Gson versions. Major JUnit upgrades require coordinated work. Report ordinary defects through [Issues](https://github.com/MCEnvision/LandofDrunir/issues) and exploitable issues through [private reporting](../security/documentation.md).

## Credits and license

Sammy supplies the original artwork and collaborates with EnVy on the continuation. Original assets, model geometry, and animations come from [LandOfDrunir](https://github.com/sammmmy1628/LandOfDrunir). The root [MIT license](../../LICENSE), including the original Christofmeg copyright notice, remains unchanged and is included in the JAR. Return to the [documentation index](../README.md) or [project overview](../../README.md).
