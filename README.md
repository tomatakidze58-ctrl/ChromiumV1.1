# ChromiumClient 26.2

A fresh Fabric 26.2 client project with a monochrome Chromium identity, compact Dawn-inspired HUD blocks, and the new gray **Cr** branding.

## Built into the ChromiumClient jar

- Fullbright (uniform vanilla lightmap brightness)
- Low Fire with adjustable offset
- Low Shield that only moves the shield
- No Particles
- No Fog
- FPS counter
- Ping counter
- Playtime
- Freelook — hold Left Alt
- Custom crosshair — four shapes
- Auto Sprint always while moving forward
- Built-in lightweight waypoints — B adds, N cycles
- Locator Bar for nearby players
- Chat macros — F6/F7/F8
- Right Shift Chromium module GUI
- Right Ctrl draggable HUD editor

See `FEATURES.md` for the full built-in/integration feature matrix.

## Official companion integrations

For the features where the mature 26.2 mod is safer and/or its license should not be rebranded, Chromium detects the official installed mod:

- AppleSkin
- ColorSaturation
- Xaero's Minimap (also provides the full minimap/waypoint experience)
- 3D Skin Layers
- Chat Heads
- Shulker Box Tooltip
- Maptip
- MCTiers TierTagger + ukulib
- TNT Countdown

Run `install-companions.ps1` on Windows to download the current Fabric 26.2 builds directly from Modrinth. Chromium does not redistribute their jars.

## Build

Requirements: Java 25. The included GitHub Actions workflow uses Gradle 9.7.1.

```text
gradle build
```

Use `build/libs/ChromiumClient-1.0.0.jar`, not the `-sources.jar` file.

## Controls

- Right Shift — module GUI
- Right Ctrl — HUD editor
- Left Alt — hold Freelook
- B — add current position as waypoint
- N — cycle selected waypoint
- F6 / F7 / F8 — chat macros

Macro text can be edited in `config/chromiumclient.properties` after first launch.

## Note

No software project can honestly guarantee zero bugs across every server, GPU and mod combination. This source keeps the core small and uses official companion mods for the complex integrations specifically to reduce breakage.
