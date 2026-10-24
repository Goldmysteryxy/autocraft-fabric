package com.example.autocraft;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.List;

public class AutoCraftManager {
    public static void process(PlayerEntity player) {
        if (player == null || player.getWorld().isClient) {
            return;
        }

        if (!(player instanceof ServerPlayerEntity)) {
            return;
        }

        ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
        PlayerInventory inventory = player.getInventory();

        boolean changed;
        do {
            changed = false;

            for (AutoCraftRecipe recipe : AutoCraftRecipes.ALL) {
                if (!AutoCraftConfig.isEnabled(recipe.id())) {
                    continue;
                }

                if (craftRecipe(inventory, player, recipe)) {
                    changed = true;
                    break;
                }
            }
        } while (changed);
    }

    private static boolean craftRecipe(PlayerInventory inventory, PlayerEntity player, AutoCraftRecipe recipe) {
        List<Integer> slots = findInputSlots(inventory, recipe.input(), recipe.inputCount());

        if (slots.isEmpty()) {
            return false;
        }

        int remaining = recipe.inputCount();

        for (int slot : slots) {
            ItemStack stack = inventory.getStack(slot);
            int toRemove = Math.min(stack.getCount(), remaining);
            stack.decrement(toRemove);
            remaining -= toRemove;

            if (stack.isEmpty()) {
                inventory.setStack(slot, ItemStack.EMPTY);
            }

            if (remaining <= 0) {
                break;
            }
        }

        ItemStack output = new ItemStack(recipe.output(), recipe.outputCount());

        if (inventory.insertStack(output)) {
            return true;
        }

        player.dropItem(output, false);
        return true;
    }

    private static List<Integer> findInputSlots(PlayerInventory inventory, Item input, int needed) {
        List<Integer> slots = new ArrayList<>();
        int total = 0;

        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (stack.isOf(input)) {
                total += stack.getCount();
                slots.add(i);

                if (total >= needed) {
                    return slots;
                }
            }
        }

        return new ArrayList<>();
    }
}
