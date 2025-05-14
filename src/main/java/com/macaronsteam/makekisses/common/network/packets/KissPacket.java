package com.macaronsteam.makekisses.common.network.packets;

import com.macaronsteam.makekisses.MakeKissesMod;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public record KissPacket(int entityId) implements CustomPayload {
    public static final Id<KissPacket> ID = new Id<>(Identifier.of("makekisses", "kiss"));
    public static final PacketCodec<PacketByteBuf, KissPacket> CODEC = PacketCodec.ofStatic((buf, packet) -> buf.writeVarInt(packet.entityId), buf -> new KissPacket(buf.readVarInt()));

    private static final Map<UUID, Long> LAST_PLAYER_HANDLE_TICK = new HashMap<>();

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void handle(KissPacket packet, ServerPlayNetworking.Context context) {
        ServerPlayerEntity player = context.player();
        Entity target = player.getWorld().getEntityById(packet.entityId);
        double range = player.getBlockInteractionRange();
        if (target instanceof ServerPlayerEntity && player.squaredDistanceTo(target) <= range * range) {
            long currentTick = player.getWorld().getTime();
            if (LAST_PLAYER_HANDLE_TICK.getOrDefault(player.getUuid(), 0L) + 10 > currentTick) {
                return;
            }
            player.getWorld().playSound(null, target.getX(), target.getY(), target.getZ(), MakeKissesMod.KISS_SOUND, SoundCategory.PLAYERS, 1.0F, 1.0F);
            Vec3d pos = target.getPos();
            Random random = ThreadLocalRandom.current();
            player.getServerWorld().spawnParticles(ParticleTypes.HEART, pos.x, pos.y + target.getEyeHeight(target.getPose()), pos.z, 3, random.nextDouble(-0.5D, 0.5D), random.nextDouble(-0.5D, 0.5D), random.nextDouble(-0.5D, 0.5D), random.nextDouble(0, 0.25D));
            player.sendMessage(Text.translatable("kiss.from", Text.translatable("kiss.name.format", target.getDisplayName())));
            target.sendMessage(Text.translatable("kiss.to", Text.translatable("kiss.name.format", player.getDisplayName())));

            LAST_PLAYER_HANDLE_TICK.put(player.getUuid(), currentTick);
        }
    }
}