package com.chromiumclient.util;

import com.chromiumclient.hud.HudLayout;
import com.chromiumclient.module.Module;
import com.chromiumclient.module.ModuleManager;
import net.fabricmc.loader.api.FabricLoader;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class Config {
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("chromiumclient.properties");
    private Config() {}

    public static void load(ModuleManager modules) {
        if (!Files.exists(FILE)) return;
        Properties p = new Properties();
        try (InputStream in = Files.newInputStream(FILE)) {
            p.load(in);
            for (Module m : modules.all()) {
                String v = p.getProperty("module." + m.name);
                if (v != null) m.enabled = Boolean.parseBoolean(v);
            }
            for (String id : HudLayout.all().keySet()) {
                HudLayout.Pos f = HudLayout.get(id);
                HudLayout.put(id, parseInt(p.getProperty("hud." + id + ".x"), f.x()), parseInt(p.getProperty("hud." + id + ".y"), f.y()));
            }
            ChromiumSettings.lowFireOffset = parseFloat(p.getProperty("setting.lowFireOffset"), ChromiumSettings.lowFireOffset);
            ChromiumSettings.lowShieldOffset = parseFloat(p.getProperty("setting.lowShieldOffset"), ChromiumSettings.lowShieldOffset);
            ChromiumSettings.freelookSensitivity = parseFloat(p.getProperty("setting.freelookSensitivity"), ChromiumSettings.freelookSensitivity);
            ChromiumSettings.hudScale = parseFloat(p.getProperty("setting.hudScale"), ChromiumSettings.hudScale);
            ChromiumSettings.hudBackground = Boolean.parseBoolean(p.getProperty("setting.hudBackground", Boolean.toString(ChromiumSettings.hudBackground)));
            ChromiumSettings.crosshairStyle = parseInt(p.getProperty("setting.crosshairStyle"), ChromiumSettings.crosshairStyle);
            ChromiumSettings.crosshairSize = parseInt(p.getProperty("setting.crosshairSize"), ChromiumSettings.crosshairSize);
            ChromiumSettings.crosshairGap = parseInt(p.getProperty("setting.crosshairGap"), ChromiumSettings.crosshairGap);
            ChromiumSettings.macro1 = p.getProperty("macro.1", ChromiumSettings.macro1);
            ChromiumSettings.macro2 = p.getProperty("macro.2", ChromiumSettings.macro2);
            ChromiumSettings.macro3 = p.getProperty("macro.3", ChromiumSettings.macro3);
        } catch (Exception ignored) {}
    }

    public static void save(ModuleManager modules) {
        Properties p = new Properties();
        for (Module m : modules.all()) p.setProperty("module." + m.name, Boolean.toString(m.enabled));
        for (var e : HudLayout.all().entrySet()) {
            p.setProperty("hud." + e.getKey() + ".x", Integer.toString(e.getValue().x()));
            p.setProperty("hud." + e.getKey() + ".y", Integer.toString(e.getValue().y()));
        }
        p.setProperty("setting.lowFireOffset", Float.toString(ChromiumSettings.lowFireOffset));
        p.setProperty("setting.lowShieldOffset", Float.toString(ChromiumSettings.lowShieldOffset));
        p.setProperty("setting.freelookSensitivity", Float.toString(ChromiumSettings.freelookSensitivity));
        p.setProperty("setting.hudScale", Float.toString(ChromiumSettings.hudScale));
        p.setProperty("setting.hudBackground", Boolean.toString(ChromiumSettings.hudBackground));
        p.setProperty("setting.crosshairStyle", Integer.toString(ChromiumSettings.crosshairStyle));
        p.setProperty("setting.crosshairSize", Integer.toString(ChromiumSettings.crosshairSize));
        p.setProperty("setting.crosshairGap", Integer.toString(ChromiumSettings.crosshairGap));
        p.setProperty("macro.1", ChromiumSettings.macro1);
        p.setProperty("macro.2", ChromiumSettings.macro2);
        p.setProperty("macro.3", ChromiumSettings.macro3);
        try {
            Files.createDirectories(FILE.getParent());
            try (OutputStream out = Files.newOutputStream(FILE)) { p.store(out, "ChromiumClient 26.2"); }
        } catch (Exception ignored) {}
    }

    private static int parseInt(String v, int f) { try { return v == null ? f : Integer.parseInt(v); } catch (Exception e) { return f; } }
    private static float parseFloat(String v, float f) { try { return v == null ? f : Float.parseFloat(v); } catch (Exception e) { return f; } }
}
