package com.easyfarming.overlays.utils;

import com.easyfarming.EasyFarmingPlugin;

import javax.inject.Inject;
import java.util.regex.Pattern;

/**
 * Utility class for checking patch states (composted, protected, etc.).
 */
public class PatchStateChecker {
    private static final String REGEX_COMPOST1 = "You treat the (herb patch|flower patch|allotment|tree patch|fruit tree patch|hops patch) with (compost|supercompost|ultracompost)\\.";
    private static final String REGEX_COMPOST2 = "This (herb patch|flower patch|allotment|tree patch|fruit tree patch|hops patch) has already been treated with (compost|supercompost|ultracompost)\\.";
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
     * True when the last compost-related chat line refers to the regular tree patch (not the fruit tree patch).
     * Used so composting the tree at a location with both patches does not satisfy the fruit tree step.
     */
    public boolean patchIsCompostedForTreePatch() {
        String lastMessage = plugin.getLastMessage();
        if (lastMessage == null || lastMessage.isEmpty()) {
            return false;
        }
        if (!COMPOST_PATTERN.matcher(lastMessage).matches()) {
            return false;
        }
        if (lastMessage.contains("fruit tree patch")) {
            return false;
        }
        if (lastMessage.contains("tree patch")) {
            return true;
        }
        if (lastMessage.contains("herb patch") || lastMessage.contains("flower patch")
                || lastMessage.contains("allotment") || lastMessage.contains("hops patch")) {
            return false;
        }
        return isGenericPatchCompostMessage(lastMessage);
    }

    /**
     * True when the last compost-related chat line refers to the fruit tree patch (or an untyped "the patch" line).
     */
    public boolean patchIsCompostedForFruitTreePatch() {
        String lastMessage = plugin.getLastMessage();
        if (lastMessage == null || lastMessage.isEmpty()) {
            return false;
        }
        if (!COMPOST_PATTERN.matcher(lastMessage).matches()) {
            return false;
        }
        if (lastMessage.contains("fruit tree patch")) {
            return true;
        }
        if (lastMessage.contains("tree patch")) {
            return false;
        }
        if (lastMessage.contains("herb patch") || lastMessage.contains("flower patch")
                || lastMessage.contains("allotment") || lastMessage.contains("hops patch")) {
            return false;
        }
        return isGenericPatchCompostMessage(lastMessage);
    }

    private static boolean isGenericPatchCompostMessage(String lastMessage) {
        return lastMessage.contains("treat the patch with") || lastMessage.contains("This patch has already");
    }

    /**
     * Herb step: accept only herb-specific or untyped "the patch" compost lines (not tree/fruit/hops/etc.).
     */
    public boolean patchIsCompostedForHerbPatch() {
        String lastMessage = plugin.getLastMessage();
        if (lastMessage == null || lastMessage.isEmpty()) {
            return false;
        }
        if (!COMPOST_PATTERN.matcher(lastMessage).matches()) {
            return false;
        }
        if (lastMessage.contains("herb patch")) {
            return true;
        }
        if (lastMessage.contains("flower patch") || lastMessage.contains("fruit tree patch")
                || lastMessage.contains("hops patch") || lastMessage.contains("allotment")) {
            return false;
        }
        if (lastMessage.contains("tree patch")) {
            return false;
        }
        return isGenericPatchCompostMessage(lastMessage);
    }

    /**
     * Flower (limpwurt) step: accept only flower-specific or untyped "the patch" lines.
     */
    public boolean patchIsCompostedForFlowerPatch() {
        String lastMessage = plugin.getLastMessage();
        if (lastMessage == null || lastMessage.isEmpty()) {
            return false;
        }
        if (!COMPOST_PATTERN.matcher(lastMessage).matches()) {
            return false;
        }
        if (lastMessage.contains("flower patch")) {
            return true;
        }
        if (lastMessage.contains("herb patch") || lastMessage.contains("fruit tree patch")
                || lastMessage.contains("hops patch") || lastMessage.contains("allotment")) {
            return false;
        }
        if (lastMessage.contains("tree patch")) {
            return false;
        }
        return isGenericPatchCompostMessage(lastMessage);
    }

    /**
     * Hops step: accept only hops-specific or untyped "the patch" lines.
     */
    public boolean patchIsCompostedForHopsPatch() {
        String lastMessage = plugin.getLastMessage();
        if (lastMessage == null || lastMessage.isEmpty()) {
            return false;
        }
        if (!COMPOST_PATTERN.matcher(lastMessage).matches()) {
            return false;
        }
        if (lastMessage.contains("hops patch")) {
            return true;
        }
        if (lastMessage.contains("herb patch") || lastMessage.contains("flower patch")
                || lastMessage.contains("fruit tree patch") || lastMessage.contains("allotment")) {
            return false;
        }
        if (lastMessage.contains("tree patch")) {
            return false;
        }
        return isGenericPatchCompostMessage(lastMessage);
    }

    /**
     * Allotment north/south: accept allotment lines or generic "the patch". North→south transitions clear the
     * last message so one allotment compost line cannot satisfy the other half.
     */
    public boolean patchIsCompostedForAllotmentPatch() {
        String lastMessage = plugin.getLastMessage();
        if (lastMessage == null || lastMessage.isEmpty()) {
            return false;
        }
        if (!COMPOST_PATTERN.matcher(lastMessage).matches()) {
            return false;
        }
        if (lastMessage.contains("allotment")) {
            return true;
        }
        if (lastMessage.contains("herb patch") || lastMessage.contains("flower patch")
                || lastMessage.contains("fruit tree patch") || lastMessage.contains("hops patch")) {
            return false;
        }
        if (lastMessage.contains("tree patch")) {
            return false;
        }
        return isGenericPatchCompostMessage(lastMessage);
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

