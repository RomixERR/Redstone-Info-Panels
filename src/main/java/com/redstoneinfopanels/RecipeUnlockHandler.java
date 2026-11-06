package com.redstoneinfopanels;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = RedstoneInfoPanels.MODID)
public class RecipeUnlockHandler {

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {

        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        List<Recipe<?>> recipes = new ArrayList<>();

        addRecipe(player, recipes, "redstone_info_panel");
        addRecipe(player, recipes, "wireless_redstone");
        addRecipe(player, recipes, "linker");

        player.awardRecipes(recipes);
    }

    private static void addRecipe(
            ServerPlayer player,
            List<Recipe<?>> recipes,
            String recipeName) {

        ResourceLocation id = new ResourceLocation(
                RedstoneInfoPanels.MODID,
                recipeName
        );

        player.server.getRecipeManager()
                .byKey(id)
                .ifPresent(recipes::add);
    }
}