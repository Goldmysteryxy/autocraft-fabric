package com.example.autocraft;

import net.minecraft.item.Item;

public record AutoCraftRecipe(
    String id,
    String label,
    Item input,
    int inputCount,
    Item output,
    int outputCount
) {
}
