package com.endtinkers.modifier.register;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static net.minecraft.core.Registry.*;


public class CustomModifier {


public static class Rude2 extends Modifier implements MeleeDamageModifierHook {

    // 构造方法
    public Rude2() {
    }

    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {

        hookBuilder.addHook(this, ModifierHooks.MELEE_DAMAGE);
    }


    @Override
    public float getMeleeDamage(@NotNull IToolStackView tool, @NotNull ModifierEntry modifier, ToolAttackContext context, float baseDamage, float damage) {
        // 获取被攻击的目标实体
        LivingEntity enemy = context.getLivingTarget();
        if (enemy != null) {
            // 获取当前词条的等级
            int level = modifier.getLevel();
            // 获取武器的面板攻击值
            float weaponDamage = tool.getDamage() * 1.5f;

            // 随机选择（词条等级）数种负面效果
            List<MobEffect> statusEffects = getRandomStatusEffects(level);
            // 负面效果的总种数
            int effectCount = statusEffects.size();

            // 给目标实体添加负面效果
            applyStatusEffects(enemy, statusEffects, level);

            // 计算持续伤害
            float continuousDamage = level * weaponDamage * effectCount;

            // 检查目标实体身上的负面效果数量
            if (enemy.getActiveEffects().size() >= 15) {
                // 若目标获得 15 种以上负面效果时则直接秒杀
                enemy.setHealth(0);
            } else if (enemy.getActiveEffects().size() >= 10) {
                // 若目标获得 10 种以上的负面效果时则持续伤害 ^ 3
                continuousDamage = (float) Math.pow(continuousDamage, 3);
            }

            // 持续（词条等级）×（负面效果总种数）秒，转换为游戏刻（1 秒 = 20 刻）
            int duration = level * effectCount * 20;

            // 给目标实体添加自定义的持续伤害效果
            float finalContinuousDamage = continuousDamage;
            enemy.addEffect(new MobEffectInstance(CustomDamageEffect.INSTANCE, duration, 0) {
                public void applyEffectTick(LivingEntity entity, int amplifier) {
                    if (entity.tickCount % 20 == 0) { // 每秒触发一次
                        entity.hurt(entity.damageSources().magic(), finalContinuousDamage);
                    }
                }
            });
        }
        return damage;
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
        for (Object effectId : allEffectIds) {
            MobEffect effect =  ForgeRegistries.MOB_EFFECTS.getValue((ResourceLocation) effectId);
            if (effect != null) {
                allStatusEffects.add(effect);
            }
        }
        List<MobEffect> selectedEffects = new ArrayList<>();
        Random random = new Random();
        while (selectedEffects.size() < count && !allStatusEffects.isEmpty()) {
            int index = random.nextInt(allStatusEffects.size());
            MobEffect effect = allStatusEffects.get(index);
            // 通过 getCategory() 判断是否为有害效果
            if (effect.getCategory() == MobEffectCategory.HARMFUL) {
                selectedEffects.add(effect);
            }
            allStatusEffects.remove(index);
        }
        return selectedEffects;
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
            entity.addEffect(new MobEffectInstance(effect, level * 20 * 10, 0)); // 持续 10 秒
        }
    }

    // 自定义一个用于造成持续伤害的状态效果
    public static class CustomDamageEffect extends MobEffect {
        public static final CustomDamageEffect INSTANCE = new CustomDamageEffect();

        private CustomDamageEffect() {
            super(MobEffectCategory.HARMFUL, 0x000000); // 黑色
        }

        @Override
        public boolean isDurationEffectTick(int duration, int amplifier) {
            return true;
        }
    }
    }

}