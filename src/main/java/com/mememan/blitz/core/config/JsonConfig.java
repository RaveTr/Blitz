package com.mememan.blitz.core.config;

import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mememan.blitz.Blitz;
import com.mememan.nexus.loader.ModSide;
import com.mememan.nexus.platform.NexusServices;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.function.Supplier;

/**
 * Statically-initialized Json-based config for Blitz.
 */
public final class JsonConfig {
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();
    private static final Map<ModSide, List<ConfigValue>> CONFIG_VALUES_BY_SIDE = new EnumMap<>(ModSide.class); // Store whatever configs we have in memory on every startup, then write to the file on the appropriate *physical* side
    private static final Set<ConfigValue> CURRENT_CONFIG_VALUES = new ObjectOpenCustomHashSet<>(new Hash.Strategy<>() {
        @Override
        public int hashCode(ConfigValue configValue) {
            return configValue.getValueName().hashCode();
        }

        @Override
        public boolean equals(ConfigValue configValue1, ConfigValue configValue2) {
            return configValue1 != null && configValue2 != null && Objects.equals(configValue1.getValueName(), configValue2.getValueName());
        }
    });
    private static JsonObject CONFIG_JSON;
    public static final File CONFIG_FILE = NexusServices.PLATFORM_MANAGER.getGamePathWrapper().getConfigDir().resolve(Blitz.MOD_ID.concat(".json")).toFile();

    // Config Values
    public static final ConfigValue ENABLE_SCREEN_SHAKE = registerClientConfigValue("enable_screen_shake", new JsonPrimitive(true), "Whether or not screen shake should be enabled (only really present when using the Ore Sonar at the moment).");

    public static final ConfigValue ORE_SONAR_SCAN_RADIUS = registerConfigValue("ore_sonar_scan_radius", new JsonPrimitive(1024), "The radius (in blocks) in which the Ore Sonar should scan for ores.");
    public static final ConfigValue RUBBLE_HAMMER_MINING_RADIUS = registerConfigValue("rubble_hammer_mining_radius", new JsonPrimitive(1), "The radius (in blocks) around the center block being mined by the Rubble Hammer that should be destroyed.");
    public static final ConfigValue RUBBLE_HAMMER_MINING_DEPTH = registerConfigValue("rubble_hammer_mining_depth", new JsonPrimitive(1), "The depth (in blocks) from the center block being mined by the Rubble Hammer (relative to the direction in which the mining entity/player is looking) that should be destroyed.");

    private JsonConfig() {

    }

    public static void initializeConfig() {
        checkAndWriteDefaultConfig();
        loadConfigFromDisk();
    }

    public static void reloadConfig() {
        updateConfigFromMemory();

        saveConfigToDisk();
        loadConfigFromDisk();
        writeConfigFromDisk();
    }

    public static void updateConfigValue(ConfigValue targetValue, JsonPrimitive newValue) {
        targetValue.currentValue = newValue;
    }

    private static void checkAndWriteDefaultConfig() {
        if (CONFIG_JSON == null) CONFIG_JSON = new JsonObject();

        if (!CONFIG_FILE.exists()) {
            writeConfigFromMemory();
            saveConfigToDisk();
        } else {
            loadConfigFromDisk();
            writeConfigFromDisk();
        }
    }

    private static void writeConfigFromMemory() {
        CONFIG_VALUES_BY_SIDE.forEach((logicalSide, configValues) -> {
            if (!NexusServices.PLATFORM_MANAGER.getEnvironmentSide().pertainsTo(logicalSide)) return; // Avoid writing config configValues to the CONFIG_JSON and storing them in CURRENT_CONFIG_VALUES

            configValues.forEach(configValue -> {
                if (configValue != null) {
                    configValue.serializeTo(CONFIG_JSON);
                    CURRENT_CONFIG_VALUES.add(configValue);
                }
            });
        });
    }

    private static void writeConfigFromDisk() {
        if (CONFIG_JSON != null) {
            CURRENT_CONFIG_VALUES.clear();

            CONFIG_JSON.entrySet().forEach(configEntry -> {
                CURRENT_CONFIG_VALUES.add(new ConfigValue(
                        configEntry.getKey(), // Name/Key
                        configEntry.getValue().getAsJsonObject().get("value").getAsJsonPrimitive(),
                        configEntry.getValue().getAsJsonObject().get("description").getAsString())
                );
            });
        }
    }

    private static void updateConfigFromMemory() {
        CURRENT_CONFIG_VALUES.forEach(configValue -> {
            if (!CONFIG_JSON.has(configValue.getValueName()) || !CONFIG_JSON.get(configValue.getValueName()).getAsJsonObject().get("value").getAsJsonPrimitive().equals(configValue.get())) {
                configValue.serializeTo(CONFIG_JSON);
            }
        });
    }

    private static void saveConfigToDisk() {
        try {
            Files.write(CONFIG_FILE.toPath(), GSON.toJson(CONFIG_JSON).getBytes());
        } catch (IOException e) {
            Blitz.LOGGER.error("Failed to write config file!", e);
        }
    }

    private static void loadConfigFromDisk() {
        try {
            CONFIG_JSON = GSON.fromJson(Files.readString(CONFIG_FILE.toPath()), JsonObject.class);
        } catch (IOException e) {
            Blitz.LOGGER.error("Failed to read config file!", e);
        }
    }

    private static ConfigValue registerConfigValue(String valueName, JsonPrimitive defaultValue, @Nullable String description) {
        return registerConfigValue(valueName, defaultValue, description, ModSide.COMMON);
    }

    private static ConfigValue registerConfigValue(String valueName, JsonPrimitive defaultValue) {
        return registerConfigValue(valueName, defaultValue, null);
    }

    private static ConfigValue registerServerConfigValue(String valueName, JsonPrimitive defaultValue, @Nullable String description) {
        return registerConfigValue(valueName, defaultValue, description, ModSide.SERVER);
    }

    private static ConfigValue registerServerConfigValue(String valueName, JsonPrimitive defaultValue) {
        return registerServerConfigValue(valueName, defaultValue, null);
    }

    private static ConfigValue registerClientConfigValue(String valueName, JsonPrimitive defaultValue, @Nullable String description) {
        return registerConfigValue(valueName, defaultValue, description, ModSide.CLIENT);
    }

    private static ConfigValue registerClientConfigValue(String valueName, JsonPrimitive defaultValue) {
        return registerClientConfigValue(valueName, defaultValue, null);
    }

    private static ConfigValue registerConfigValue(String valueName, JsonPrimitive defaultValue, @Nullable String description, ModSide logicalSide) {
        ConfigValue configValueToRegister = new ConfigValue(valueName, defaultValue, description);

        CONFIG_VALUES_BY_SIDE.computeIfAbsent(logicalSide, k -> new ObjectArrayList<>()).add(configValueToRegister);

        return configValueToRegister;
    }

    public static ImmutableSet<ConfigValue> getConfigValues() {
        return ImmutableSet.copyOf(CURRENT_CONFIG_VALUES);
    }

    public static class ConfigValue implements Supplier<JsonPrimitive> {
        protected final String valueName;
        protected final JsonPrimitive defaultValue;
        @Nullable
        protected final String description;
        protected JsonPrimitive currentValue;

        public ConfigValue(String valueName, JsonPrimitive defaultValue, @Nullable String description) {
            this.valueName = valueName;
            this.defaultValue = defaultValue;
            this.description = description;
        }

        @Override
        public JsonPrimitive get() {
            return (currentValue = CONFIG_JSON.has(valueName) ? CONFIG_JSON.get(valueName).getAsJsonObject().get("value").getAsJsonPrimitive() : defaultValue) == null
                    ? (currentValue = defaultValue)
                    : currentValue;
        }

        public void serializeTo(JsonObject configFileJson) {
            JsonObject valueJson = configFileJson.has(valueName) ? configFileJson.get(valueName).getAsJsonObject() : new JsonObject();

            valueJson.add("value", get());
            valueJson.addProperty("description", description == null ? "No description provided." : description);

            configFileJson.add(valueName, valueJson);
        }

        public String getValueName() {
            return valueName;
        }

        public JsonPrimitive getDefaultValue() {
            return defaultValue;
        }

        @Nullable
        public String getDescription() {
            return description;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof ConfigValue other)) return false;

            return Objects.equals(valueName, other.valueName) && Objects.equals(defaultValue, other.defaultValue) && Objects.equals(currentValue, other.currentValue);
        }
    }
}
