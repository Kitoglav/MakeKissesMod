package com.macaronsteam.makekisses.common.network;

import com.macaronsteam.makekisses.common.network.packets.KissPacket;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.packet.CustomPayload;

public class MakeKissesModNetwork {
    public static void registerServerReceivers() {
        PayloadTypeRegistry.playC2S().register(KissPacket.ID, KissPacket.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(KissPacket.ID, KissPacket::handle);
    }
}