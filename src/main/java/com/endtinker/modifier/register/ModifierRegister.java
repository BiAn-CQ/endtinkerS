package com.endtinker.modifier.register;

import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;


public class ModifierRegister {


    public static class Rude extends Modifier implements MeleeDamageModifierHook {
        public Rude() {
        }


        @Override
        protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {

            hookBuilder.addHook(this, ModifierHooks.MELEE_DAMAGE);
        }


        @Override
        public float getMeleeDamage(@NotNull IToolStackView tool, @NotNull ModifierEntry modifier, ToolAttackContext context, float baseDamage, float damage) {

            LivingEntity enemy = context.getLivingTarget();
            //检测该实体最大生命值是否大于20点
            if (enemy != null && enemy.getMaxHealth() > 20) {
                //是，则输出原伤害*100000
                return damage *100000;
            }
            //否则输出这个原伤害
            return damage;
        }

    }
}