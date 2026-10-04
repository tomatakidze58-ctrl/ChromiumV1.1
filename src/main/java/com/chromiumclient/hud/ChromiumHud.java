package com.chromiumclient.hud;

import com.chromiumclient.ChromiumClient;
import com.chromiumclient.util.ChromiumSettings;
import com.chromiumclient.util.Reflect;
import com.chromiumclient.waypoint.WaypointManager;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.player.Player;

import java.util.Locale;

/** Compact gray/black HUD inspired by the clean density of PvP clients. */
public final class ChromiumHud {
    private static final int BG = 0xB8111215;
    private static final int BORDER = 0x884A4F58;
    private static final int TEXT = 0xFFF0F2F5;
    private static final int MUTED = 0xFF9DA3AD;
    private static final int ACCENT = 0xFFC4C9D1;

    private ChromiumHud() {}

    public static void render(GuiGraphicsExtractor g, DeltaTracker delta) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        float s = Math.max(0.75f, Math.min(1.5f, ChromiumSettings.hudScale));

        if (ChromiumClient.MODULES.on("FPS Counter") || ChromiumClient.MODULES.on("Ping Counter")) {
            drawAt(g, "stats", s, () -> drawStats(g, mc));
        }
        if (ChromiumClient.MODULES.on("Playtime")) drawAt(g, "playtime", s, () -> drawPlaytime(g, mc));
        if (ChromiumClient.MODULES.on("Waypoint HUD")) drawAt(g, "waypoint", s, () -> drawWaypoint(g, mc));
        if (ChromiumClient.MODULES.on("Locator Bar")) drawAt(g, "locator", s, () -> drawLocator(g, mc));
        if (ChromiumClient.MODULES.on("Custom Crosshair")) drawCrosshair(g, mc);
    }

    private static void drawAt(GuiGraphicsExtractor g, String id, float scale, Runnable draw) {
        HudLayout.Pos p = HudLayout.get(id);
        g.pose().pushMatrix();
        g.pose().translate(p.x(), p.y());
        g.pose().scale(scale, scale);
        draw.run();
        g.pose().popMatrix();
    }

    private static void panel(GuiGraphicsExtractor g, int w, int h) {
        if (ChromiumSettings.hudBackground) {
            g.fill(0, 0, w, h, BG);
            g.outline(0, 0, w, h, BORDER);
        }
    }

    private static void drawStats(GuiGraphicsExtractor g, Minecraft mc) {
        String left = ChromiumClient.MODULES.on("FPS Counter") ? "FPS " + fps(mc) : "";
        String right = ChromiumClient.MODULES.on("Ping Counter") ? "PING " + ping(mc) + "ms" : "";
        String text = left + (!left.isEmpty() && !right.isEmpty() ? "   " : "") + right;
        int w = Math.max(82, mc.font.width(text) + 14);
        panel(g, w, 18);
        g.text(mc.font, "Cr", 6, 5, ACCENT, true);
        g.text(mc.font, text, 24, 5, TEXT, false);
    }

    private static void drawPlaytime(GuiGraphicsExtractor g, Minecraft mc) {
        long total = Math.max(0L, (System.currentTimeMillis() - ChromiumClient.SESSION_START) / 1000L);
        long h = total / 3600L, m = (total % 3600L) / 60L, s = total % 60L;
        String time = h > 0 ? String.format(Locale.ROOT, "%02d:%02d:%02d", h, m, s) : String.format(Locale.ROOT, "%02d:%02d", m, s);
        int w = Math.max(82, mc.font.width("PLAY " + time) + 14);
        panel(g, w, 18);
        g.text(mc.font, "PLAY", 6, 5, MUTED, false);
        g.text(mc.font, time, 33, 5, TEXT, false);
    }

    private static void drawWaypoint(GuiGraphicsExtractor g, Minecraft mc) {
        WaypointManager.Waypoint wp = WaypointManager.selected();
        String line;
        if (wp == null) line = "WAYPOINT  none  [B add]";
        else {
            double dx = wp.x() - mc.player.getX(), dz = wp.z() - mc.player.getZ();
            int distance = (int) Math.round(Math.sqrt(dx * dx + dz * dz));
            line = "WAYPOINT  " + wp.name() + "  " + distance + "m";
        }
        int w = Math.max(120, mc.font.width(line) + 14);
        panel(g, w, 20);
        g.text(mc.font, line, 7, 6, TEXT, false);
    }

    private static void drawLocator(GuiGraphicsExtractor g, Minecraft mc) {
        int w = 220, h = 18, center = w / 2;
        panel(g, w, h);
        g.fill(center, 3, center + 1, h - 3, ACCENT);
        g.text(mc.font, "LOCATOR", 6, 5, MUTED, false);
        if (mc.level == null) return;
        float yaw = mc.player.getYRot();
        for (Player p : mc.level.players()) {
            if (p == mc.player) continue;
            double dx = p.getX() - mc.player.getX();
            double dz = p.getZ() - mc.player.getZ();
            double dist = Math.sqrt(dx * dx + dz * dz);
            if (dist > 96.0 || dist < 0.01) continue;
            double angle = Math.toDegrees(Math.atan2(dz, dx)) - 90.0;
            double rel = wrap(angle - yaw);
            if (Math.abs(rel) > 90.0) continue;
            int x = center + (int) Math.round((rel / 90.0) * 86.0);
            g.fill(x - 1, 5, x + 2, 13, 0xFFE2E5E9);
        }
    }

    private static double wrap(double a) {
        while (a <= -180.0) a += 360.0;
        while (a > 180.0) a -= 360.0;
        return a;
    }

    private static void drawCrosshair(GuiGraphicsExtractor g, Minecraft mc) {
        int cx = mc.getWindow().getGuiScaledWidth() / 2;
        int cy = mc.getWindow().getGuiScaledHeight() / 2;
        int size = Math.max(2, ChromiumSettings.crosshairSize);
        int gap = Math.max(0, ChromiumSettings.crosshairGap);
        int c = 0xFFF1F2F4;
        switch (Math.floorMod(ChromiumSettings.crosshairStyle, 4)) {
            case 0 -> {
                g.fill(cx - 1, cy - gap - size, cx + 1, cy - gap, c);
                g.fill(cx - 1, cy + gap, cx + 1, cy + gap + size, c);
                g.fill(cx - gap - size, cy - 1, cx - gap, cy + 1, c);
                g.fill(cx + gap, cy - 1, cx + gap + size, cy + 1, c);
            }
            case 1 -> {
                g.fill(cx - size, cy, cx + size + 1, cy + 1, c);
                g.fill(cx, cy - size, cx + 1, cy + size + 1, c);
            }
            case 2 -> {
                g.fill(cx - 1, cy - 1, cx + 2, cy + 2, c);
            }
            default -> {
                g.outline(cx - size, cy - size, size * 2 + 1, size * 2 + 1, c);
            }
        }
    }

    private static int fps(Minecraft mc) { return Reflect.intCall(mc, "getFps", 0); }
    private static int ping(Minecraft mc) {
        Object conn = Reflect.call(mc, "getConnection");
        Object info = conn == null ? null : Reflect.call(conn, "getPlayerInfo", mc.player.getUUID());
        int p = Reflect.intCall(info, "getLatency", -1);
        if (p < 0) p = Reflect.intCall(info, "getPing", 0);
        return Math.max(0, p);
    }
}
