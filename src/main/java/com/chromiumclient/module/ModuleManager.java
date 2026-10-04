package com.chromiumclient.module;

import com.chromiumclient.ChromiumClient;
import com.chromiumclient.util.Config;
import com.chromiumclient.util.Reflect;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ModuleManager {
    private final List<Module> all = new ArrayList<>();

    public ModuleManager() {
        add("FPS Counter", Module.Category.HUD, "Compact Dawn-style FPS counter", true);
        add("Ping Counter", Module.Category.HUD, "Current multiplayer latency", true);
        add("Playtime", Module.Category.HUD, "Time spent in the current session", true);
        add("Locator Bar", Module.Category.HUD, "Compass-style nearby player locator", true);
        add("Waypoint HUD", Module.Category.HUD, "Distance and direction to selected waypoint", true);
        add("Custom Crosshair", Module.Category.HUD, "Chromium crosshair with multiple shapes", true);

        add("Auto Sprint", Module.Category.MOVEMENT, "Always sprint while moving forward", true);
        add("Freelook", Module.Category.MOVEMENT, "Hold Left Alt to look around independently", true);

        add("Fullbright", Module.Category.VISUAL, "Uniform maximum vanilla lightmap brightness", true);
        add("Low Fire", Module.Category.VISUAL, "Lower first-person fire overlay", true);
        add("Low Shield", Module.Category.VISUAL, "Lower only the first-person shield", true);
        add("No Particles", Module.Category.VISUAL, "Suppress client particle spawning", false);
        add("No Fog", Module.Category.VISUAL, "Push environmental/render fog away", false);

        add("Chat Macros", Module.Category.UTILITY, "F6/F7/F8 send configurable messages", true);
        add("Waypoints", Module.Category.UTILITY, "B adds a waypoint; N cycles waypoints", true);
        add("HUD Editor", Module.Category.UTILITY, "Right Ctrl moves Chromium HUD blocks", true);

        add("AppleSkin", Module.Category.INTEGRATIONS, "Official AppleSkin integration", true);
        add("Color Saturation", Module.Category.INTEGRATIONS, "Official ColorSaturation integration", true);
        add("Minimap", Module.Category.INTEGRATIONS, "Xaero's Minimap integration", true);
        add("3D Skin Layers", Module.Category.INTEGRATIONS, "Official 3D Skin Layers integration", true);
        add("Chat Heads", Module.Category.INTEGRATIONS, "Official Chat Heads integration", true);
        add("Shulker Tooltip", Module.Category.INTEGRATIONS, "Official Shulker Box Tooltip integration", true);
        add("Map Tooltip", Module.Category.INTEGRATIONS, "Maptip integration", true);
        add("MCTiers Tier Tagger", Module.Category.INTEGRATIONS, "Official MCTiers TierTagger + ukulib", true);
        add("TNT Countdown", Module.Category.INTEGRATIONS, "Official TNT Countdown integration", true);
    }

    private void add(String n, Module.Category c, String d, boolean on) { all.add(new Module(n, c, d, on)); }
    public List<Module> all() { return Collections.unmodifiableList(all); }
    public List<Module> in(Module.Category c) { return all.stream().filter(m -> m.category == c).toList(); }
    public Module get(String name) { return all.stream().filter(m -> m.name.equals(name)).findFirst().orElse(null); }
    public boolean on(String name) { Module m = get(name); return m != null && m.enabled; }
    public void toggle(Module m) { m.toggle(); Config.save(this); }

    public void tick(Minecraft mc) {
        if (mc == null || mc.player == null) return;
        if (on("Auto Sprint") && Reflect.keyDown(mc.options, "keyUp")) {
            try { mc.player.setSprinting(true); } catch (Throwable ignored) {}
        }
    }
}
