package com.easyfarming.managers;

import com.easyfarming.models.CustomRun;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.runelite.client.config.ConfigManager;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class CustomRunManager {
    private static final String CONFIG_GROUP = "farminghelper";
    private static final String RUN_KEY = "customRuns";
    
    private final ConfigManager configManager;
    private final Gson gson;
    
    @Inject
    public CustomRunManager(ConfigManager configManager, Gson gson) {
        this.configManager = configManager;
        this.gson = gson;
    }

    public List<CustomRun> getCustomRuns() {
        String json = configManager.getConfiguration(CONFIG_GROUP, RUN_KEY);
        if (json == null || json.isEmpty()) {
            return new ArrayList<>();
        }

        Type listType = new TypeToken<ArrayList<CustomRun>>(){}.getType();
        return gson.fromJson(json, listType);
    }

    public CustomRun getCustomRun(String name) {
        List<CustomRun> runs = getCustomRuns();
        for (CustomRun run : runs) {
            if (run.getName().equals(name)) {
                return run;
            }
        }
        return null;
    }

    public void saveCustomRuns(List<CustomRun> runs) {
        String json = gson.toJson(runs);
        configManager.setConfiguration(CONFIG_GROUP, RUN_KEY, json);
    }

    public void addCustomRun(CustomRun run) {
        List<CustomRun> currentRuns = getCustomRuns();
        currentRuns.add(run);
        saveCustomRuns(currentRuns);
    }
    
    public void deleteCustomRun(String name) {
        List<CustomRun> currentRuns = getCustomRuns();
        currentRuns.removeIf(run -> run.getName().equals(name));
        saveCustomRuns(currentRuns);
    }

    public void renameCustomRun(String oldName, String newName) {
        List<CustomRun> currentRuns = getCustomRuns();
        for (CustomRun run : currentRuns) {
            if (run.getName().equals(oldName)) {
                run.setName(newName);
                break;
            }
        }
        saveCustomRuns(currentRuns);
    }
    
    public void updateCustomRun(CustomRun updatedRun) {
        List<CustomRun> currentRuns = getCustomRuns();
        for (int i = 0; i < currentRuns.size(); i++) {
            if (currentRuns.get(i).getName().equals(updatedRun.getName())) {
                currentRuns.set(i, updatedRun);
                break;
            }
        }
        saveCustomRuns(currentRuns);
    }
}
