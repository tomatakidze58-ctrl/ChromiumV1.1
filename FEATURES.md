# ChromiumClient 26.2 feature matrix

ChromiumClient uses a small built-in core for features that are simple and stable to own directly, and official companion mods for large renderer/UI systems that already have mature 26.2 implementations.

## Built into ChromiumClient

| Feature | Implementation |
|---|---|
| Fullbright | Uniform maximum client lightmap brightness |
| Low Fire | Adjustable first-person fire overlay offset |
| Low Shield | Moves only the first-person shield |
| No Particles | Client particle suppression |
| No Fog | Client fog suppression |
| FPS Counter | Compact Dawn-style HUD widget |
| Ping Counter | Multiplayer latency HUD widget |
| Freelook | Hold Left Alt |
| Custom Crosshair | Multiple Chromium crosshair shapes |
| Auto Sprint | Always sprint while moving forward |
| Waypoints | Lightweight fallback waypoint system |
| Chat Macros | F6/F7/F8 configurable chat messages |
| Playtime | Session timer |
| Locator Bar | Nearby-player direction bar |
| HUD Editor | Right Ctrl |
| Module GUI | Right Shift |

## Official companion integrations

| Feature | Companion project | Reason kept external |
|---|---|---|
| AppleSkin | AppleSkin | Mature hunger/saturation overlay and tooltips |
| Color Saturation | ColorSaturation | Shader/post-processing implementation |
| Minimap + advanced waypoints | Xaero's Minimap | Mature minimap; all-rights-reserved project |
| 3D Skin Layers | 3D Skin Layers | Mature player renderer integration |
| Chat Heads | Chat Heads | Mature chat parsing/rendering |
| Shulker Box Tooltip | Shulker Box Tooltip | Mature container preview |
| Map Tooltip | Maptip | Mature filled-map tooltip preview |
| MCTiers tags | Tier Tagger + ukulib | Official MCTiers-supported tier display |
| TNT Countdown | TNT Countdown | Mature fuse renderer |

Run `install-companions.ps1` to download compatible Fabric 26.2 releases from Modrinth directly into `.minecraft/mods`.

ChromiumClient does not rebrand or redistribute restricted third-party code/assets. Each companion keeps its own license and project identity.
