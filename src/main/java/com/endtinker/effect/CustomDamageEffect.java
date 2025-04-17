package com.endtinker.effect;

import com.endtinker.Endtinker;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

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
    public void applyEffectTick(@NotNull LivingEntity entity, int amplifier)
    {
        int effectCount = entity.getActiveEffects().size();
        int level = amplifier + 1;
        float damageAmount = calculateDamage(effectCount, level);
        entity.hurt(entity.damageSources().magic(), damageAmount);
        entity.hurt(entity.damageSources().fall(), damageAmount);
        entity.hurt(entity.damageSources().inWall(), damageAmount);
        entity.hurt(entity.damageSources().inFire(), damageAmount);
        entity.hurt(entity.damageSources().lightningBolt(), damageAmount);
        entity.hurt(entity.damageSources().drown(), damageAmount);
        entity.hurt(entity.damageSources().starve(), damageAmount);
        entity.hurt(entity.damageSources().generic(), damageAmount);
        entity.hurt(entity.damageSources().cactus(), damageAmount);
        entity.hurt(entity.damageSources().dragonBreath(), damageAmount);
        entity.hurt(entity.damageSources().wither(), damageAmount);
        entity.hurt(entity.damageSources().freeze(), damageAmount);
    }

    private float calculateDamage(int effectCount, int level) {
        return 10 * level * 100 +   effectCount * 2147483648F;
    }

public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, Endtinker.MODID);


public static final RegistryObject<MobEffect> CUSTOM_DAMAGE_EFFECT = MOB_EFFECTS.register("custom_damage_effect", () -> INSTANCE);


public static void register(IEventBus eventBus) {
    MOB_EFFECTS.register(eventBus);
    }
}