package com.example.autocraft;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class AutoCraftConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Type MAP_TYPE = new TypeToken<Map<String, Boolean>>() {}.getType();

    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("autocraft.json");
    private static final Map<String, Boolean> STATE = new HashMap<>();

    public static void load() {
        STATE.clear();

        if (Files.notExists(CONFIG_PATH)) {
            save();
            return;
        }

        try {
            String json = Files.readString(CONFIG_PATH, StandardCharsets.UTF_8);
            Map<String, Boolean> loaded = GSON.fromJson(json, MAP_TYPE);
            if (loaded != null) {
                STATE.putAll(loaded);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            Files.writeString(CONFIG_PATH, GSON.toJson(STATE), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isEnabled(String recipeId) {
        return STATE.getOrDefault(recipeId, false);
    }

    public static void setEnabled(String recipeId, boolean enabled) {
        STATE.put(recipeId, enabled);
        save();
    }

    public static void toggle(String recipeId) {
        setEnabled(recipeId, !isEnabled(recipeId));
    }
}
