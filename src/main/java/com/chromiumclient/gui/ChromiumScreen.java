package com.chromiumclient.gui;

import com.chromiumclient.ChromiumClient;
import com.chromiumclient.integration.IntegrationManager;
import com.chromiumclient.module.Module;
import com.chromiumclient.util.ChromiumSettings;
import com.chromiumclient.util.Config;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

/** Completely custom Chromium gray/black module GUI. */
public final class ChromiumScreen extends Screen {
    private Module.Category category;
    private String selectedName;

    public ChromiumScreen() { this(Module.Category.HUD, "FPS Counter"); }
    private ChromiumScreen(Module.Category category, String selectedName) {
        super(Component.literal("ChromiumClient"));
        this.category = category;
        this.selectedName = selectedName;
    }

    @Override
    protected void init() {
        int w = Math.min(980, width - 24), h = Math.min(590, height - 24);
        int x = (width - w) / 2, y = (height - h) / 2;
        int sidebar = 150, details = 250;

        int cy = y + 76;
        for (Module.Category c : Module.Category.values()) {
            this.addRenderableWidget(Button.builder(Component.literal((c == category ? "● " : "  ") + label(c)), b ->
                    this.minecraft.gui.setScreen(new ChromiumScreen(c, first(c))))
                    .bounds(x + 12, cy, sidebar - 24, 24).build());
            cy += 30;
        }

        List<Module> mods = ChromiumClient.MODULES.in(category);
        int listX = x + sidebar + 16, listW = w - sidebar - details - 44;
        int rowY = y + 84;
        for (Module m : mods) {
            if (rowY > y + h - 44) break;
            int toggleW = 52;
            this.addRenderableWidget(Button.builder(Component.literal(m.name), b ->
                    this.minecraft.gui.setScreen(new ChromiumScreen(category, m.name)))
                    .bounds(listX, rowY, listW - toggleW - 6, 25).build());
            if (m.category != Module.Category.INTEGRATIONS) {
                this.addRenderableWidget(Button.builder(Component.literal(m.enabled ? "ON" : "OFF"), b -> {
                    ChromiumClient.MODULES.toggle(m);
                    this.minecraft.gui.setScreen(new ChromiumScreen(category, m.name));
                }).bounds(listX + listW - toggleW, rowY, toggleW, 25).build());
            }
            rowY += 31;
        }

        Module selected = selected();
        if (selected == null) return;
        int rx = x + w - details + 12, rw = details - 24;

        if (selected.category != Module.Category.INTEGRATIONS) {
            this.addRenderableWidget(Button.builder(Component.literal(selected.enabled ? "Disable" : "Enable"), b -> {
                ChromiumClient.MODULES.toggle(selected); refresh(selected.name);
            }).bounds(rx, y + 176, rw, 24).build());
        }

        switch (selected.name) {
            case "Low Fire" -> plusMinus(rx, y + 238, rw, selected.name, () -> ChromiumSettings.lowFireOffset, v -> ChromiumSettings.lowFireOffset = clamp(v, 0f, .9f), .05f);
            case "Low Shield" -> plusMinus(rx, y + 238, rw, selected.name, () -> ChromiumSettings.lowShieldOffset, v -> ChromiumSettings.lowShieldOffset = clamp(v, 0f, .8f), .05f);
            case "Freelook" -> plusMinus(rx, y + 238, rw, selected.name, () -> ChromiumSettings.freelookSensitivity, v -> ChromiumSettings.freelookSensitivity = clamp(v, .25f, 2f), .1f);
            case "Custom Crosshair" -> {
                this.addRenderableWidget(Button.builder(Component.literal("Crosshair style"), b -> {
                    ChromiumSettings.crosshairStyle = (ChromiumSettings.crosshairStyle + 1) % 4; saveRefresh(selected.name);
                }).bounds(rx, y + 238, rw, 22).build());
                this.addRenderableWidget(Button.builder(Component.literal("Size -"), b -> {
                    ChromiumSettings.crosshairSize = Math.max(2, ChromiumSettings.crosshairSize - 1); saveRefresh(selected.name);
                }).bounds(rx, y + 266, (rw - 6) / 2, 22).build());
                this.addRenderableWidget(Button.builder(Component.literal("Size +"), b -> {
                    ChromiumSettings.crosshairSize = Math.min(12, ChromiumSettings.crosshairSize + 1); saveRefresh(selected.name);
                }).bounds(rx + (rw + 6) / 2, y + 266, (rw - 6) / 2, 22).build());
            }
            case "HUD Editor" -> this.addRenderableWidget(Button.builder(Component.literal("Open HUD editor"), b -> this.minecraft.gui.setScreen(new HudEditorScreen()))
                    .bounds(rx, y + 238, rw, 24).build());
        }
    }

    private interface FloatGetter { float get(); }
    private interface FloatSetter { void set(float v); }
    private void plusMinus(int x, int y, int w, String module, FloatGetter getter, FloatSetter setter, float step) {
        this.addRenderableWidget(Button.builder(Component.literal("-"), b -> { setter.set(getter.get() - step); saveRefresh(module); })
                .bounds(x, y, (w - 6) / 2, 22).build());
        this.addRenderableWidget(Button.builder(Component.literal("+"), b -> { setter.set(getter.get() + step); saveRefresh(module); })
                .bounds(x + (w + 6) / 2, y, (w - 6) / 2, 22).build());
    }

    private void refresh(String name) { this.minecraft.gui.setScreen(new ChromiumScreen(category, name)); }
    private void saveRefresh(String name) { Config.save(ChromiumClient.MODULES); refresh(name); }
    private static float clamp(float v, float min, float max) { return Math.max(min, Math.min(max, v)); }

    @Override
    public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float delta) {
        super.extractRenderState(g, mouseX, mouseY, delta);
        int w = Math.min(980, width - 24), h = Math.min(590, height - 24);
        int x = (width - w) / 2, y = (height - h) / 2;
        int sidebar = 150, details = 250;
        int listX = x + sidebar + 16;
        int rightX = x + w - details;

        g.fill(0, 0, width, height, 0x88000000);
        g.fill(x, y, x + w, y + h, 0xF20B0C0E);
        g.outline(x, y, w, h, 0xFF34383E);
        g.fill(x, y, x + w, y + 54, 0xFF101216);
        g.fill(x, y + 54, x + sidebar, y + h, 0xFF0D0F12);
        g.fill(rightX, y + 54, x + w, y + h, 0xFF0E1013);
        g.fill(x + 10, y + 12, x + 34, y + 36, 0xFFE1E4E8);
        g.text(font, "Cr", x + 14, y + 20, 0xFF111317, true);
        g.text(font, "CHROMIUM", x + 43, y + 18, 0xFFF0F2F4, true);
        g.text(font, "CLIENT 26.2", x + 43, y + 31, 0xFF7E858E, false);
        g.text(font, "RIGHT SHIFT", x + w - 84, y + 24, 0xFF7E858E, false);

        g.text(font, label(category).toUpperCase(), listX, y + 64, 0xFFE4E7EB, true);
        g.text(font, subtitle(category), listX, y + 76, 0xFF747B84, false);

        Module s = selected();
        if (s != null) {
            int rx = rightX + 14;
            g.text(font, s.name, rx, y + 72, 0xFFF1F3F5, true);
            if (s.category == Module.Category.INTEGRATIONS) {
                boolean loaded = IntegrationManager.loaded(s.name);
                g.text(font, loaded ? "● ACTIVE" : "○ NOT INSTALLED", rx, y + 92, loaded ? 0xFFC7CBD1 : 0xFF777D85, true);
                drawWrapped(g, s.description, rx, y + 116, details - 28, 0xFF9CA2AA);
                g.text(font, "Companion integration", rx, y + 162, 0xFF6E747C, false);
                g.text(font, "Use install-companions.ps1", rx, y + 176, 0xFFB4B8BE, false);
            } else {
                g.text(font, s.enabled ? "● ENABLED" : "○ DISABLED", rx, y + 92, s.enabled ? 0xFFD7DADE : 0xFF717780, true);
                drawWrapped(g, s.description, rx, y + 116, details - 28, 0xFF9CA2AA);
                drawSettingValue(g, s, rx, y + 214);
            }
        }

        g.text(font, "F6/F7/F8 macros  •  B waypoint  •  N cycle  •  Left Alt freelook  •  Right Ctrl HUD", x + 16, y + h - 18, 0xFF676E77, false);
    }

    private void drawSettingValue(GuiGraphicsExtractor g, Module s, int x, int y) {
        String value = switch (s.name) {
            case "Low Fire" -> Math.round(ChromiumSettings.lowFireOffset * 100f) + "%";
            case "Low Shield" -> Math.round(ChromiumSettings.lowShieldOffset * 100f) + "%";
            case "Freelook" -> String.format("%.2fx", ChromiumSettings.freelookSensitivity);
            case "Custom Crosshair" -> "Style " + (ChromiumSettings.crosshairStyle + 1) + "  •  size " + ChromiumSettings.crosshairSize;
            default -> "";
        };
        if (!value.isEmpty()) {
            g.text(font, "SETTING", x, y, 0xFF686F78, true);
            g.text(font, value, x, y + 16, 0xFFE4E7EB, true);
        }
    }

    private Module selected() {
        Module m = ChromiumClient.MODULES.get(selectedName);
        if (m != null && m.category == category) return m;
        List<Module> list = ChromiumClient.MODULES.in(category);
        return list.isEmpty() ? null : list.getFirst();
    }
    private String first(Module.Category c) { List<Module> l = ChromiumClient.MODULES.in(c); return l.isEmpty() ? "" : l.getFirst().name; }
    private static String label(Module.Category c) { return switch (c) {
        case HUD -> "HUD"; case MOVEMENT -> "Movement"; case VISUAL -> "Visual"; case UTILITY -> "Utility"; case INTEGRATIONS -> "Integrations";
    }; }
    private static String subtitle(Module.Category c) { return switch (c) {
        case HUD -> "compact overlays"; case MOVEMENT -> "legit movement helpers"; case VISUAL -> "client-side rendering"; case UTILITY -> "quality of life"; case INTEGRATIONS -> "official companion mods";
    }; }
    private void drawWrapped(GuiGraphicsExtractor g, String text, int x, int y, int max, int color) {
        String line = ""; int yy = y;
        for (String word : text.split(" ")) {
            String n = line.isEmpty() ? word : line + " " + word;
            if (!line.isEmpty() && font.width(n) > max) { g.text(font, line, x, yy, color, false); yy += 11; line = word; }
            else line = n;
        }
        if (!line.isEmpty()) g.text(font, line, x, yy, color, false);
    }
    @Override public boolean isPauseScreen() { return false; }
}
