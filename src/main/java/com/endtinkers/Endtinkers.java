package com.endtinkers;

import com.endtinkers.effect.CustomDamageEffect;
import com.endtinkers.modifier.register.CustomModifier;
import com.endtinkers.modifier.register.ModifierRegister;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.modifiers.util.ModifierDeferredRegister;
import slimeknights.tconstruct.library.modifiers.util.StaticModifier;

@Mod(Endtinkers.MODID)
public class Endtinkers {


    public static final String MODID = "endtinkers";

    private static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, MODID);

    public static final RegistryObject<Block> dot_block = BLOCKS.register("dot_block", () -> new Block(BlockBehaviour.Properties.of().strength(4.0f).sound(SoundType.COPPER)));
    public static final RegistryObject<Item> DOT_BLOCK_ITEM = ITEMS.register("dot_block", () -> new BlockItem(dot_block.get(), new Item.Properties()));
    public static final RegistryObject<Item> dot_apple = ITEMS.register("dot_apple", () -> new Item(new Item.Properties().food(new FoodProperties.Builder().alwaysEat().nutrition(20).saturationMod(20f).effect(new MobEffectInstance(MobEffects.REGENERATION,600*20,4),1f).build())));
    public static final RegistryObject<Item> dot_item = ITEMS.register("dot_item", () -> new Item(new Item.Properties()));



    public static ModifierDeferredRegister MODIFIERS = ModifierDeferredRegister.create(MODID);
    public static final StaticModifier<ModifierRegister.Rude> rude = MODIFIERS.register("rude", ModifierRegister.Rude::new);
    public static final StaticModifier<CustomModifier.dot> dot = MODIFIERS.register("dot", CustomModifier.dot::new);

    public static final RegistryObject<CreativeModeTab> mytab = CREATIVE_MODE_TAB.register("endtinkers_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("Endtinkers_tab"))
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> dot_apple.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(DOT_BLOCK_ITEM.get());
                output.accept(dot_item.get());
                output.accept(dot_apple.get());
            }).build()
    );




    public Endtinkers() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();


        modEventBus.addListener(this::commonSetup);

        BLOCKS.register(modEventBus);

        ITEMS.register(modEventBus);

        CREATIVE_MODE_TAB.register(modEventBus);


        MinecraftForge.EVENT_BUS.register(this);

        Endtinkers.MODIFIERS.register(modEventBus);



        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);


        CustomDamageEffect.register(modEventBus);



    }



    private void commonSetup(final FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");
        LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));

        if (Config.logDirtBlock) LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));

        LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber);

        Config.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));
    }


    @SubscribeEvent
    public static void gatherData(final GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        ExistingFileHelper fileHelper = event.getExistingFileHelper();
        if (event.includeClient()) {
        }
        if (event.includeServer()) {
        }
    }
        public void onServerStarting(ServerStartingEvent event) {

        LOGGER.info("HELLO from server starting");
    }


    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {

            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }
}
