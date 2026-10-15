# ChatSorter 1.3.3 / Minecraft Java 26.1.2 — Source Port (beta)

A Fabric client-side chat sorting mod by **mlpgod** for **MigosMC**.

This source includes the ported versions of tabs (General, DM, Spy, Console, All), search, keyword alerts, notification log, configurable notification sound/volume, custom themes, and draggable/resizable chat and copy functions.

Minecraft 26.1.2 requires Java 25, Fabric Loom 1.18.2, and official Mojang names. This mod no longer clears/replays the vanilla chat on tab switch; it applies a message visibility filter to native ChatComponent.

**Port status: UNVERIFIED.** Compilation has not been run against Minecraft 26.1.2 here; the GitHub Actions workflow must still compile it. A successful build must be followed by a manual Minecraft launch to ensure Mixin compatibility.

### How to build without a local installation

1. Upload the full contents of this archive to your ChatSorter2612 GitHub repository, including `.github/workflows/build.yml`.
2. Go to **Actions** > **Build ChatSorter 26.1.2** > **Run workflow**.
3. If success, download the `ChatSorter-1.3.3-mc26.1.2` artifact and use the regular built JAR (not the `-sources` JAR) with Fabric Loader/Fabric API.
4. If failure, download `ChatSorter-26.1.2-compile-log` and share the first errors to diagnose them.

Review `PORT_STATUS.md` for exact changes and limitations.
