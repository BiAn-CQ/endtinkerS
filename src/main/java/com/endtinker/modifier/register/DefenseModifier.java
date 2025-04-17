package com.endtinker.modifier.register;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.ModifyDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.armor.OnAttackedModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.armor.EquipmentChangeModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import javax.annotation.Nonnull;
import net.minecraft.world.damagesource.DamageSource;
import java.util.*;


import net.minecraftforge.common.MinecraftForge;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Mod.EventBusSubscriber
public class DefenseModifier {

    public static class dot2 extends NoLevelsModifier implements OnAttackedModifierHook, ModifyDamageModifierHook,EquipmentChangeModifierHook{
        private static final int POWER_DURATION = 200;
        private Object eventListener; // 新增事件监听器引用存储



        protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
            hookBuilder.addHook(this, ModifierHooks.ON_ATTACKED);
            hookBuilder.addHook(this, ModifierHooks.MODIFY_DAMAGE);
            hookBuilder.addHook(this, ModifierHooks.EQUIPMENT_CHANGE);
            super.registerHooks(hookBuilder);
        }

        @Override
        public void onAttacked(@NotNull IToolStackView tool, @NotNull ModifierEntry entry, EquipmentContext context, @NotNull EquipmentSlot slot, DamageSource damageSource, float amount, boolean isDirectDamage) {
            Entity attacker = damageSource.getEntity();
            if (!(context.getEntity() instanceof Player player)) return;
            if (!(attacker instanceof LivingEntity living)) return;

            // 获取所有负面效果
            List<MobEffect> negativeEffects = new ArrayList<>();
            for (var key : ForgeRegistries.MOB_EFFECTS.getKeys()) {
                MobEffect effect = ForgeRegistries.MOB_EFFECTS.getValue(key);
                if (effect != null && !effect.isBeneficial()) {
                    negativeEffects.add(effect);
                }
            }

            if (negativeEffects.isEmpty()) {
                return;
            }

            // 随机选择一个负面效果
            Random random = new Random();
            MobEffect selectedEffect = negativeEffects.get(random.nextInt(negativeEffects.size()));

            // 给攻击者添加负面效果
            living.addEffect(new MobEffectInstance(selectedEffect, 200*10*5, 4));

            // 对攻击者造成反伤
            living.hurt(player.damageSources().thorns(player), amount * 10f);// 反伤为受到伤害的 1000% // 反伤为受到伤害的 1000%
        }

            @Override
        public float modifyDamageTaken(@Nonnull IToolStackView tool, @Nonnull ModifierEntry modifier, @Nonnull EquipmentContext context, @Nonnull EquipmentSlot slot, @Nonnull DamageSource damageSource, float amount, boolean isDirectDamage) {

            if (!(context.getEntity() instanceof Player player)) {
                return amount;
            }
            // 如果被攻击的实体是玩家，将伤害减免90%
            return amount * 0.1f;
        }
        @Override
        public void onEquip(@Nonnull IToolStackView tool, @Nonnull ModifierEntry modifier,@Nonnull EquipmentChangeContext context) {
            if (isArmorSlot(context.getChangedSlot()) && context.getEntity() instanceof Player player) {
                player.getAbilities().mayfly = true;
                player.getAbilities().flying = true;
                player.onUpdateAbilities();
                player.getPersistentData().putBoolean("dot2_equipped", true);
                // 修改事件监听器注册方式
                eventListener = new Object() {
                    @SubscribeEvent
                    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
                        if (event.phase == TickEvent.Phase.START
                                && event.player == player
                                && player.getPersistentData().getBoolean("dot2_equipped")) {

                            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, POWER_DURATION, 3));

                            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, POWER_DURATION, 4));

                            player.addEffect(new MobEffectInstance(MobEffects.SATURATION, POWER_DURATION, 4));

                            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, POWER_DURATION, 9));

                            List<MobEffectInstance> toRemove = player.getActiveEffects().stream()
                                    .filter(effect ->!effect.getEffect().isBeneficial())
                                    .toList();
                            toRemove.forEach(effect -> player.removeEffect(effect.getEffect()));
                        }
                    }
                };
                MinecraftForge.EVENT_BUS.register(eventListener); // 使用成员变量注册
            }
        }

        @Override
        public void onUnequip(@Nonnull IToolStackView tool, @Nonnull ModifierEntry modifier, @Nonnull EquipmentChangeContext context) {
            if (isArmorSlot(context.getChangedSlot()) && context.getEntity() instanceof Player player) {
                player.getPersistentData().putBoolean("dot2_equipped", false);
                player.getAbilities().mayfly = false;
                player.getAbilities().flying = false;
                player.onUpdateAbilities();
                MinecraftForge.EVENT_BUS.unregister(this);
                MinecraftForge.EVENT_BUS.unregister(eventListener); // 改为使用存储的引用注销
            }
        }

        private boolean isArmorSlot(EquipmentSlot slot) {
            return slot == EquipmentSlot.HEAD || slot == EquipmentSlot.CHEST || slot == EquipmentSlot.LEGS || slot == EquipmentSlot.FEET;
        }
    }
}