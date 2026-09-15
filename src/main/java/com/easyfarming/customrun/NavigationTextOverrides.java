package com.easyfarming.customrun;

import com.easyfarming.core.Teleport;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.runelite.client.config.ConfigManager;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Persists instruction text overrides for navigation and farming steps.
 * <ul>
 *   <li>Nav keys: {@code locationName|teleportEnumOption} so shared teleport methods
 *       (e.g. Camelot) can differ at Seers Village vs Catherby.</li>
 *   <li>Farming/step keys: {@code step|defaultText} so e.g. "Harvest Herbs." can be customized.</li>
 * </ul>
 */
public class NavigationTextOverrides {
    private static final String CONFIG_GROUP = "farminghelper";
    private static final String KEY_NAV_TEXT_OVERRIDES = "navTextOverrides";
    private static final String STEP_PREFIX = "step|";
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

    /** Package-visible for unit tests. */
    static String stepKey(String defaultText) {
        return STEP_PREFIX + (defaultText != null ? defaultText : "");
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

    public String getStepOverride(String defaultText) {
        return loadAll().get(stepKey(defaultText));
    }

    public void setStepOverride(String defaultText, String text) {
        Map<String, String> map = loadAll();
        String k = stepKey(defaultText);
        if (text == null || text.trim().isEmpty()) {
            map.remove(k);
        } else {
            map.put(k, text);
        }
        saveAll(map);
    }

    public void clearStepOverride(String defaultText) {
        Map<String, String> map = loadAll();
        map.remove(stepKey(defaultText));
        saveAll(map);
    }

    /**
     * Returns the step override if present, otherwise {@code defaultText} (or empty if null).
     */
    public String resolveStep(String defaultText) {
        String override = getStepOverride(defaultText);
        if (override != null && !override.isEmpty()) {
            return override;
        }
        return defaultText != null ? defaultText : "";
    }
}
