package com.easyfarming.customrun;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.runelite.client.config.ConfigManager;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Persists custom runs to Runelite config (JSON under a single key).
 */
public class CustomRunStorage {
    private static final String CONFIG_GROUP = "farminghelper";
    private static final String KEY_CUSTOM_RUNS = "customRuns";
    private static final Type LIST_TYPE = new TypeToken<ArrayList<CustomRun>>() {}.getType();

    private final ConfigManager configManager;
    private final Gson gson;

    public CustomRunStorage(ConfigManager configManager, Gson gson) {
        this.configManager = configManager;
        this.gson = gson;
    }

    public List<CustomRun> load() {
        String json = configManager.getConfiguration(CONFIG_GROUP, KEY_CUSTOM_RUNS);
        if (json == null || json.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            List<CustomRun> list = gson.fromJson(json, LIST_TYPE);
            return list != null ? list : new ArrayList<>();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public void save(List<CustomRun> runs) {
        String json = gson.toJson(runs != null ? runs : new ArrayList<CustomRun>());
        configManager.setConfiguration(CONFIG_GROUP, KEY_CUSTOM_RUNS, json);
    }
}
