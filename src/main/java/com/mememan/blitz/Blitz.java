package com.mememan.blitz;

import com.mememan.blitz.core.config.JsonConfig;
import com.mememan.nexus.platform.NexusServices;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Blitz implements ModInitializer {
	public static final String MOD_ID = "blitz";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
        if (!NexusServices.PLATFORM_MANAGER.isRunningDataGen()) JsonConfig.initializeConfig();
	}

    public static ResourceLocation prefix(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}