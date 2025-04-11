package com.endtinkers.modifier.register;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nonnull;
import java.util.*;

import static slimeknights.tconstruct.tools.data.ModifierIds.luck;

public class DefenseModifier {

    public static class dot2 extends Modifier implements MeleeDamageModifierHook {

    

        // 构造方法
        public dot2() {
        }

        @Override
        protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
            hookBuilder.addHook(this, ModifierHooks.MELEE_DAMAGE);
        }

        @Override
        public float getMeleeDamage(@Nonnull IToolStackView tool, @Nonnull ModifierEntry modifier, ToolAttackContext context, float baseDamage, float damage) {
            // 获取被攻击的目标实体
            LivingEntity defender = context.getLivingTarget();
            if (defender != null) {
                // 获取当前词条的等级
                int level = modifier.getLevel();

                // 获取攻击方实体
                LivingEntity attacker = context.getAttacker();
                if (attacker != null) {
                    // 获取攻击方身上的负面效果数量
                    int attackerEffectCount = attacker.getActiveEffects().size();

                    // 根据攻击方的负面效果数量减少伤害
                    float damageReduction = getDamageReduction(attackerEffectCount, level);
                    damage = baseDamage * (1 - damageReduction);


                    if (new Random().nextFloat() < 0.5 * level) {
                        float reboundDamage = damage * 5f; // 反弹 500% 的伤害
                        attacker.hurt(attacker.damageSources().generic(), reboundDamage);
                    }

                    // 多属性回复（仅生命值）
                    defender.heal(50 * level);

                    // 添加随机增益效果
                    applyRandomBeneficialEffect(defender, level);
                    // 给攻击者添加随机负面效果
                    applyRandomHarmfulEffect(attacker, level);
                }

                // 负面效果免疫
                removeNegativeEffects(defender);
            }
            return damage;
        }

        /**
         * 根据攻击方的负面效果数量和词条等级获取伤害减少比例
         *
         * @param effectCount 攻击方的负面效果数量
         * @param level       词条等级
         * @return 伤害减少比例
         */
        private float getDamageReduction(int effectCount, int level) {
            if (effectCount >= 25) {
                return 0.9f * level / 5;
            } else if (effectCount >= 20) {
                return 0.7f * level / 5;
            } else if (effectCount >= 15) {
                return 0.5f * level / 5;
            } else if (effectCount >= 10) {
                return 0.3f * level / 5;
            } else if (effectCount >= 5) {
                return 0.1f * level / 5;
            }
            return 0;
        }

        /**
         * 移除目标实体身上的负面效果
         *
         * @param entity 目标实体
         */
        private void removeNegativeEffects(LivingEntity entity) {
            Collection<MobEffectInstance> effects = entity.getActiveEffects();
            for (MobEffectInstance effectInstance : effects) {
                MobEffect effect = effectInstance.getEffect();
                if (effect.getCategory() == MobEffectCategory.HARMFUL) {
                    entity.removeEffect(effect);
                }
            }
        }

        /**
         * 应用随机增益效果
         *
         * @param entity 目标实体
         * @param level  词条等级
         */
        private void applyRandomBeneficialEffect(LivingEntity entity, int level) {
            List<MobEffectInstance> beneficialEffects = new ArrayList<>();
            beneficialEffects.add(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 200*5, 0));
            beneficialEffects.add(new MobEffectInstance(MobEffects.REGENERATION, 200*5, 0));
            beneficialEffects.add(new MobEffectInstance(MobEffects.ABSORPTION, 200*5, 0));

            Random random = new Random();
            MobEffectInstance randomEffect = beneficialEffects.get(random.nextInt(beneficialEffects.size()));

            MobEffectInstance existingEffect = entity.getEffect(randomEffect.getEffect());
            if (existingEffect != null) {
                int newAmplifier = Math.min(existingEffect.getAmplifier() + 1, 255);
                entity.addEffect(new MobEffectInstance(randomEffect.getEffect(), 200*5, newAmplifier));
            } else {
                entity.addEffect(randomEffect);
            }
        }

        /**
         * 应用随机负面效果
         *
         * @param entity 目标实体
         * @param level  词条等级
         */
        private void applyRandomHarmfulEffect(LivingEntity entity, int level) {
            List<MobEffectInstance> harmfulEffects = new ArrayList<>();
            harmfulEffects.add(new MobEffectInstance(MobEffects.POISON, 200*5, 0));
            harmfulEffects.add(new MobEffectInstance(MobEffects.WEAKNESS, 200*5, 0));
            harmfulEffects.add(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200*5, 0));
            harmfulEffects.add(new MobEffectInstance(MobEffects.BLINDNESS, 200*5, 0));
            harmfulEffects.add(new MobEffectInstance(MobEffects.HUNGER, 200*5, 0));
            harmfulEffects.add(new MobEffectInstance(MobEffects.CONFUSION, 200*5, 0));
            harmfulEffects.add(new MobEffectInstance(MobEffects.WITHER, 200*5, 0));
            harmfulEffects.add(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 200*5, 0));
            harmfulEffects.add(new MobEffectInstance(MobEffects.DARKNESS, 200*5, 0));
            harmfulEffects.add(new MobEffectInstance(MobEffects.LEVITATION, 200*5, 0));
            harmfulEffects.add(new MobEffectInstance(MobEffects.UNLUCK, 200*5, 0));
            harmfulEffects.add(new MobEffectInstance(MobEffects.HARM, 200*5, 0));
            harmfulEffects.add(new MobEffectInstance(MobEffects.SLOW_FALLING, 200*5, 0));
            Random random = new Random();
            MobEffectInstance randomEffect = harmfulEffects.get(random.nextInt(harmfulEffects.size()));

            MobEffectInstance existingEffect = entity.getEffect(randomEffect.getEffect());
            if (existingEffect != null) {
                int newAmplifier = Math.min(existingEffect.getAmplifier() + 1, 255);
                entity.addEffect(new MobEffectInstance(randomEffect.getEffect(), 200*5, newAmplifier));
            } else {
                entity.addEffect(randomEffect);
            }
        }
    }
}
