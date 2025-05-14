package com.macaronsteam.makekisses.common.network;

import com.macaronsteam.makekisses.MakeKissesMod;
import com.macaronsteam.makekisses.common.network.packets.KissPacket;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.SimpleChannel;

import java.util.Optional;

public class MakeKissesModNetwork {

    public static final SimpleChannel INSTANCE = ChannelBuilder
            .named(ResourceLocation.fromNamespaceAndPath(MakeKissesMod.MODID, "main"))
            .simpleChannel();

    public static void registerMessages(){
        INSTANCE.messageBuilder(KissPacket.class, 0, NetworkDirection.PLAY_TO_SERVER)
                .codec(KissPacket.CODEC)
                .consumerMainThread(KissPacket::handle)
                .add();
    }
}
