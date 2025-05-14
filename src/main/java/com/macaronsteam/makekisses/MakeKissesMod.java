package com.macaronsteam.makekisses;

import com.macaronsteam.makekisses.common.network.MakeKissesModNetwork;
import net.fabricmc.api.ModInitializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class MakeKissesMod implements ModInitializer {
    public static final String MODID = "makekisses";
    public static final SoundEvent KISS_SOUND = SoundEvent.of(Identifier.of(MODID, "kiss"));

    @Override
    public void onInitialize() {
        Registry.register(Registries.SOUND_EVENT, Identifier.of(MODID, "kiss"), KISS_SOUND);
        MakeKissesModNetwork.registerServerReceivers();
    }
}