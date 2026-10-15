# ChatSorter 1.3.3 -> Minecraft 26.1.2

STATUS: NOT PORTED / NOT COMPILE-VERIFIED.

The original source uses Yarn names from Minecraft 1.21.11. Minecraft 26.1.2 uses official mappings, so the source must be migrated. Changing Gradle coordinates alone is insufficient.

Known migration areas:
- MinecraftClient, Text, DrawContext, ChatHud, ChatHudLine, Screen, ChatScreen, widgets
- ChatHudMixin targets and injection method descriptors
- Fabric HUD rendering callback and rendering APIs
- SoundEvents and sound registry lookup
- text wrapping, input, and keyboard handling

The workflow captures compile-log.txt even when compilation fails. This is a diagnostic workspace, not a playable mod.
