package com.easyfarming.overlays.utils;

import com.easyfarming.EasyFarmingPlugin;

import javax.inject.Inject;
import java.util.regex.Pattern;

/**
 * Utility class for checking patch states (composted, protected, etc.).
 */
public class PatchStateChecker {
    private static final String REGEX_COMPOST1 = "You treat the (herb patch|flower patch|allotment patch|allotment|tree patch|fruit tree patch) with (compost|supercompost|ultracompost)\\.";
    private static final String REGEX_COMPOST2 = "This (herb patch|flower patch|allotment patch|allotment|tree patch|fruit tree patch) has already been treated with (compost|supercompost|ultracompost)\\.";
    private static final String REGEX_COMPOST3 = "You treat the patch with (compost|supercompost|ultracompost)\\.";
    private static final String REGEX_COMPOST4 = "This patch has already been treated with (compost|supercompost|ultracompost)\\.";
    private static final Pattern COMPOST_PATTERN = Pattern.compile(REGEX_COMPOST1 + "|" + REGEX_COMPOST2 + "|" + REGEX_COMPOST3 + "|" + REGEX_COMPOST4);
    
    private static final String STANDARD_RESPONSE = "You pay the gardener ([0-9A-Za-z\\ ]+) to protect the patch\\.";
    private static final String FALADOR_ELITE_RESPONSE = "The gardener protects your tree for you, free of charge, as a token of gratitude for completing the ([A-Za-z\\ ]+)\\.";
    private static final Pattern PROTECTED_PATTERN = Pattern.compile(STANDARD_RESPONSE + "|" + FALADOR_ELITE_RESPONSE);
    
    private final EasyFarmingPlugin plugin;
    
    @Inject
    public PatchStateChecker(EasyFarmingPlugin plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Checks if a patch has been composted based on chat messages.
     */
    public boolean patchIsComposted() {
        String lastMessage = plugin.getLastMessage();
        if (lastMessage == null || lastMessage.isEmpty()) {
            return false;
        }
        
        return COMPOST_PATTERN.matcher(lastMessage).matches();
    }
    
    /**
     * Checks if a patch has been protected based on chat messages.
     */
    public boolean patchIsProtected() {
        String lastMessage = plugin.getLastMessage();
        if (lastMessage == null || lastMessage.isEmpty()) {
            return false;
        }
        
        return PROTECTED_PATTERN.matcher(lastMessage).matches();
    }
}

