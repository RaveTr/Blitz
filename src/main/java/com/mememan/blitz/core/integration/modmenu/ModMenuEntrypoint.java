package com.mememan.blitz.core.integration.modmenu;

import com.mememan.blitz.core.config.JsonConfig;
import com.mememan.blitz.core.integration.modmenu.screen.BlitzConfigScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

/**
 * Entrypoint to add integration with the Mod Menu mod. Functionally, this just adds a config screen for Blitz.
 *
 * @see BlitzConfigScreen
 * @see JsonConfig
 */
public class ModMenuEntrypoint implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return BlitzConfigScreen::new;
    }
}