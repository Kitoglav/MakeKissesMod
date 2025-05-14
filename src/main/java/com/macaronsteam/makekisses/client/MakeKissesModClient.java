package com.macaronsteam.makekisses.client;

import com.macaronsteam.makekisses.common.network.MakeKissesModNetwork;
import com.macaronsteam.makekisses.common.network.packets.KissPacket;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

public class MakeKissesModClient {
    private static final LazyOptional<KeyMapping> KISS_KEY = LazyOptional.of(() -> new KeyMapping("key.kiss.name", GLFW.GLFW_KEY_R, "key.categories.multiplayer"));

    public static void setup() {

    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ModEvents {
        @SubscribeEvent
        public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
            KISS_KEY.ifPresent(event::register);
        }
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void clientTick(TickEvent.ClientTickEvent event) {
            if (event.phase == TickEvent.Phase.END) {
                return;
            }
            KISS_KEY.ifPresent(key -> {
                if (key.consumeClick()) {
                    Entity hoverEntity = Minecraft.getInstance().crosshairPickEntity;
                    if (hoverEntity != null) {
                        MakeKissesModNetwork.INSTANCE.send(new KissPacket(hoverEntity.getId()), PacketDistributor.SERVER.noArg());
                    }
                }
            });
        }
    }

}
