package com.mememan.blitz.core.template;

import com.mememan.blitz.Blitz;
import com.mememan.nexus.asm.annotations.RegistrarEntry;
import com.mememan.nexus.property_wrapper.impl.specialised.language.SpecializedLanguagePropertyWrapper;
import net.minecraft.world.level.block.Blocks;

@RegistrarEntry
public final class BlitzLocalizationTemplates { // TODO Nexus v1.1.0 already supports dummy property wrappers for this purpose. For now, let's fake it to get datagen working automatically without any hassle.
    public static final Object CONFIG_BUTTONS = new SpecializedLanguagePropertyWrapper<>(() -> Blocks.AIR, Blitz.MOD_ID)
            .builder()
            .bypassDefaultTranslation()
            .withAdditionalLocalizationKey("blitz.config.title", "Blitz Config")
            .withAdditionalLocalizationKey("blitz.config.save", "Save & Exit")
            .withAdditionalLocalizationKey("blitz.config.save.description", "Saves the current config and exits the config screen. Press [ESC] to discard changes.")
            .withAdditionalLocalizationKey("blitz.config.reset", "R")
            .withAdditionalLocalizationKey("blitz.config.reset.description", "Resets all config keys to their default values.")
            .withAdditionalLocalizationKey("blitz.config.reset_value", "Reset this config key to its default value.")
            .withAdditionalLocalizationKey("config.blitz.min_val_option", "%s - %s [Min]")
            .withAdditionalLocalizationKey("config.blitz.max_val_option", "%s - %s [Max]")
            .withAdditionalLocalizationKey("config.blitz.standard_val_option", "%s - %s")
            .buildAndGet();
}
