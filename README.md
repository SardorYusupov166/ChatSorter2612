# ChatSorter 1.3.3 — Minecraft 26.1.2 migration workspace (NOT A WORKING MOD)

This archive is a **migration workspace**, not a tested/working release. Original 1.21.11 Java sources are preserved as a reference and are NOT yet migrated to the 26.1.2 official Minecraft API. Running GitHub Actions is expected to expose Java compilation errors until the source files and Mixins are ported.

Updated: Minecraft 26.1.2, Fabric Loom non-remapping plugin, Java 25, Fabric API 0.152.1+26.1.2, GitHub Actions Gradle 9.7.1. Versions must be checked against published artifacts.

Next steps: migrate Yarn references to official Mojang names (ideally first run Loom `migrateMappings` in a 1.21.11 staging project), adapt Minecraft API and Fabric API calls, rewrite and verify ChatHud/ChatScreen Mixins, compile and test the actual game.

Do NOT install this archive as a mod. It contains source code only.
