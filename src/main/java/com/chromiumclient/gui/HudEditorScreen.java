package com.chromiumclient.gui;

import com.chromiumclient.ChromiumClient;
import com.chromiumclient.hud.HudLayout;
import com.chromiumclient.util.ChromiumSettings;
import com.chromiumclient.util.Config;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

/** Minimal gray HUD editor: drag blocks, adjust global scale/background. */
public final class HudEditorScreen extends Screen {
    private String dragging;
    private double grabX, grabY;

    public HudEditorScreen() { super(Component.literal("Chromium HUD Editor")); }

    @Override protected void init() {
        this.addRenderableWidget(Button.builder(Component.literal("Scale -"), b -> {
            ChromiumSettings.hudScale = Math.max(.75f, ChromiumSettings.hudScale - .05f); save();
        }).bounds(width - 168, 70, 72, 22).build());
        this.addRenderableWidget(Button.builder(Component.literal("Scale +"), b -> {
            ChromiumSettings.hudScale = Math.min(1.50f, ChromiumSettings.hudScale + .05f); save();
        }).bounds(width - 90, 70, 72, 22).build());
        this.addRenderableWidget(Button.builder(Component.literal("Background " + (ChromiumSettings.hudBackground ? "ON" : "OFF")), b -> {
            ChromiumSettings.hudBackground = !ChromiumSettings.hudBackground; save(); this.minecraft.gui.setScreen(new HudEditorScreen());
        }).bounds(width - 168, 98, 150, 22).build());
        this.addRenderableWidget(Button.builder(Component.literal("Back"), b -> this.minecraft.gui.setScreen(new ChromiumScreen()))
                .bounds(width - 168, height - 34, 150, 22).build());
    }

    @Override public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float delta) {
        super.extractRenderState(g, mouseX, mouseY, delta);
        g.fill(0, 0, width, height, 0xD008090B);
        g.fill(0, 0, width, 52, 0xFF111317);
        g.centeredText(font, "CHROMIUM HUD EDITOR", width / 2, 14, 0xFFF0F2F4);
        g.centeredText(font, "drag blocks • gray Dawn-style HUD • scale " + Math.round(ChromiumSettings.hudScale * 100f) + "%", width / 2, 30, 0xFF7E858E);
        for (String id : HudLayout.all().keySet()) {
            HudLayout.Pos p = HudLayout.get(id); HudLayout.Size s = HudLayout.size(id);
            int bg = id.equals(dragging) ? 0xCC3A3E45 : 0xAA17191D;
            g.fill(p.x(), p.y(), p.x() + s.width(), p.y() + s.height(), bg);
            g.outline(p.x(), p.y(), s.width(), s.height(), id.equals(dragging) ? 0xFFE0E3E7 : 0xFF545A63);
            g.text(font, label(id), p.x() + 6, p.y() + 6, 0xFFE7E9EC, true);
        }
    }

    @Override public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (event.button() == 0) {
            for (String id : HudLayout.all().keySet()) {
                HudLayout.Pos p = HudLayout.get(id); HudLayout.Size s = HudLayout.size(id);
                if (event.x() >= p.x() && event.x() <= p.x() + s.width() && event.y() >= p.y() && event.y() <= p.y() + s.height()) {
                    dragging = id; grabX = event.x() - p.x(); grabY = event.y() - p.y(); return true;
                }
            }
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
        if (dragging != null) {
            HudLayout.Size s = HudLayout.size(dragging);
            int nx = (int)Math.round(event.x() - grabX), ny = (int)Math.round(event.y() - grabY);
            HudLayout.put(dragging, Math.max(0, Math.min(width - s.width(), nx)), Math.max(52, Math.min(height - s.height(), ny)));
            return true;
        }
        return super.mouseDragged(event, dx, dy);
    }

    @Override public boolean mouseReleased(MouseButtonEvent event) {
        if (dragging != null) { dragging = null; save(); return true; }
        return super.mouseReleased(event);
    }

    private void save() { Config.save(ChromiumClient.MODULES); }
    private static String label(String id) { return switch (id) {
        case "stats" -> "FPS / Ping"; case "playtime" -> "Playtime"; case "waypoint" -> "Waypoint"; case "locator" -> "Locator"; default -> id;
    }; }
    @Override public boolean isPauseScreen() { return false; }
}
