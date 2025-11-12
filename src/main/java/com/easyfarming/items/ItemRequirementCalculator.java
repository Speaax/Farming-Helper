package com.easyfarming.items;

import com.easyfarming.EasyFarmingConfig;
import com.easyfarming.EasyFarmingPlugin;
import com.easyfarming.core.ItemRequirement;
import com.easyfarming.core.Location;
import com.easyfarming.core.Teleport;
import net.runelite.api.gameval.ItemID;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * NOTE: This class is part of an incomplete refactoring effort.
 * 
 * This class was designed to replace the current ItemsAndLocations.* package structure
 * and work with the new locations.* package classes, but the migration was never completed.
 * 
 * Currently unused - no instantiation found in the codebase.
 * 
 * SPARED FROM PURGING: This appears to be part of an unimplemented feature/refactoring
 * and may be completed in the future.
 */
public class ItemRequirementCalculator {
    
    private final EasyFarmingConfig config;
    private final EasyFarmingPlugin plugin;
    private final ItemAndLocationHelper itemHelper;
    
    public ItemRequirementCalculator(EasyFarmingConfig config, EasyFarmingPlugin plugin, ItemAndLocationHelper itemHelper) {
        this.config = config;
        this.plugin = plugin;
        this.itemHelper = itemHelper;
    }
    
    public Map<Integer, Integer> calculateItemRequirements(RunType runType, List<Location> locations) {
        Map<Integer, Integer> allRequirements = new HashMap<>();
        
        // Get run-specific configuration
        RunTypeConfig runConfig = getRunTypeConfig(runType);
        
        // Process each location
        for (Location location : locations) {
            if (!isLocationEnabled(runType, location.getName())) {
                continue;
            }
            
            // Add seed/sapling for this location
            allRequirements.merge(runConfig.getSeedId(), 1, Integer::sum);
            
            // Add coins for tree types
            if (runConfig.requiresCoins()) {
                allRequirements.merge(ItemID.COINS, 200, Integer::sum);
            }
            
            // Add compost if needed (not bottomless)
            Integer compostId = itemHelper.selectedCompostID();
            if (compostId != null && compostId != -1 && compostId != ItemID.BOTTOMLESS_COMPOST_BUCKET) {
                allRequirements.merge(compostId, 1, Integer::sum);
            }
            
            // Add teleport requirements
            Teleport teleport = location.getSelectedTeleport();
            Map<Integer, Integer> locationRequirements = teleport.getItemRequirements();
            
            for (Map.Entry<Integer, Integer> entry : locationRequirements.entrySet()) {
                int itemId = entry.getKey();
                int quantity = entry.getValue();
                
                // Handle special items that should be counted as 1 max
                if (isSpecialItem(itemId, runType)) {
                    allRequirements.merge(itemId, quantity, (oldValue, newValue) -> Math.min(1, oldValue + newValue));
                } else {
                    allRequirements.merge(itemId, quantity, Integer::sum);
                }
            }
            
            // Handle limpwurt seeds for herb runs
            if (runType == RunType.HERB && location.getFarmLimps() && config.generalLimpwurt()) {
                allRequirements.merge(ItemID.LIMPWURT_SEED, 1, Integer::sum);
                
                // Add compost for limpwurt patch if needed
                if (compostId != null && compostId != -1 && compostId != ItemID.BOTTOMLESS_COMPOST_BUCKET) {
                    allRequirements.merge(compostId, 1, Integer::sum);
                }
            }
        }
        
        // Add common tools
        if (runType == RunType.HERB && config.generalSeedDibber()) {
            allRequirements.merge(ItemID.DIBBER, 1, Integer::sum);
        }
        
        allRequirements.merge(ItemID.SPADE, 1, Integer::sum);
        
        // Add bottomless compost bucket if selected
        Integer compostId = itemHelper.selectedCompostID();
        if (compostId != null && compostId == ItemID.BOTTOMLESS_COMPOST_BUCKET) {
            allRequirements.merge(ItemID.BOTTOMLESS_COMPOST_BUCKET, 1, Integer::sum);
        }
        
        allRequirements.merge(ItemID.FAIRY_ENCHANTED_SECATEURS, 1, Integer::sum);
        
        if (config.generalRake()) {
            allRequirements.merge(ItemID.RAKE, 1, Integer::sum);
        }
        
        return allRequirements;
    }
    
    private boolean isLocationEnabled(RunType runType, String locationName) {
        switch (runType) {
            case HERB:
                return plugin.getHerbLocationEnabled(locationName);
            case TREE:
                return plugin.getTreeLocationEnabled(locationName);
            case FRUIT_TREE:
                return plugin.getFruitTreeLocationEnabled(locationName);
            default:
                return false;
        }
    }
    
    private boolean isSpecialItem(int itemId, RunType runType) {
        // Items that should be counted as max 1 regardless of how many locations need them
        if (itemId == ItemID.SKILLCAPE_CONSTRUCTION || 
            itemId == ItemID.SKILLCAPE_CONSTRUCTION_TRIMMED || 
            itemId == ItemID.SKILLCAPE_MAX) {
            return true;
        }
        
        if (itemId == ItemID.HG_QUETZALWHISTLE_BASIC || 
            itemId == ItemID.HG_QUETZALWHISTLE_ENHANCED || 
            itemId == ItemID.HG_QUETZALWHISTLE_PERFECTED) {
            return true;
        }
        
        if (itemId == ItemID.SKILLCAPE_HUNTING || 
            itemId == ItemID.SKILLCAPE_HUNTING_TRIMMED) {
            return true;
        }
        
        if (runType == RunType.FRUIT_TREE && itemId == ItemID.MM2_ROYAL_SEED_POD) {
            return true;
        }
        
        return false;
    }
    
    private RunTypeConfig getRunTypeConfig(RunType runType) {
        switch (runType) {
            case HERB:
                return new RunTypeConfig(ItemID.GUAM_SEED, false);
            case TREE:
                return new RunTypeConfig(ItemID.PLANTPOT_OAK_SAPLING, true);
            case FRUIT_TREE:
                return new RunTypeConfig(ItemID.PLANTPOT_APPLE_SAPLING, true);
            default:
                throw new IllegalArgumentException("Unknown run type: " + runType);
        }
    }
    
    private static class RunTypeConfig {
        private final int seedId;
        private final boolean requiresCoins;
        
        public RunTypeConfig(int seedId, boolean requiresCoins) {
            this.seedId = seedId;
            this.requiresCoins = requiresCoins;
        }
        
        public int getSeedId() { return seedId; }
        public boolean requiresCoins() { return requiresCoins; }
    }
    
    // Helper interface to abstract ItemAndLocation methods
    public interface ItemAndLocationHelper {
        Integer selectedCompostID();
        List<ItemRequirement> getHouseTeleportItemRequirements();
    }
}

