package com.endtinkers.effect;

import com.endtinkers.Endtinkers;
import com.endtinkers.modifier.register.CustomModifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import net.minecraft.world.damagesource.DamageSource;

public class CustomDamageEffect extends MobEffect {
    public static final CustomDamageEffect INSTANCE = new CustomDamageEffect();


    protected CustomDamageEffect() {
        super(MobEffectCategory.HARMFUL, 0x000000); // 黑色
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % 20 == 0; // 每秒触发一次
    }

    @Override
    public void applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        entity.hurt(entity.damageSources().fellOutOfWorld(), 100.0F); // 世界中坠落伤害，固定伤害值为 100.0F
    }

    private float calculateDamage(int effectCount, int level) {
        if (effectCount >= 20) {
            return 2147483647F;
        } else if (effectCount >= 15) {
            return 500000 * level * 100;
        } else if (effectCount >= 10) {
            return 1000 * level * 100;
        } else if (effectCount >= 5) {
            return 50 * level * 100;
        }
        return 0;
    }

public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, Endtinkers.MODID);

// 注册 CustomDamageEffect
public static final RegistryObject<MobEffect> CUSTOM_DAMAGE_EFFECT = MOB_EFFECTS.register("custom_damage_effect", () -> INSTANCE);

// 在模组主类中调用这个方法来注册
public static void register(IEventBus eventBus) {
    MOB_EFFECTS.register(eventBus);
}
}