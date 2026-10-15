# ChatSorter 1.3.3 -> Minecraft 26.1.2

STATUS: SOURCE API PORT (NOT COMPILE-VERIFIED, NOT GAME-TESTED).

This archive is a substantive adaptation of the 1.21.x ChatSorter sources to Mojang names / Minecraft 26.1.2, not merely a diagnostics workspace.

Changed:
- Yarn MinecraftClient/Text/ChatHud/DrawContext/Screen/EditBox APIs replaced by official Minecraft/Component/ChatComponent/GuiGraphicsExtractor types.
- Filters use ChatComponent.setVisibleMessageFilter(Predicate<GuiMessage>) and preserve original vanilla messages rather than clear-and-replay.
- ChatComponentMixin captures new player/system messages, ChatScreenMixin handles tabs/search/copy, and the ChatComponentAccess accessor reads GuiMessage.Line records.
- HUD unread badge ported to HudElementRegistry; sounds ported to BuiltInRegistries.SOUND_EVENT.getValue(Identifier).
- The settings / key setup screens use the updated 26.1.2 GUI and input signatures; original keyword rules, themes, logging, and notifications kept.
- The bundled GitHub Actions workflow uses Java 25 and uploads compilation logs regardless of success.

Verification: The ZIP and Java source trees have been checked locally, and class/method names were cross-checked against 26.1.2 Minecraft API reference. This environment has Java 21 and no external Gradle dependency access, hence a true 26.1.2 compile or in-game Mixin verification WAS NOT possible. Expect that follow-up compiler/runtime fixes may still be necessary.

IMPORTANT: The archive is source code, not a playable JAR. Upload all project files, including .github, to your ChatSorter2612 repository and run GitHub Actions. If it fails, send the FIRST build errors (or attach the compilation log artifact). If it builds, test on a local world before connecting to multiplayer.

## Follow-up compiler fixes (28-error build)
- Legacy Yarn-era ChatHudMixin / ChatHudAccess are not included in the port source; GitHub's browser uploader does **not** delete them automatically. The Gradle JavaCompile exclude prevents them from being compiled if left in the repository. **Please delete both files in GitHub** for a clean source tree.
- Changed Mojang OptionInstance getters/setters from getValue()/setValue() to get()/set().
- Changed old Window.getHandle() calls to Window.getWindow() (26.1.2 still uses GLFW).
- Not compile-verified here: rerun GitHub Actions and provide the next failure log if any.
