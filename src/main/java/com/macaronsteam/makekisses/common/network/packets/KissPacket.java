package com.macaronsteam.makekisses.common.network.packets;

import com.macaronsteam.makekisses.MakeKissesMod;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.event.network.CustomPayloadEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public record KissPacket(int entityId) implements CustomPacketPayload {
    public static final StreamCodec<RegistryFriendlyByteBuf, KissPacket> CODEC = StreamCodec.of((p_329563_, p_334765_) -> p_329563_.writeVarInt(p_334765_.entityId), p_330042_ -> new KissPacket(p_330042_.readVarInt()));
    private static final Type<KissPacket> TYPE = CustomPacketPayload.createType(MakeKissesMod.MODID, "kiss");
    private static final Lazy<Map<UUID, Long>> LAST_PLAYER_HANDLE_TICK = Lazy.of(HashMap::new);

    public void handle(CustomPayloadEvent.Context context) {
        if (context.isServerSide()) {
            context.setPacketHandled(true);
            context.enqueueWork(() -> {
                ServerPlayer player = context.getSender();
                if (player != null) {
                    Map<UUID, Long> timersMap = LAST_PLAYER_HANDLE_TICK.get();
                    ServerLevel level = player.serverLevel();
                    UUID senderUUID = player.getUUID();
                    long tick = level.getGameTime();
                    if (timersMap.containsKey(senderUUID) && tick - timersMap.get(senderUUID) < 10) {
                        return;
                    }
                    Entity entity = level.getEntity(this.entityId);
                    if (entity instanceof LivingEntity target && target.closerThan(player, player.entityInteractionRange())) {
                        MakeKissesMod.KISS_SOUND.ifPresent(soundEvent -> level.playSound(null, target, soundEvent, SoundSource.PLAYERS, 1f, 1f));
                        Vec3 pos = target.position();
                        Random random = ThreadLocalRandom.current();
                        level.sendParticles(ParticleTypes.HEART, pos.x(), pos.y() + target.getEyeHeight(), pos.z(), 3, random.nextDouble(-0.5D, 0.5D), random.nextDouble(-0.5D, 0.5D), random.nextDouble(-0.5D, 0.5D), random.nextDouble(0, 0.25D));
                        player.sendSystemMessage(Component.translatable("kiss.from", Component.translatable("kiss.name.format", target.getDisplayName())));
                        target.sendSystemMessage(Component.translatable("kiss.to", Component.translatable("kiss.name.format", player.getDisplayName())));
                        timersMap.put(senderUUID, tick);
                    }
                }
            });
        }
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
