package com.easyfarming.overlays.utils;

import com.easyfarming.EasyFarmingPlugin;
import net.runelite.api.Client;

import java.util.regex.Pattern;

/**
 * Utility class for checking patch states (composted, protected, etc.).
 */
public class PatchStateChecker {
    private final EasyFarmingPlugin plugin;
    
    public PatchStateChecker(EasyFarmingPlugin plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Checks if a patch has been composted based on chat messages.
     */
    public boolean patchIsComposted() {
        String regexCompost1 = "You treat the (herb patch|flower patch|tree patch|fruit tree patch) with (compost|supercompost|ultracompost)\\.";
        String regexCompost2 = "This (herb patch|flower patch|tree patch|fruit tree patch) has already been treated with (compost|supercompost|ultracompost)\\.";
        String regexCompost3 = "You treat the patch with (compost|supercompost|ultracompost)\\.";
        String regexCompost4 = "This patch has already been treated with (compost|supercompost|ultracompost)\\.";
        
        String lastMessage = plugin.getLastMessage();
        if (lastMessage == null || lastMessage.isEmpty()) {
            return false;
        }
        
        return Pattern
            .compile(regexCompost1 + "|" + regexCompost2 + "|" + regexCompost3 + "|" + regexCompost4)
            .matcher(lastMessage)
            .matches();
    }
    
    /**
     * Checks if a patch has been protected based on chat messages.
     */
    public boolean patchIsProtected() {
        String standardResponse = "You pay the gardener ([0-9A-Za-z\\ ]+) to protect the patch\\.";
        String faladorEliteResponse = "The gardener protects your tree for you, free of charge, as a token of gratitude for completing the ([A-Za-z\\ ]+)\\.";
        
        return Pattern
            .compile(standardResponse + "|" + faladorEliteResponse)
            .matcher(plugin.getLastMessage())
            .matches();
    }
}

