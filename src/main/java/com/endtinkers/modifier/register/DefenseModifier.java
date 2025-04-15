package com.endtinkers.modifier.register;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.common.Mod;
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
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.concurrent.atomic.AtomicInteger;

@Mod.EventBusSubscriber
public class DefenseModifier {

    public static class dot2 extends NoLevelsModifier implements OnAttackedModifierHook, ModifyDamageModifierHook,EquipmentChangeModifierHook{
        private static final int POWER_DURATION = 200;
        private static final int UPDATE_INTERVAL = 20;
        private final AtomicInteger tickCounter = new AtomicInteger(0);
        private final AtomicInteger amplifier = new AtomicInteger(0);


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


        }

        @Override
        public float modifyDamageTaken(@Nonnull IToolStackView tool, @Nonnull ModifierEntry modifier, @Nonnull EquipmentContext context, @Nonnull EquipmentSlot slot, @Nonnull DamageSource damageSource, float amount, boolean isDirectDamage) {

            if (!(context.getEntity() instanceof Player player)) {
                return amount;
            }
            // 如果被攻击的实体是玩家，将伤害减免 100%，即返回 0
            return 0;
        }
        @Override
        public void onEquip(@Nonnull IToolStackView tool, @Nonnull ModifierEntry modifier,@Nonnull EquipmentChangeContext context) {
            if (isArmorSlot(context.getChangedSlot()) && context.getEntity() instanceof Player player) {
                player.getAbilities().mayfly = true;
                player.getAbilities().flying = true;
                player.onUpdateAbilities();
                player.getPersistentData().putBoolean("dot2_equipped", true);
                MinecraftForge.EVENT_BUS.register(new Object() {
                    @SubscribeEvent
                    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
                        if (event.phase == TickEvent.Phase.START 
                            && event.player == player 
                            && player.getPersistentData().getBoolean("dot2_equipped")) {
                            
                            int currentTick = tickCounter.incrementAndGet();
                            if (currentTick % UPDATE_INTERVAL == 0) {
                                amplifier.incrementAndGet();
                            }
                            
                            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, POWER_DURATION, amplifier.get()));
                            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, POWER_DURATION, amplifier.get()));
                            List<MobEffectInstance> toRemove = player.getActiveEffects().stream()
                                    .filter(effect -> !effect.getEffect().equals(MobEffects.DAMAGE_RESISTANCE)
                                            && !effect.getEffect().equals(MobEffects.REGENERATION))
                                    .toList();

                            toRemove.forEach(effect -> player.removeEffect(effect.getEffect()));
                        }
                    }
                });
            }
        }

        @Override
        public void onUnequip(@Nonnull IToolStackView tool, @Nonnull ModifierEntry modifier, @Nonnull EquipmentChangeContext context) {
            if (isArmorSlot(context.getChangedSlot()) && context.getEntity() instanceof Player player) {
                player.getPersistentData().putBoolean("dot2_equipped", false);
                player.getAbilities().mayfly = false;
                player.getAbilities().flying = false;
                player.onUpdateAbilities();
                amplifier.set(0);
                tickCounter.set(0);
                MinecraftForge.EVENT_BUS.unregister(this);
            }
        }

        private boolean isArmorSlot(EquipmentSlot slot) {
            return slot == EquipmentSlot.HEAD || slot == EquipmentSlot.CHEST || slot == EquipmentSlot.LEGS || slot == EquipmentSlot.FEET;
        }


    }
}

