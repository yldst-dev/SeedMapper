package dev.xpple.seedmapper.client;

import dev.xpple.seedmapper.gui.OreHighlightScreen;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {
    private KeyBindings() {
    }

    public static final KeyMapping OPEN_ORE_HIGHLIGHT_GUI = KeyBindingHelper.registerKeyBinding(new KeyMapping(
        "key.seedmapper.open_ore_gui",
        GLFW.GLFW_KEY_O,
        "key.categories.seedmapper"
    ));

    public static void registerEvents() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (OPEN_ORE_HIGHLIGHT_GUI.consumeClick()) {
                openOreHighlightGui();
            }
        });
    }

    private static void openOreHighlightGui() {
        Minecraft client = Minecraft.getInstance();
        if (client.level == null || client.player == null) {
            return;
        }

        // 현재 다른 GUI가 열려있으면 닫기
        if (client.screen != null) {
            client.screen.onClose();
        }

        // 광물 하이라이트 GUI 열기
        client.setScreen(new OreHighlightScreen());
    }
}