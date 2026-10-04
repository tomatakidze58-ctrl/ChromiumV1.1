package com.chromiumclient.waypoint;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/** Lightweight built-in waypoint fallback. Xaero integration can provide the full minimap waypoint UI. */
public final class WaypointManager {
    public record Waypoint(String name, double x, double y, double z) {}
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("chromiumclient-waypoints.properties");
    private static final List<Waypoint> POINTS = new ArrayList<>();
    private static int selected;

    private WaypointManager() {}

    public static void load() {
        POINTS.clear();
        if (!Files.exists(FILE)) return;
        Properties p = new Properties();
        try (InputStream in = Files.newInputStream(FILE)) {
            p.load(in);
            int count = Integer.parseInt(p.getProperty("count", "0"));
            for (int i = 0; i < count; i++) {
                String[] v = p.getProperty("p." + i, "").split(",", 4);
                if (v.length == 4) POINTS.add(new Waypoint(v[0], Double.parseDouble(v[1]), Double.parseDouble(v[2]), Double.parseDouble(v[3])));
            }
        } catch (Exception ignored) {}
    }

    public static void addCurrent(Minecraft mc) {
        if (mc.player == null) return;
        String name = "Point " + (POINTS.size() + 1);
        POINTS.add(new Waypoint(name, Math.floor(mc.player.getX()), Math.floor(mc.player.getY()), Math.floor(mc.player.getZ())));
        selected = POINTS.size() - 1;
        save();
    }

    public static void cycle() {
        if (!POINTS.isEmpty()) selected = (selected + 1) % POINTS.size();
    }

    public static Waypoint selected() {
        if (POINTS.isEmpty()) return null;
        selected = Math.max(0, Math.min(selected, POINTS.size() - 1));
        return POINTS.get(selected);
    }

    public static List<Waypoint> all() { return List.copyOf(POINTS); }

    private static void save() {
        Properties p = new Properties();
        p.setProperty("count", Integer.toString(POINTS.size()));
        for (int i = 0; i < POINTS.size(); i++) {
            Waypoint w = POINTS.get(i);
            p.setProperty("p." + i, w.name() + "," + w.x() + "," + w.y() + "," + w.z());
        }
        try {
            Files.createDirectories(FILE.getParent());
            try (OutputStream out = Files.newOutputStream(FILE)) { p.store(out, "ChromiumClient waypoints"); }
        } catch (Exception ignored) {}
    }
}
