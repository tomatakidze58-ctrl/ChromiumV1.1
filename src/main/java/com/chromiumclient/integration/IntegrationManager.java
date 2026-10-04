package com.chromiumclient.integration;

import net.fabricmc.loader.api.FabricLoader;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Detects official companion mods. Chromium does not copy or redistribute
 * restricted third-party sources; it integrates with their installed jars.
 */
public final class IntegrationManager {
    public record Integration(String label, String modId, String purpose) {}

    private static final Map<String, Integration> ALL = new LinkedHashMap<>();
    static {
        add("AppleSkin", "appleskin", "Food/saturation HUD and food tooltips");
        add("Color Saturation", "colorsaturation", "GPU color saturation controls");
        add("Minimap", "xaerominimap", "Terrain minimap and waypoint system");
        add("3D Skin Layers", "skinlayers3d", "3D player skin second layers");
        add("Chat Heads", "chat_heads", "Player heads beside chat messages");
        add("Shulker Tooltip", "shulkerboxtooltip", "Preview shulker contents in tooltips");
        add("Map Tooltip", "maptip", "Preview filled maps in tooltips");
        add("MCTiers Tier Tagger", "tiertagger", "Official MCTiers nametag tiers");
        add("TNT Countdown", "tntcountdown", "Fuse countdown above primed TNT");
    }

    private IntegrationManager() {}
    private static void add(String label, String id, String purpose) { ALL.put(label, new Integration(label, id, purpose)); }
    public static Map<String, Integration> all() { return Map.copyOf(ALL); }
    public static boolean loaded(String label) {
        Integration i = ALL.get(label);
        return i != null && FabricLoader.getInstance().isModLoaded(i.modId());
    }
    public static String status(String label) { return loaded(label) ? "ACTIVE" : "INSTALL"; }
}
