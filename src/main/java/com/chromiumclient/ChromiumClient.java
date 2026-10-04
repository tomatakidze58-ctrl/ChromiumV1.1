package com.chromiumclient;

import com.chromiumclient.camera.FreelookState;
import com.chromiumclient.gui.ChromiumScreen;
import com.chromiumclient.gui.HudEditorScreen;
import com.chromiumclient.hud.ChromiumHud;
import com.chromiumclient.module.ModuleManager;
import com.chromiumclient.util.ChromiumSettings;
import com.chromiumclient.util.Config;
import com.chromiumclient.util.Reflect;
import com.chromiumclient.waypoint.WaypointManager;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public final class ChromiumClient implements ClientModInitializer {
    public static final String MOD_ID = "chromiumclient";
    public static final ModuleManager MODULES = new ModuleManager();
    public static final long SESSION_START = System.currentTimeMillis();

    private KeyMapping menuKey, hudKey, freelookKey, addWaypointKey, cycleWaypointKey, macro1Key, macro2Key, macro3Key;

    public static Identifier id(String path) { return Identifier.fromNamespaceAndPath(MOD_ID, path); }

    @Override
    public void onInitializeClient() {
        Config.load(MODULES);
        WaypointManager.load();

        KeyMapping.Category category = KeyMapping.Category.register(id("main"));
        menuKey = key("key.chromiumclient.menu", GLFW.GLFW_KEY_RIGHT_SHIFT, category);
        hudKey = key("key.chromiumclient.hud", GLFW.GLFW_KEY_RIGHT_CONTROL, category);
        freelookKey = key("key.chromiumclient.freelook", GLFW.GLFW_KEY_LEFT_ALT, category);
        addWaypointKey = key("key.chromiumclient.waypoint_add", GLFW.GLFW_KEY_B, category);
        cycleWaypointKey = key("key.chromiumclient.waypoint_cycle", GLFW.GLFW_KEY_N, category);
        macro1Key = key("key.chromiumclient.macro1", GLFW.GLFW_KEY_F6, category);
        macro2Key = key("key.chromiumclient.macro2", GLFW.GLFW_KEY_F7, category);
        macro3Key = key("key.chromiumclient.macro3", GLFW.GLFW_KEY_F8, category);

        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            while (menuKey.consumeClick()) mc.gui.setScreen(new ChromiumScreen());
            while (hudKey.consumeClick()) mc.gui.setScreen(new HudEditorScreen());
            while (addWaypointKey.consumeClick()) if (MODULES.on("Waypoints")) WaypointManager.addCurrent(mc);
            while (cycleWaypointKey.consumeClick()) if (MODULES.on("Waypoints")) WaypointManager.cycle();
            while (macro1Key.consumeClick()) if (MODULES.on("Chat Macros")) sendChat(mc, ChromiumSettings.macro1);
            while (macro2Key.consumeClick()) if (MODULES.on("Chat Macros")) sendChat(mc, ChromiumSettings.macro2);
            while (macro3Key.consumeClick()) if (MODULES.on("Chat Macros")) sendChat(mc, ChromiumSettings.macro3);
            MODULES.tick(mc);
            FreelookState.updateHeld(freelookKey.isDown());
        });

        HudElementRegistry.attachElementBefore(VanillaHudElements.CHAT, id("hud"), ChromiumHud::render);
    }

    private static KeyMapping key(String id, int code, KeyMapping.Category category) {
        return KeyMappingHelper.registerKeyMapping(new KeyMapping(id, InputConstants.Type.KEYSYM, code, category));
    }

    private static void sendChat(Minecraft mc, String text) {
        if (mc.player == null || text == null || text.isBlank()) return;
        Object conn = Reflect.field(mc.player, "connection");
        if (conn == null) conn = Reflect.call(mc, "getConnection");
        Reflect.invokeAny(conn, new String[]{"sendChat", "sendChatMessage"}, text);
    }
}
