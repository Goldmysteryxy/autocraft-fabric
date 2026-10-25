package com.example.autocraft;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class AutoCraftCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
            CommandManager.literal("autocraft")
                .executes(AutoCraftCommand::showStatus)
                .then(CommandManager.literal("list").executes(AutoCraftCommand::showStatus))
                .then(CommandManager.literal("toggle")
                    .then(CommandManager.argument("selector", StringArgumentType.word())
                        .suggests(AutoCraftCommand::suggestRecipes)
                        .executes(ctx -> toggleRecipe(ctx, ctx.getArgument("selector", String)))))
                .then(CommandManager.literal("all")
                    .then(CommandManager.argument("state", StringArgumentType.word())
                        .suggests((ctx, builder) -> {
                            builder.suggest("on");
                            builder.suggest("off");
                            return builder.buildFuture();
                        })
                        .executes(ctx -> setStateForAll(ctx, ctx.getArgument("state", String)))))
                .then(CommandManager.argument("selector", StringArgumentType.word())
                    .suggests(AutoCraftCommand::suggestRecipes)
                    .executes(ctx -> showStatusForSelector(ctx, ctx.getArgument("selector", String)))
                    .then(CommandManager.argument("state", StringArgumentType.word())
                        .suggests((ctx, builder) -> {
                            builder.suggest("on");
                            builder.suggest("off");
                            return builder.buildFuture();
                        })
                        .executes(ctx -> setStateForSelector(
                            ctx,
                            ctx.getArgument("selector", String),
                            ctx.getArgument("state", String)
                        ))))
        );
    }

    private static int showStatus(CommandContext<ServerCommandSource> ctx) {
        StringBuilder msg = new StringBuilder("AutoCraft recipes:");
        for (AutoCraftRecipe recipe : AutoCraftRecipes.ALL) {
            msg.append("\n- ")
                .append(recipe.label())
                .append(": ")
                .append(AutoCraftConfig.isEnabled(recipe.id()) ? "ON" : "OFF");
        }

        ctx.getSource().sendFeedback(() -> Text.literal(msg.toString()), false);
        return 1;
    }

    private static int showStatusForSelector(CommandContext<ServerCommandSource> ctx, String selector) {
        List<AutoCraftRecipe> matches = findMatchingRecipes(selector);
        if (matches.isEmpty()) {
            ctx.getSource().sendError(Text.literal("No AutoCraft recipe matched '" + selector + "'."));
            return 0;
        }

        StringBuilder msg = new StringBuilder("AutoCraft matches for '" + selector + "':");
        for (AutoCraftRecipe recipe : matches) {
            msg.append("\n- ")
                .append(recipe.label())
                .append(": ")
                .append(AutoCraftConfig.isEnabled(recipe.id()) ? "ON" : "OFF");
        }

        ctx.getSource().sendFeedback(() -> Text.literal(msg.toString()), false);
        return matches.size();
    }

    private static int toggleRecipe(CommandContext<ServerCommandSource> ctx, String selector) {
        List<AutoCraftRecipe> matches = findMatchingRecipes(selector);
        if (matches.isEmpty()) {
            ctx.getSource().sendError(Text.literal("No AutoCraft recipe matched '" + selector + "'."));
            return 0;
        }

        for (AutoCraftRecipe recipe : matches) {
            AutoCraftConfig.setEnabled(recipe.id(), !AutoCraftConfig.isEnabled(recipe.id()));
        }

        String stateText = matches.size() == 1
            ? (AutoCraftConfig.isEnabled(matches.get(0).id()) ? "ON" : "OFF")
            : "toggled";

        ctx.getSource().sendFeedback(() -> Text.literal(
            "AutoCraft recipe(s) matched by '" + selector + "' are now " + stateText + "."
        ), false);
        return matches.size();
    }

    private static int setStateForSelector(CommandContext<ServerCommandSource> ctx, String selector, String state) {
        List<AutoCraftRecipe> matches = findMatchingRecipes(selector);
        if (matches.isEmpty()) {
            ctx.getSource().sendError(Text.literal("No AutoCraft recipe matched '" + selector + "'."));
            return 0;
        }

        boolean enabled = switch (state.toLowerCase(Locale.ROOT)) {
            case "on", "true", "enabled" -> true;
            case "off", "false", "disabled" -> false;
            default -> {
                ctx.getSource().sendError(Text.literal("State must be 'on' or 'off'."));
                yield AutoCraftConfig.isEnabled(matches.get(0).id());
            }
        };

        for (AutoCraftRecipe recipe : matches) {
            AutoCraftConfig.setEnabled(recipe.id(), enabled);
        }

        ctx.getSource().sendFeedback(() -> Text.literal(
            "AutoCraft recipe(s) matched by '" + selector + "' set to " + (enabled ? "ON" : "OFF") + "."
        ), false);
        return matches.size();
    }

    private static int setStateForAll(CommandContext<ServerCommandSource> ctx, String state) {
        boolean enabled = switch (state.toLowerCase(Locale.ROOT)) {
            case "on", "true", "enabled" -> true;
            case "off", "false", "disabled" -> false;
            default -> {
                ctx.getSource().sendError(Text.literal("State must be 'on' or 'off'."));
                yield true;
            }
        };

        for (AutoCraftRecipe recipe : AutoCraftRecipes.ALL) {
            AutoCraftConfig.setEnabled(recipe.id(), enabled);
        }

        ctx.getSource().sendFeedback(() -> Text.literal("AutoCraft is now set to " + (enabled ? "ON" : "OFF") + " for all recipes."), false);
        return AutoCraftRecipes.ALL.size();
    }

    private static List<AutoCraftRecipe> findMatchingRecipes(String selector) {
        String search = selector.toLowerCase(Locale.ROOT).trim();
        if (search.isEmpty()) {
            return new ArrayList<>();
        }

        if (search.equals("all") || search.equals("everything")) {
            return new ArrayList<>(AutoCraftRecipes.ALL);
        }

        List<AutoCraftRecipe> matches = new ArrayList<>();
        for (AutoCraftRecipe recipe : AutoCraftRecipes.ALL) {
            String id = recipe.id().toLowerCase(Locale.ROOT);
            String label = recipe.label().toLowerCase(Locale.ROOT);
            if (id.equals(search) || label.equals(search) || id.contains(search) || label.contains(search)) {
                matches.add(recipe);
            }
        }
        return matches;
    }

    private static CompletableFuture<Suggestions> suggestRecipes(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
        builder.suggest("all");
        for (AutoCraftRecipe recipe : AutoCraftRecipes.ALL) {
            builder.suggest(recipe.id());
            builder.suggest(recipe.label());
        }
        return builder.buildFuture();
    }
}
