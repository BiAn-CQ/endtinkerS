package com.endtinkers.modifier.register;

import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.behavior.ToolDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;

import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;

public class DurableModifier extends Modifier {
                public static class dot3 extends Modifier implements ToolDamageModifierHook {


    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        super.registerHooks(hookBuilder);
        hookBuilder.addHook(this, ModifierHooks.TOOL_DAMAGE);
    }

    @Override
    public int getPriority() {
        return 300;
    }

    @Override
    public int onDamageTool(@NotNull IToolStackView tool, @NotNull ModifierEntry modifier, int amount, @Nullable LivingEntity holder) {
        return 0;
    }
}
}


