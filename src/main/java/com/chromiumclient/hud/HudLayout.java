package com.chromiumclient.hud;

import java.util.LinkedHashMap;
import java.util.Map;

public final class HudLayout {
    public record Pos(int x, int y) {}
    public record Size(int width, int height) {}
    private static final Map<String, Pos> POS = new LinkedHashMap<>();
    private static final Map<String, Size> SIZE = new LinkedHashMap<>();

    static {
        register("stats", 8, 8, 118, 22);
        register("playtime", 8, 34, 118, 22);
        register("waypoint", 8, 60, 150, 24);
        register("locator", 180, 8, 220, 18);
    }

    private HudLayout() {}
    private static void register(String id, int x, int y, int w, int h) { POS.put(id, new Pos(x, y)); SIZE.put(id, new Size(w, h)); }
    public static Pos get(String id) { return POS.getOrDefault(id, new Pos(8, 8)); }
    public static void put(String id, int x, int y) { POS.put(id, new Pos(Math.max(0, x), Math.max(0, y))); }
    public static Size size(String id) { return SIZE.getOrDefault(id, new Size(100, 20)); }
    public static Map<String, Pos> all() { return Map.copyOf(POS); }
}
