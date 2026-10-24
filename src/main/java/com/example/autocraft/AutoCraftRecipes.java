package com.example.autocraft;

import net.minecraft.item.Items;

import java.util.List;

public class AutoCraftRecipes {
    public static final List<AutoCraftRecipe> ALL = List.of(
        new AutoCraftRecipe("oak_log", "Oak Log -> Oak Planks", Items.OAK_LOG, 1, Items.OAK_PLANKS, 4),
        new AutoCraftRecipe("spruce_log", "Spruce Log -> Spruce Planks", Items.SPRUCE_LOG, 1, Items.SPRUCE_PLANKS, 4),
        new AutoCraftRecipe("birch_log", "Birch Log -> Birch Planks", Items.BIRCH_LOG, 1, Items.BIRCH_PLANKS, 4),
        new AutoCraftRecipe("jungle_log", "Jungle Log -> Jungle Planks", Items.JUNGLE_LOG, 1, Items.JUNGLE_PLANKS, 4),
        new AutoCraftRecipe("acacia_log", "Acacia Log -> Acacia Planks", Items.ACACIA_LOG, 1, Items.ACACIA_PLANKS, 4),
        new AutoCraftRecipe("dark_oak_log", "Dark Oak Log -> Dark Oak Planks", Items.DARK_OAK_LOG, 1, Items.DARK_OAK_PLANKS, 4),

        new AutoCraftRecipe("gold_nugget", "Gold Nuggets -> Gold Ingots", Items.GOLD_NUGGET, 9, Items.GOLD_INGOT, 1),
        new AutoCraftRecipe("gold_ingot", "Gold Ingots -> Gold Blocks", Items.GOLD_INGOT, 9, Items.GOLD_BLOCK, 1),
        new AutoCraftRecipe("iron_nugget", "Iron Nuggets -> Iron Ingots", Items.IRON_NUGGET, 9, Items.IRON_INGOT, 1),
        new AutoCraftRecipe("iron_ingot", "Iron Ingots -> Iron Blocks", Items.IRON_INGOT, 9, Items.IRON_BLOCK, 1),

        new AutoCraftRecipe("sand", "Sand -> Glass", Items.SAND, 1, Items.GLASS, 1)
    );
}
