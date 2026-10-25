# LandofDrunir

LandofDrunir continues the original [LandOfDrunir project](https://github.com/sammmmy1628/LandOfDrunir) on **Minecraft 1.21.1 and NeoForge**. **EnVy is the head developer**, working with **Sammy**, the original project creator and artwork collaborator who supplied the original artwork for this continuation.

The current focus is a mystical golden island, a useful creative building collection, and a maintainable foundation for continued development. This build does not yet reproduce every feature of the original mod.

## Play

Use Java 21 and NeoForge 21.1.259 or a later release in the 21.1 series. Install the same `landofdrunir-neoforge-1.21.1-0.1.0.jar` in the client and server `mods` folders. Replace older JARs rather than keeping both. The mod ID remains `landofdrunir`, so another build with that ID cannot be installed alongside it.

- **Yellow Tree Forest** is one seed selected island per supported Overworld, roughly **500 blocks across at its nominal coast**. Beaches, reefs, and the surrounding ocean extend beyond the forest.
- The outer woodland is dense and irregular, thinning toward a **10 block radius clearing** at the center. Rounded and branching golden birches have varied heights and keep three clear blocks beneath their leaves.
- Sun Hollies, warm sand, reefs, seagrass, six sunken wrecks, and short stone spikes give the island its character. Coastal fog fades out 16 blocks into full yellow grass. Compasses spin and F3 coordinates drift inside the sanctuary; actual player positions stay unchanged.
- Ordinary structures, caves, and natural mob spawns are excluded from the sanctuary. The six wrecks are intentional exceptions. Deer is the only creature shown in the creative tab; it does not currently spawn naturally.
- Creative tabs provide decorative blocks, five extra grass colors, items, wearable armor, deer, a texture gallery, and 60 modular boulder choices. Matching boulders join and stack for player builds.

Use `/drunir island` to find the saved island center. Standard, Large Biomes, and Amplified Overworld generators are supported. Terrain changes affect new chunks only. Already generated islands keep their old size and blocks; use a fresh disposable world to inspect the smaller island.

Survival recipes, custom ore generation, another dimension, progression, and the original creatures' advanced abilities are not implemented in this build. Some inventory artwork is available as preview items rather than fully functional content. See the [technical overview](docs/general/documentation.md) for the exact scope.

## Develop

With Java 21 and Python 3 installed:

```sh
./gradlew build
```

The JAR is written to `build/libs/`. The build runs Java tests, resource tool tests, resource generation, and validation. Use `./gradlew runClient` or `./gradlew runServer` for a development runtime. Work in disposable worlds and follow the Minecraft EULA for a server.

Start with the [documentation index](docs/README.md), [developer guide and EnVy's design direction](docs/dev/documentation.md), and [security reporting guidance](docs/security/documentation.md). Report ordinary bugs through [Issues](https://github.com/MCEnvision/LandofDrunir/issues).

## Credits and license

EnVy leads development and the island's design direction. Sammy supplies the original project's artwork and works with EnVy on this continuation. Original textures, model geometry, and animations originate from [LandOfDrunir](https://github.com/sammmmy1628/LandOfDrunir). The original [MIT license](LICENSE), including its 2024 Christofmeg copyright notice, is retained and packaged with the mod.
