package com.easyfarming.core;

import com.easyfarming.ItemRequirement;
import net.runelite.api.coords.WorldPoint;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Teleport {
    public enum Category {
        ITEM,
        PORTAL_NEXUS,
        SPIRIT_TREE,
        FAIRY_RING,
        JEWELLERY_BOX,
        MOUNTED_XERICS,
        SPELLBOOK,
        /** No teleport - the player travels to the patch manually. */
        NONE
    }
    
    private final String enumOption;
    private final Category category;
    private final String description;
    private final int id;
    private final String rightClickOption;
    private final int interfaceGroupId;
    private final int interfaceChildId;
    private final int regionId;
    private final WorldPoint point;
    private final List<ItemRequirement> itemRequirements;
    
    public Teleport(String enumOption, Category category, String description, int id,
                   String rightClickOption, int interfaceGroupId, int interfaceChildId,
                   int regionId, WorldPoint point, List<ItemRequirement> itemRequirements) {
        this.enumOption = enumOption;
        this.category = category;
        this.description = description;
        this.id = id;
        this.rightClickOption = rightClickOption;
        this.interfaceGroupId = interfaceGroupId;
        this.interfaceChildId = interfaceChildId;
        this.regionId = regionId;
        this.point = point;
        this.itemRequirements = itemRequirements;
    }

    /**
     * Creates the standard "None" teleport option for a location: no teleport items, no highlighting,
     * the player travels to the patch on their own. The patch point and region let the plugin detect
     * arrival the same way as any teleport.
     */
    public static Teleport none(String locationLabel, int regionId, WorldPoint patchPoint) {
        return new Teleport(
            "None",
            Category.NONE,
            "No teleport - travel to " + locationLabel + " on your own.",
            0,
            "",
            0,
            0,
            regionId,
            patchPoint,
            Collections.emptyList()
        );
    }
    
    public Map<Integer, Integer> getItemRequirements() {
        Map<Integer, Integer> requirements = new HashMap<>();
        for (ItemRequirement itemRequirement : itemRequirements) {
            requirements.put(itemRequirement.getItemId(), itemRequirement.getQuantity());
        }
        return requirements;
    }
    
    // Getters
    public String getEnumOption() { return enumOption; }
    public Category getCategory() { return category; }
    public String getDescription() { return description; }
    public int getId() { return id; }
    public String getRightClickOption() { return rightClickOption; }
    public int getInterfaceGroupId() { return interfaceGroupId; }
    public int getInterfaceChildId() { return interfaceChildId; }
    public int getRegionId() { return regionId; }
    public WorldPoint getPoint() { return point; }
    public List<ItemRequirement> getItemRequirementsList() { return itemRequirements; }
}

