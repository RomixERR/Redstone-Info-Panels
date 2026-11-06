package com.redstoneinfopanels;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;


// The value here should match an entry in the META-INF/mods.toml file
@Mod(RedstoneInfoPanels.MODID)
public class RedstoneInfoPanels
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "redstoneinfopanels";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MODID);
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, MODID);

    public static final RegistryObject<MenuType<RedstoneInfoPanelMenu>> REDSTONE_INFO_PANEL_MENU =
            MENUS.register(
                    "redstone_info_panel",
                    () -> net.minecraftforge.common.extensions.IForgeMenuType.create(
                            RedstoneInfoPanelMenu::new
                    )
            );

    public static final RegistryObject<Block> REDSTONE_INFO_PANEL = BLOCKS.register("redstone_info_panel", () -> new RedstoneInfoPanelBlock(BlockBehaviour.Properties.of(Material.STONE).strength(1.0F)));
    public static final RegistryObject<Block> WIRELESS_REDSTONE = BLOCKS.register(
            "wireless_redstone",
            () -> new WirelessRedstoneBlock(
                    BlockBehaviour.Properties.of(Material.STONE).strength(1.0F).noOcclusion()
            )
    );
    public static final RegistryObject<Item> REDSTONE_INFO_PANEL_ITEM = ITEMS.register("redstone_info_panel", () -> new BlockItem(REDSTONE_INFO_PANEL.get(), new Item.Properties().tab(CreativeModeTab.TAB_REDSTONE)));
    public static final RegistryObject<Item> WIRELESS_REDSTONE_ITEM = ITEMS.register(
            "wireless_redstone",
            () -> new BlockItem(
                    WIRELESS_REDSTONE.get(),
                    new Item.Properties().tab(CreativeModeTab.TAB_REDSTONE)
            )
    );


    public static final RegistryObject<Item> LINKER = ITEMS.register(
            "linker",
            () -> new LinkerItem(
                    new Item.Properties()
                            .stacksTo(1)
                            .tab(CreativeModeTab.TAB_TOOLS)
            )
    );

    public static final RegistryObject<BlockEntityType<RedstoneInfoPanelBlockEntity>> REDSTONE_INFO_PANEL_BLOCK_ENTITY =
            BLOCK_ENTITY_TYPES.register(
                    "redstone_info_panel",
                    () -> BlockEntityType.Builder.of(
                            RedstoneInfoPanelBlockEntity::new,
                            REDSTONE_INFO_PANEL.get()
                    ).build(null)
            );

    public static final RegistryObject<BlockEntityType<WirelessRedstoneBlockEntity>> WIRELESS_REDSTONE_BLOCK_ENTITY =
            BLOCK_ENTITY_TYPES.register(
                    "wireless_redstone",
                    () -> BlockEntityType.Builder.of(
                            WirelessRedstoneBlockEntity::new,
                            WIRELESS_REDSTONE.get()
                    ).build(null)
            );

    public RedstoneInfoPanels(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        BLOCK_ENTITY_TYPES.register(modEventBus);
        MENUS.register(modEventBus);
        ModNetwork.register();
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        context.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        // Some common setup code
        LOGGER.info("=== REDSTONE INFO PANELS: COMMON SETUP ===");

        if (Config.logDirtBlock)
            LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));

        LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber);

        Config.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
            event.enqueueWork(() -> {
                BlockEntityRenderers.register(
                        RedstoneInfoPanels.REDSTONE_INFO_PANEL_BLOCK_ENTITY.get(),
                        RedstoneInfoPanelRenderer::new
                );

                MenuScreens.register(
                        RedstoneInfoPanels.REDSTONE_INFO_PANEL_MENU.get(),
                        RedstoneInfoPanelScreen::new
                );
            });
        }
    }
}
