package com.macaronsteam.makekisses;

import com.macaronsteam.makekisses.common.network.packets.KissPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.hit.EntityHitResult;
import org.lwjgl.glfw.GLFW;

public class MakeKissesModClient implements ClientModInitializer {
    private static KeyBinding KISS_KEY;

    @Override
    public void onInitializeClient() {
        KISS_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.kiss.name",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                "key.categories.multiplayer"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (KISS_KEY.wasPressed()) {
                if (client.crosshairTarget instanceof EntityHitResult entityHit) {
                    ClientPlayNetworking.send(new KissPacket(entityHit.getEntity().getId()));
                }
            }
        });
    }
}