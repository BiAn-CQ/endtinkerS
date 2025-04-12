package com.endtinkers.modifier.register;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.common.Mod;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.ModifyDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.armor.OnAttackedModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nonnull;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;

@Mod.EventBusSubscriber
public class DefenseModifier {

    public static class dot2 extends NoLevelsModifier implements OnAttackedModifierHook, ModifyDamageModifierHook {

        @Override
        protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
            hookBuilder.addHook(this, ModifierHooks.ON_ATTACKED);
            hookBuilder.addHook(this, ModifierHooks.MODIFY_DAMAGE);
            super.registerHooks(hookBuilder);
        }

        public void onAttacked(IToolStackView tool, ModifierEntry entry, EquipmentContext context, EquipmentSlot slot, DamageSource damageSource, float amount, boolean isDirectDamage) {

            Entity attacker = damageSource.getEntity();
            if (!(context.getEntity() instanceof Player player)) return;
            if (!(attacker instanceof LivingEntity living)) return;



        }


        @Override
        public float modifyDamageTaken(@Nonnull IToolStackView tool, @Nonnull ModifierEntry modifier, @Nonnull EquipmentContext context, @Nonnull EquipmentSlot slot, @Nonnull DamageSource source, float amount, boolean isDirectDamage) {

            if (!(context.getEntity() instanceof Player player)) {
                return amount;
            }
            // 如果被攻击的实体是玩家，将伤害减免 100%，即返回 0
            return 0;


        }


    }
}