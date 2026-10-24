package com.example.autocraft;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class AutoCraftConfigScreen extends Screen {
    private final Screen parent;

    public AutoCraftConfigScreen(Screen parent) {
        super(Text.literal("AutoCraft"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int x = 30;
        int y = 30;
        int width = 280;
        int height = 20;

        for (AutoCraftRecipe recipe : AutoCraftRecipes.ALL) {
            boolean enabled = AutoCraftConfig.isEnabled(recipe.id());

            ButtonWidget button = ButtonWidget.builder(
                Text.literal((enabled ? "[ON] " : "[OFF] ") + recipe.label()),
                btn -> {
                    AutoCraftConfig.toggle(recipe.id());
                    btn.setMessage(Text.literal((AutoCraftConfig.isEnabled(recipe.id()) ? "[ON] " : "[OFF] ") + recipe.label()));
                }
            ).dimensions(x, y, width, height).build();

            addDrawableChild(button);
            y += 24;
        }

        addDrawableChild(
            ButtonWidget.builder(Text.literal("Close"), btn -> this.client.setScreen(parent))
                .dimensions(x, y + 10, 120, 20)
                .build()
        );
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
    }
}
