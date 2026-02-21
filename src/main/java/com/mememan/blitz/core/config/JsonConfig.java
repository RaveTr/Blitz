package com.mememan.blitz.core.config;

import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mememan.blitz.Blitz;
import com.mememan.nexus.loader.ModSide;
import com.mememan.nexus.platform.NexusServices;
import com.mememan.nexus.util.StringUtil;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet;
import net.minecraft.util.Mth;
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

    public static final ConfigValue ORE_SONAR_SCAN_RADIUS = registerConfigValue("ore_sonar_scan_radius", new JsonPrimitive(32), "The radius (in blocks, relative to scanning pos) in which the Ore Sonar should scan for ores.", 10.0D, 2048.0D);
    public static final ConfigValue ORE_SONAR_SCAN_LIMIT = registerConfigValue("ore_sonar_scan_limit", new JsonPrimitive(512), "The maximum number of ores that the Ore Sonar should scan for.", 10.0D, 1024.0D);
    public static final ConfigValue ORE_SONAR_COOLDOWN = registerConfigValue("ore_sonar_cooldown", new JsonPrimitive(100), "The cooldown (in ticks) for the Ore Sonar.", 20.0D, 10000.0D);

    public static final ConfigValue RUBBLE_HAMMER_MINING_RADIUS = registerConfigValue("rubble_hammer_mining_radius", new JsonPrimitive(1), "The radius (in blocks) around the center block being mined by the Rubble Hammer that should be destroyed.", 0.0D, 16.0D);
    public static final ConfigValue RUBBLE_HAMMER_MINING_DEPTH = registerConfigValue("rubble_hammer_mining_depth", new JsonPrimitive(1), "The depth (in blocks) from the center block being mined by the Rubble Hammer (relative to the direction in which the mining entity/player is looking) that should be destroyed.", 0.0D, 16.0D);

    private JsonConfig() {

    }

    public static void initializeConfig() {
        checkAndWriteDefaultConfig();
    }

    public static void reloadConfig() {
        updateConfigFromMemory();

        saveConfigToDisk();
        loadConfigFromDisk();
        writeConfigFromDisk();
    }

    public static void resetConfig() {
        CURRENT_CONFIG_VALUES.clear();

        writeConfigFromMemory(true);

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
            writeConfigFromMemory(false);
            saveConfigToDisk();
        } else {
            appendMissingConfigValues();
            loadConfigFromDisk();
            writeConfigFromDisk();
        }
    }

    private static void writeConfigFromMemory(boolean setDefault) {
        CONFIG_VALUES_BY_SIDE.forEach((logicalSide, configValues) -> {
            if (!NexusServices.PLATFORM_MANAGER.getEnvironmentSide().pertainsTo(logicalSide)) return; // Avoid writing config configValues to the CONFIG_JSON and storing them in CURRENT_CONFIG_VALUES

            configValues.forEach(configValue -> {
                if (configValue != null) {
                    if (setDefault) configValue.currentValue = configValue.defaultValue;

                    configValue.serializeTo(CONFIG_JSON, setDefault);
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
                        configEntry.getValue().getAsJsonObject().get("description").getAsString(),
                        configEntry.getValue().getAsJsonObject().has("min") ? configEntry.getValue().getAsJsonObject().get("min").getAsDouble() : null,
                        configEntry.getValue().getAsJsonObject().has("max") ? configEntry.getValue().getAsJsonObject().get("max").getAsDouble() : null
                ));
            });
        }
    }

    private static void appendMissingConfigValues() {
        CONFIG_VALUES_BY_SIDE.forEach((logicalSide, configValues) -> {
            if (!NexusServices.PLATFORM_MANAGER.getEnvironmentSide().pertainsTo(logicalSide)) return;

            configValues.forEach(configValue -> {
                if (!CONFIG_JSON.has(configValue.getValueName())) {
                    configValue.serializeTo(CONFIG_JSON, false);
                }
            });
        });
    }

    private static void updateConfigFromMemory() {
        CURRENT_CONFIG_VALUES.forEach(configValue -> {
            if (!CONFIG_JSON.has(configValue.getValueName()) || !CONFIG_JSON.get(configValue.getValueName()).getAsJsonObject().get("value").getAsJsonPrimitive().equals(configValue.currentValue)) {
                configValue.serializeTo(CONFIG_JSON, true);
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

    private static ConfigValue registerConfigValue(String valueName, JsonPrimitive defaultValue, @Nullable String description, Double min, Double max) {
        return registerConfigValue(valueName, defaultValue, description, min, max, ModSide.COMMON);
    }

    private static ConfigValue registerConfigValue(String valueName, JsonPrimitive defaultValue, @Nullable String description) {
        return registerConfigValue(valueName, defaultValue, description, null, null);
    }

    private static ConfigValue registerServerConfigValue(String valueName, JsonPrimitive defaultValue, @Nullable String description, Double min, Double max) {
        return registerConfigValue(valueName, defaultValue, description, min, max, ModSide.SERVER);
    }

    private static ConfigValue registerServerConfigValue(String valueName, JsonPrimitive defaultValue, @Nullable String description) {
        return registerServerConfigValue(valueName, defaultValue, description, null, null);
    }

    private static ConfigValue registerServerConfigValue(String valueName, JsonPrimitive defaultValue) {
        return registerServerConfigValue(valueName, defaultValue, null);
    }

    private static ConfigValue registerClientConfigValue(String valueName, JsonPrimitive defaultValue, @Nullable String description, Double min, Double max) {
        return registerConfigValue(valueName, defaultValue, description, min, max, ModSide.CLIENT);
    }

    private static ConfigValue registerClientConfigValue(String valueName, JsonPrimitive defaultValue, @Nullable String description) {
        return registerClientConfigValue(valueName, defaultValue, description, null, null);
    }

    private static ConfigValue registerClientConfigValue(String valueName, JsonPrimitive defaultValue) {
        return registerClientConfigValue(valueName, defaultValue, null);
    }

    private static ConfigValue registerConfigValue(String valueName, JsonPrimitive defaultValue, @Nullable String description, ModSide logicalSide) {
        return registerConfigValue(valueName, defaultValue, description, null, null, logicalSide);
    }

    private static ConfigValue registerConfigValue(String valueName, JsonPrimitive defaultValue, @Nullable String description, Double min, Double max, ModSide logicalSide) {
        ConfigValue configValueToRegister = new ConfigValue(valueName, defaultValue, description, min, max);

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
        protected final Double min;
        protected final Double max;

        public ConfigValue(String valueName, JsonPrimitive defaultValue, @Nullable String description) {
            this(valueName, defaultValue, description, null, null);
        }

        public ConfigValue(String valueName, JsonPrimitive defaultValue, @Nullable String description, Double min, Double max) {
            this.valueName = valueName;
            this.defaultValue = defaultValue;
            this.description = description;
            this.min = min;
            this.max = max;
        }

        @Override
        public JsonPrimitive get() {
            return (currentValue = computeValue()) == null
                    ? (currentValue = defaultValue)
                    : currentValue;
        }

        protected JsonPrimitive computeValue() {
            if (CONFIG_JSON.has(valueName)) {
                JsonPrimitive retrievedValue = CONFIG_JSON.get(valueName).getAsJsonObject().get("value").getAsJsonPrimitive();

                if (retrievedValue.isNumber() && min != null && max != null) retrievedValue = new JsonPrimitive(Mth.clamp(retrievedValue.getAsDouble(), min, max));

                return retrievedValue;
            } else return defaultValue;
        }

        public void serializeTo(JsonObject configFileJson, boolean fromUpdatedConfigInMemory) {
            JsonObject valueJson = configFileJson.has(valueName) ? configFileJson.get(valueName).getAsJsonObject() : new JsonObject();

            valueJson.add("value", fromUpdatedConfigInMemory ? Optional.ofNullable(currentValue).orElse(get()) : get());
            valueJson.addProperty("description", description == null ? "No description provided." : description);

            if (getDefaultValue().isNumber() && min != null) valueJson.addProperty("min", min);
            if (getDefaultValue().isNumber() && max != null) valueJson.addProperty("max", max);

            configFileJson.add(valueName, valueJson);
            int a = 0;
        }

        public String getValueName() {
            return valueName;
        }

        public String getFormattedValueName() {
            return StringUtil.toTitleCase(valueName);
        }

        public JsonPrimitive getDefaultValue() {
            return defaultValue;
        }

        @Nullable
        public String getDescription() {
            return description;
        }

        @Nullable
        public Double getMin() {
            return min;
        }

        @Nullable
        public Double getMax() {
            return max;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof ConfigValue other)) return false;

            return Objects.equals(valueName, other.valueName) && Objects.equals(defaultValue, other.defaultValue) && Objects.equals(currentValue, other.currentValue) && Objects.equals(min, other.min) && Objects.equals(max, other.max);
        }
    }
}
