package com.endtinker.Register;


import com.endtinker.Endtinker;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import slimeknights.mantle.registration.deferred.FluidDeferredRegister;
import slimeknights.mantle.registration.object.FluidObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static slimeknights.tconstruct.fluids.block.BurningLiquidBlock.createBurning;

public class FluidRegister {

    public static final FluidDeferredRegister FLUIDS = new FluidDeferredRegister(Endtinker.MODID);
    protected static Map<FluidObject<ForgeFlowingFluid>, Boolean> FLUID_MAP = new HashMap<>();

    public static Set<FluidObject<ForgeFlowingFluid>> getFluids() {
        return FLUID_MAP.keySet();
    }

    public static Map<FluidObject<ForgeFlowingFluid>, Boolean> getFluidMap() {
        return FLUID_MAP;
    }

    private static FluidObject<ForgeFlowingFluid> registerHotBurning(FluidDeferredRegister register, String name, int temp, int lightLevel, int burnTime, float damage, boolean gas) {
        FluidObject<ForgeFlowingFluid> object = register.register(name)
                .type(hot(name, temp, gas))
                .bucket()
                .block(createBurning(MapColor.COLOR_GRAY, lightLevel, burnTime, damage))
                .commonTag()
                .flowing();
        FLUID_MAP.put(object, gas);
        return object;
    }

    public static final FluidObject<ForgeFlowingFluid> DOT_METAL = registerHotBurning(FLUIDS, "dot_metal", 13000, 15, 200*300000, 10f, false);


    private static FluidType.Properties hot(String name, int temp, boolean gas) {
        return FluidType.Properties.create()
                .density(gas ? -2000 : 2000)
                .viscosity(10000)
                .temperature(temp)
                .descriptionId("fluid." + Endtinker.MODID + "." + name)
                .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA)
                .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA)
                .motionScale(0.00111111111111111111f)
                .canSwim(false)
                .canDrown(false)
                .pathType(BlockPathTypes.LAVA)
                .adjacentPathType(null);
    }

}




