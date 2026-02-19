package com.mememan.blitz.core.integration.modmenu.screen;

import com.mememan.blitz.core.config.JsonConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class BlitzConfigScreen extends Screen {
    public static final Component TITLE = Component.translatable("blitz.config.title").withStyle(ChatFormatting.BOLD);
    protected final Screen parentScreen;
    protected OptionsList list;

    public BlitzConfigScreen(Screen parentScreen) {
        super(TITLE);

        this.parentScreen = parentScreen;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics);
        guiGraphics.drawCenteredString(font, TITLE, width / 2, 20, 16777215);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    protected void init() {
        super.init();

        this.list = new OptionsList(minecraft, width, height, 32, height - 32, 25);

        // Core Buttons
        addRenderableWidget(Button.builder(Component.translatable("blitz.config.save"), button -> {
                    JsonConfig.reloadConfig();
                    minecraft.setScreen(parentScreen);
                }).tooltip(Tooltip.create(Component.translatable("blitz.config.save.description")))
                .bounds(width / 2 - 100, height - 35, 200, 20)
                .build());

        // Scroll widget

        // Config - Client
        JsonConfig.getConfigValues().forEach(configValue -> {
            addRenderableWidget(Button.builder(Component.translatable("blitz.config.client.%s".formatted(configValue.getValueName())), button -> {

                    }).bounds(width / 2 - 50, height - 65, 100, 20)
                    .build());
            addRenderableWidget(Button.builder(Component.literal("R").withStyle(ChatFormatting.BOLD), button -> {

                    }).bounds(width / 2 - 10, height - 95, 20, 20)
                    .tooltip(Tooltip.create(Component.translatable("blitz.config.reset_value")))
                    .build());
        });
    }

    @Override
    public void onClose() {
        minecraft.setScreen(parentScreen);
    }

    public Screen getParentScreen() {
        return parentScreen;
    }
}
