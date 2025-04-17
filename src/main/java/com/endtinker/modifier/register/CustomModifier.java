package com.endtinker.modifier.register;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Mod.EventBusSubscriber
public class CustomModifier {


    public static class dot extends Modifier implements MeleeDamageModifierHook{

        // 构造方法
        public dot() {
        }

        @Override
        protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
            hookBuilder.addHook(this, ModifierHooks.MELEE_DAMAGE);
            super.registerHooks(hookBuilder);
        }

        @Override
        public float getMeleeDamage(@Nonnull IToolStackView tool, @Nonnull ModifierEntry modifier, ToolAttackContext context, float baseDamage, float damage) {
            // 获取被攻击的目标实体
            LivingEntity enemy = context.getLivingTarget();
            if (enemy != null) {
                // 获取当前词条的等级
                int level = modifier.getLevel();
                // 获取武器的面板攻击值
                float weaponDamage = tool.getDamage();

                // 随机选择（词条等级）数种负面效果
                List<MobEffect> statusEffects = getRandomStatusEffects(level);
                // 给目标实体添加负面效果
                applyStatusEffects(enemy, statusEffects, level);

                // 重新获取目标实体身上的负面效果数量
                int effectCount = enemy.getActiveEffects().size();

                // 根据负面效果的种类提升原本攻击伤害
                float damageMultiplier = getDamageMultiplier(effectCount);
                damage = baseDamage * damageMultiplier;

                // 检查目标实体身上的负面效果数量
                if (effectCount >= 26) {
                    // 若目标获得 26 种以上负面效果时则直接秒杀
                    enemy.setHealth(0);
                }
            }
            return damage;
        }

        /**
         * 根据负面效果的种类获取伤害乘数
         *
         * @param effectCount 负面效果的种类数量
         * @return 伤害乘数
         */
        private float getDamageMultiplier(float effectCount) {
            return 1 + effectCount * 2147483648F;
        }

        /**
         * 随机选择指定数量的负面效果
         *
         * @param count 负面效果的数量
         * @return 负面效果列表
         */
        private List<MobEffect> getRandomStatusEffects(int count) {
            // 获取所有 MobEffect 的 ResourceLocation 列表
            List<ResourceLocation> allEffectIds = new ArrayList<>(ForgeRegistries.MOB_EFFECTS.getKeys());
            List<MobEffect> allStatusEffects = new ArrayList<>();
            // 根据 ResourceLocation 获取对应的 MobEffect 实例
            for (ResourceLocation effectId : allEffectIds) {
                MobEffect effect = ForgeRegistries.MOB_EFFECTS.getValue(effectId);
                if (effect != null) {
                    // 通过 getCategory() 判断是否为有害效果
                    if (effect.getCategory() == MobEffectCategory.HARMFUL) {

                        allStatusEffects.add(effect);
                    }
                }
            }
            List<MobEffect> selectedEffects = new ArrayList<>();
            Random random = new Random();
            while (selectedEffects.size() < count && !allStatusEffects.isEmpty()) {
                int index = random.nextInt(allStatusEffects.size());
                MobEffect effect = allStatusEffects.get(index);
                selectedEffects.add(effect);
                allStatusEffects.remove(index);
            }
            return selectedEffects;
        }

    }

    /**
     * 给目标实体添加负面效果
     *
     * @param entity        目标实体
     * @param statusEffects 负面效果列表
     * @param level         词条等级
     */
    private static void applyStatusEffects(LivingEntity entity, List<MobEffect> statusEffects, int level) {
        for (MobEffect effect : statusEffects) {
            entity.addEffect(new MobEffectInstance(effect, level * 20 * 100, 4)); // 持续 100 秒
        }
    }



    }




