package com.easyfarming.customrun;

import com.easyfarming.core.Teleport;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.runelite.client.config.ConfigManager;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Persists per-location, per-teleport navigation instruction text overrides.
 * Keys are {@code locationName|teleportEnumOption} so shared teleport methods
 * (e.g. Camelot) can have different text at Seers Village vs Catherby.
 */
public class NavigationTextOverrides {
    private static final String CONFIG_GROUP = "farminghelper";
    private static final String KEY_NAV_TEXT_OVERRIDES = "navTextOverrides";
    private static final Type MAP_TYPE = new TypeToken<HashMap<String, String>>() {}.getType();

    private final ConfigManager configManager;
    private final Gson gson;

    public NavigationTextOverrides(ConfigManager configManager, Gson gson) {
        this.configManager = configManager;
        this.gson = gson;
    }

    /** Package-visible for unit tests. */
    static String key(String locationName, String teleportEnumOption) {
        if (locationName == null) {
            locationName = "";
        }
        if (teleportEnumOption == null) {
            teleportEnumOption = "";
        }
        return locationName + "|" + teleportEnumOption;
    }

    public Map<String, String> loadAll() {
        String json = configManager.getConfiguration(CONFIG_GROUP, KEY_NAV_TEXT_OVERRIDES);
        if (json == null || json.isEmpty()) {
            return new HashMap<>();
        }
        try {
            Map<String, String> map = gson.fromJson(json, MAP_TYPE);
            return map != null ? map : new HashMap<>();
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    private void saveAll(Map<String, String> map) {
        configManager.setConfiguration(CONFIG_GROUP, KEY_NAV_TEXT_OVERRIDES, gson.toJson(map != null ? map : new HashMap<>()));
    }

    public String getOverride(String locationName, String teleportEnumOption) {
        return loadAll().get(key(locationName, teleportEnumOption));
    }

    public void setOverride(String locationName, String teleportEnumOption, String text) {
        Map<String, String> map = loadAll();
        String k = key(locationName, teleportEnumOption);
        if (text == null || text.trim().isEmpty()) {
            map.remove(k);
        } else {
            map.put(k, text);
        }
        saveAll(map);
    }

    public void clearOverride(String locationName, String teleportEnumOption) {
        Map<String, String> map = loadAll();
        map.remove(key(locationName, teleportEnumOption));
        saveAll(map);
    }

    /**
     * Returns the override if present, otherwise the teleport's built-in description.
     */
    public String resolve(String locationName, Teleport teleport) {
        if (teleport == null) {
            return "";
        }
        String override = getOverride(locationName, teleport.getEnumOption());
        if (override != null && !override.isEmpty()) {
            return override;
        }
        return teleport.getDescription() != null ? teleport.getDescription() : "";
    }
}
