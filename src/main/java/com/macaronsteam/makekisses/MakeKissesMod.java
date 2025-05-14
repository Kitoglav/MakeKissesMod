package com.macaronsteam.makekisses;

import com.macaronsteam.makekisses.client.MakeKissesModClient;
import com.macaronsteam.makekisses.common.network.MakeKissesModNetwork;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.Explosion;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import org.slf4j.Logger;

@Mod(MakeKissesMod.MODID)
public class MakeKissesMod {
    public static final String MODID = "makekisses";
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final LazyOptional<SoundEvent> KISS_SOUND = LazyOptional.of(() -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(MODID, "kiss")));

    public MakeKissesMod() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(this::clientSetup);
        modBus.addListener(this::setup);
        LOGGER.info("Make Kisses Mod is loaded!");
    }

    private void setup(FMLCommonSetupEvent event) {
        MakeKissesModNetwork.registerMessages();
    }

    private void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> MakeKissesModClient::setup));
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEvents {
        @SubscribeEvent
        public static void registerSounds(RegisterEvent event) {
            KISS_SOUND.ifPresent(soundEvent -> event.register(ForgeRegistries.Keys.SOUND_EVENTS, helper -> helper.register(ResourceLocation.fromNamespaceAndPath(MODID, "kiss"), soundEvent)));
        }
    }
}
