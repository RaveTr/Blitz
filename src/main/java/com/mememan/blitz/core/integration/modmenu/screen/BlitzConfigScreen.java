package com.mememan.blitz.core.integration.modmenu.screen;

import com.google.gson.JsonPrimitive;
import com.mememan.blitz.core.config.JsonConfig;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.ChatFormatting;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

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
        list.render(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    protected void init() {
        super.init();

        this.list = new OptionsList(minecraft, width, height, 34, height - 38, 30);

        // Core Buttons
        addRenderableWidget(Button.builder(Component.translatable("blitz.config.save"), button -> {
                    JsonConfig.reloadConfig();
                    minecraft.setScreen(parentScreen);
                }).tooltip(Tooltip.create(Component.translatable("blitz.config.save.description")))
                .bounds(width / 2 - 100, height - 35, 200, 20)
                .build());

      /*  addRenderableWidget(Button.builder(Component.translatable("blitz.config.reset").withStyle(ChatFormatting.BOLD, ChatFormatting.RED), button -> {
                    JsonConfig.resetConfig();
                }).tooltip(Tooltip.create(Component.translatable("blitz.config.reset.description")))
                .bounds(width / 2 + 120, height - 35, 20, 20)
                .build()); */ // TODO Fix ts later

        // Config
        List<OptionInstance<?>> configOptions = new ObjectArrayList<>();

        JsonConfig.getConfigValues().forEach(configValue -> populateOptions(configOptions, configValue));

        list.addSmall(configOptions.toArray(OptionInstance[]::new));

        // Scroll Widget
        addWidget(list);
    }

    @Override
    public void onClose() {
        minecraft.setScreen(parentScreen);
    }

    public Screen getParentScreen() {
        return parentScreen;
    }

    protected static void populateOptions(List<OptionInstance<?>> options, JsonConfig.ConfigValue configValue) {
        JsonPrimitive defRefVal = configValue.getDefaultValue();

        if (defRefVal.isBoolean()) {
            options.add(
                    OptionInstance.createBoolean(
                            configValue.getFormattedValueName(),
                            val -> Tooltip.create(Component.literal(configValue.getDescription())),
                            configValue.get().getAsBoolean(),
                            val -> JsonConfig.updateConfigValue(configValue, new JsonPrimitive(val))
                    )
            );
        }

        if (defRefVal.isNumber()) { // TODO Should probably handle doubles and ints separately, but eh, whatever (fn)
            int min = configValue.getMin() != null ? configValue.getMin().intValue() : 0;
            int max = configValue.getMax() != null ? configValue.getMax().intValue() : 1024;

            options.add(
                    new OptionInstance<>(
                            configValue.getFormattedValueName(),
                            val -> Tooltip.create(Component.literal(configValue.getDescription())),
                            (labelComponent, val) -> {
                                return val <= min
                                        ? Component.translatable("config.blitz.min_val_option", labelComponent, val)
                                        : val >= max
                                        ? Component.translatable("config.blitz.max_val_option", labelComponent, val)
                                        : Component.translatable("config.blitz.standard_val_option", labelComponent, val);
                            },
                            new OptionInstance.IntRange(min, max),
                            defRefVal.getAsInt(),
                            val -> JsonConfig.updateConfigValue(configValue, new JsonPrimitive(val))
                    )
            );
        }
    }
}
