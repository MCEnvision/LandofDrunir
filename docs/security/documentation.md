# Security and reporting

Install mod JARs only from a source you trust. Minecraft mods execute with the permissions of the game process. Keep world backups and install the same build on clients and servers.

Report exploitable defects through the repository's [private vulnerability reporting form](https://github.com/MCEnvision/LandofDrunir/security/advisories/new). Include the Minecraft, NeoForge, Java, and mod versions, reproduction steps in a disposable world, and the expected and observed results. Remove access tokens, private server addresses, personal information, and unrelated logs before sharing evidence. Use [Issues](https://github.com/MCEnvision/LandofDrunir/issues) for ordinary bugs and feature requests.

The current source targets Minecraft 1.21.1 and NeoForge 21.1.259. Earlier experimental builds may lack fixes present in the current product branch. No response time or compatibility with arbitrary mod combinations is guaranteed.

The only custom network payload is a bounded server to client snapshot for island presentation. Clients do not send custom gameplay mutation packets. Actual positions, world generation, block placement, and spawn protections remain server controlled. The fog, compass, and debug coordinate effects do not grant permissions or change saved player coordinates.

Resource generation is a developer build operation. It writes only inside `src/generated/resources`, rejects output symlinks and escaping paths, and removes obsolete generated JSON after successful generation. Keep hand maintained resources under `src/main/resources`.
