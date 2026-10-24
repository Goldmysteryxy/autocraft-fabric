package com.example.autocraft;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

public class AutoCraftMod implements ModInitializer {
    @Override
    public void onInitialize() {
        AutoCraftConfig.load();

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (var player : server.getPlayerManager().getPlayerList()) {
                AutoCraftManager.process(player);
            }
        });
    }
}
