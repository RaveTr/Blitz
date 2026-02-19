package com.mememan.blitz.core.datagen;

import com.mememan.blitz.Blitz;
import com.mememan.nexus.asm.annotations.DatagenRegistrarEntry;
import com.mememan.nexus.datagen.ModDatagenConfig;
import com.mememan.nexus.platform.NexusServices;

/**
 * Holder {@code class} responsible for registering and storing the singleton {@link ModDatagenConfig} instance for
 * Blitz.
 */
@DatagenRegistrarEntry
public final class BlitzDatagen {
    public static final ModDatagenConfig CONFIG = NexusServices.DATA_GENERATOR.registerConfigForMod(ModDatagenConfig.defaultConfig(Blitz.MOD_ID));
}
