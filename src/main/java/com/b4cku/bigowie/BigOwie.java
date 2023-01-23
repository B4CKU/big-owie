package com.b4cku.bigowie;

import com.mojang.logging.LogUtils;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(BigOwie.MODID)
public class BigOwie
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "bigowie";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public BigOwie()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(new SlowdownEvent());

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        // Some common setup code
        LOGGER.info("Big Owie has loaded successfully!");
    }

    @Mod.EventBusSubscriber
    public static class SlowdownEvent
    {
        @SubscribeEvent
        public void onEntityHurt(LivingHurtEvent event)
        {
            LivingEntity livingEntity = event.getEntity();
            int armor = (int)Math.floor(livingEntity.getAttribute(Attributes.ARMOR).getValue());
            int toughness = (int)Math.floor(livingEntity.getAttribute(Attributes.ARMOR_TOUGHNESS).getValue());

            if (armor <= 10) {
                return;
            }

            // 1s base slowdown + 0.5s per 4 toughness
            int duration = 20 + (toughness/4) * 10;
            // 0 base + 1 for each 5 armor over 10
            int intensity = Math.max(armor - 10, 0) / 5;

            MobEffectInstance effectInstance = new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, duration, intensity, true, false);

            livingEntity.addEffect(effectInstance);
        }
    }
}
