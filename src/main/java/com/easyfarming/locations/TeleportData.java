package com.easyfarming.locations;

import com.easyfarming.core.ItemRequirement;
import com.easyfarming.core.Teleport;
import net.runelite.api.coords.WorldPoint;
import java.util.List;
import java.util.function.Supplier;

/**
 * NOTE: This class is part of an incomplete refactoring effort.
 * 
 * This class and related classes in the locations.* package were designed to replace
 * the current ItemsAndLocations.* package structure, but the migration was never completed.
 * 
 * Currently only used by LocationData/LocationFactory (which are also unused).
 * 
 * SPARED FROM PURGING: This appears to be part of an unimplemented feature/refactoring
 * and may be completed in the future.
 */
public class TeleportData {
    private final String enumOption;
    private final Teleport.Category category;
    private final String description;
    private final int id;
    private final String rightClickOption;
    private final int interfaceGroupId;
    private final int interfaceChildId;
    private final int regionId;
    private final WorldPoint point;
    private final Supplier<List<ItemRequirement>> itemRequirementsSupplier;
    
    public TeleportData(String enumOption, Teleport.Category category, String description, int id,
                       String rightClickOption, int interfaceGroupId, int interfaceChildId,
                       int regionId, WorldPoint point, Supplier<List<ItemRequirement>> itemRequirementsSupplier) {
        this.enumOption = enumOption;
        this.category = category;
        this.description = description;
        this.id = id;
        this.rightClickOption = rightClickOption;
        this.interfaceGroupId = interfaceGroupId;
        this.interfaceChildId = interfaceChildId;
        this.regionId = regionId;
        this.point = point;
        this.itemRequirementsSupplier = itemRequirementsSupplier;
    }
    
    public Teleport toTeleport() {
        return new Teleport(
            enumOption, category, description, id, rightClickOption,
            interfaceGroupId, interfaceChildId, regionId, point,
            itemRequirementsSupplier.get()
        );
    }
    
    // Getters
    public String getEnumOption() { return enumOption; }
    public Teleport.Category getCategory() { return category; }
    public String getDescription() { return description; }
    public int getId() { return id; }
    public String getRightClickOption() { return rightClickOption; }
    public int getInterfaceGroupId() { return interfaceGroupId; }
    public int getInterfaceChildId() { return interfaceChildId; }
    public int getRegionId() { return regionId; }
    public WorldPoint getPoint() { return point; }
    public Supplier<List<ItemRequirement>> getItemRequirementsSupplier() { return itemRequirementsSupplier; }
}

