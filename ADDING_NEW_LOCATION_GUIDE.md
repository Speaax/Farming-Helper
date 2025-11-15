# Step-by-Step Guide: Adding a New Location

This guide explains how to add a new farming location, its item requirements, and teleportation options after the refactoring. The refactoring simplified this process by using a data-driven approach with `LocationData` classes.

## Overview

The refactored architecture uses:
- **LocationData**: Data class that defines location properties and teleport options
- **TeleportData**: Data class that defines individual teleport methods with their requirements
- **LocationFactory**: Factory that converts LocationData into Location instances
- **ItemRequirement**: Simple class for item requirements (itemId, quantity)

## Step-by-Step Process

### Step 1: Determine Location Type

First, determine which type of location you're adding:
- **Herb patch**: Goes in `src/main/java/com/easyfarming/locations/`
- **Tree patch**: Goes in `src/main/java/com/easyfarming/locations/tree/`
- **Fruit tree patch**: Goes in `src/main/java/com/easyfarming/locations/fruittree/`

### Step 2: Create the LocationData Class

Create a new class following the naming pattern: `[LocationName]LocationData.java` (or `[LocationName]TreeLocationData.java` for trees, `[LocationName]FruitTreeLocationData.java` for fruit trees).

**Template structure:**

```java
package com.easyfarming.locations; // or .tree or .fruittree

import com.easyfarming.EasyFarmingConfig;
import com.easyfarming.ItemRequirement;
import com.easyfarming.core.Teleport;
import com.easyfarming.locations.LocationData;
import com.easyfarming.locations.TeleportData;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * LocationData definition for [Location Name].
 */
public class [LocationName]LocationData {
    
    private static final WorldPoint [LOCATION]_PATCH_POINT = new WorldPoint(x, y, 0);
    
    /**
     * Creates LocationData for [Location Name].
     * @param houseTeleportSupplier Supplier that provides house teleport item requirements
     *                              (only needed if location has Portal Nexus teleports)
     */
    public static LocationData create(Supplier<List<ItemRequirement>> houseTeleportSupplier) {
        // Or: public static LocationData create() { if no Portal Nexus teleports
        
        LocationData locationData = new LocationData(
            "[Location Name]",           // Display name
            true,                        // farmLimps (true if location has limpwurt patch)
            [LOCATION]_PATCH_POINT,      // WorldPoint of the patch
            EasyFarmingConfig::enumOptionEnum[LocationName]Teleport  // Config function reference
        );
        
        // Add teleport options here (see Step 3)
        
        return locationData;
    }
}
```

**Key parameters:**
- **Name**: Display name of the location (must match config enum name pattern)
- **farmLimps**: `true` if location has a limpwurt patch, `false` otherwise
- **patchPoint**: `WorldPoint` coordinates of the farming patch
- **configFunction**: Method reference to the config enum getter (e.g., `EasyFarmingConfig::enumOptionEnumArdougneTeleport`)

### Step 3: Add Teleport Options

For each teleport method available to reach the location, add a `TeleportData` entry using `locationData.addTeleport()`.

**TeleportData Constructor Parameters:**
1. **enumOption** (String): Must match the enum value name in the config (see Step 5). Use underscores instead of spaces (e.g., `"Portal_Nexus"`, `"Camelot_Teleport"`)
2. **category** (Teleport.Category): One of:
   - `Teleport.Category.ITEM` - Item-based teleport (tele tabs, items)
   - `Teleport.Category.PORTAL_NEXUS` - Portal Nexus in player-owned house
   - `Teleport.Category.SPELLBOOK` - Spellbook teleport
   - `Teleport.Category.SPIRIT_TREE` - Spirit tree
   - `Teleport.Category.JEWELLERY_BOX` - Jewellery box in POH
   - `Teleport.Category.MOUNTED_XERICS` - Mounted Xeric's talisman
3. **description** (String): User-facing description of the teleport method
4. **id** (int): Item ID if category is ITEM, otherwise 0
5. **rightClickOption** (String): Right-click option name if needed (e.g., `"Farm Teleport"` for Ardy cloak), otherwise `""` or `"null"` (both are acceptable)
6. **interfaceGroupId** (int): Interface group ID for Portal Nexus/Jewellery Box/Spellbook:
   - `17` for Portal Nexus
   - `218` for Spellbook
   - `187` for Spirit Tree
   - `0` for ITEM category teleports
7. **interfaceChildId** (int): Interface child ID:
   - `13` for Portal Nexus
   - Varies for spellbook (e.g., `34` for Camelot, `41` for Ardougne)
   - `3` for Spirit Tree
   - `0` for ITEM category teleports
8. **regionId** (int): Region ID where the teleport lands (can be found using RuneLite dev tools)
9. **point** (WorldPoint): WorldPoint where the teleport lands (usually the same as the patch point)
10. **itemRequirementsSupplier** (Supplier<List<ItemRequirement>>): Supplier that returns the list of required items:
    - For Portal Nexus: use `houseTeleportSupplier` parameter
    - For other teleports: use lambda `() -> Arrays.asList(...)` or `() -> Collections.singletonList(...)`
    - For teleports with no requirements: use `() -> Collections.emptyList()`

**Example teleport options:**

```java
// Portal Nexus teleport (requires houseTeleportSupplier parameter)
locationData.addTeleport(new TeleportData(
    "Portal_Nexus",
    Teleport.Category.PORTAL_NEXUS,
    "Teleport to [Location] with Portal Nexus.",
    0,
    "",
    17,  // Interface group ID for Portal Nexus
    13,  // Interface child ID for Portal Nexus
    12345,  // Region ID
    [LOCATION]_PATCH_POINT,
    houseTeleportSupplier  // Uses the supplier passed to create()
));

// Spellbook teleport
locationData.addTeleport(new TeleportData(
    "Teleport",  // or "Camelot_Teleport", "Ardougne_teleport", etc.
    Teleport.Category.SPELLBOOK,
    "Teleport to [Location] using the standard spellbook.",
    0,
    "",
    218,  // Interface group ID for spellbook
    34,   // Interface child ID (varies by spell)
    12345,  // Region ID
    [LOCATION]_PATCH_POINT,
    () -> Arrays.asList(
        new ItemRequirement(ItemID.AIRRUNE, 5),
        new ItemRequirement(ItemID.LAWRUNE, 1)
    )
));

// Item-based teleport (tele tab)
locationData.addTeleport(new TeleportData(
    "[Location]_Tele_Tab",
    Teleport.Category.ITEM,
    "Teleport to [Location] using [Location] tele tab.",
    ItemID.POH_TABLET_[LOCATION]TELEPORT,  // Item ID
    "",
    0,
    0,
    12345,  // Region ID
    [LOCATION]_PATCH_POINT,
    () -> Collections.singletonList(
        new ItemRequirement(ItemID.POH_TABLET_[LOCATION]TELEPORT, 1)
    )
));

// Item with right-click option (e.g., Ardy cloak)
locationData.addTeleport(new TeleportData(
    "Ardy_cloak",
    Teleport.Category.ITEM,
    "Teleport to [Location] Farm with Ardougne cloak.",
    ItemID.ARDY_CAPE_MEDIUM,
    "Farm Teleport",  // Right-click option name
    0,
    0,
    12345,  // Region ID
    [LOCATION]_PATCH_POINT,
    () -> Collections.singletonList(
        new ItemRequirement(ItemID.ARDY_CAPE_MEDIUM, 1)
    )
));

// Spirit Tree teleport (no item requirements)
locationData.addTeleport(new TeleportData(
    "Spirit_Tree",
    Teleport.Category.SPIRIT_TREE,
    "Teleport to [Location] via a Spirit Tree.",
    0,
    "",
    187,  // Interface group ID for Spirit Tree
    3,    // Interface child ID for Spirit Tree
    12345,  // Region ID
    [LOCATION]_PATCH_POINT,
    () -> Collections.emptyList()  // No item requirements
));
```

**Important notes:**
- For Portal Nexus teleports, use the `houseTeleportSupplier` parameter
- For other teleports, use lambda expressions `() -> Arrays.asList(...)` or `() -> Collections.singletonList(...)`
- The `enumOption` string must exactly match the enum value name in the config (see Step 5)
- Use `ItemID` constants from `net.runelite.api.gameval.ItemID` for item IDs

### Step 4: Register the Location

Add a setup method in the appropriate `ItemAndLocation` class:

**For Herb locations:** `src/main/java/com/easyfarming/ItemsAndLocations/HerbRunItemAndLocation.java`

**For Tree locations:** `src/main/java/com/easyfarming/ItemsAndLocations/TreeRunItemAndLocation.java`

**For Fruit Tree locations:** `src/main/java/com/easyfarming/ItemsAndLocations/FruitTreeRunItemAndLocation.java`

**Pattern:**

```java
private void setup[LocationName]Location()
{
    // NEW APPROACH: Using LocationData pattern for data-driven setup
    com.easyfarming.locations.LocationData [locationName]Data = 
        com.easyfarming.locations.[LocationName]LocationData.create(createHouseTeleportSupplier());
    // Or: com.easyfarming.locations.[LocationName]LocationData.create(); if no Portal Nexus
    
    [locationName]Location = com.easyfarming.locations.LocationFactory.createLocation([locationName]Data, config);
    locations.add([locationName]Location);
}
```

**Note:** 
- If the location has **any** Portal Nexus teleport options, you **must** pass `createHouseTeleportSupplier()` to the `create()` method
- If the location has **no** Portal Nexus teleports, you can call `create()` with no parameters (see `MorytaniaLocationData` and `GnomeStrongholdTreeLocationData` as examples)

Then call this method in the `setupLocations()` method:

```java
public void setupLocations()
{
    // ... existing code ...
    setup[LocationName]Location();
    // ... existing code ...
}
```

**Note:** If the location doesn't have Portal Nexus teleports, you can omit the `houseTeleportSupplier` parameter (see `MorytaniaLocationData.create()` as an example).

### Step 5: Add Config Enum (if needed)

If this is a completely new location, you need to add a config enum in `EasyFarmingConfig.java`.

**Pattern:**

```java
@ConfigItem(
    position = X,  // Next available position
    keyName = "enumOptionEnum[LocationName]Teleport",
    name = "[Location Name] Teleport",
    description = "Select teleport method for [Location Name]"
)
default OptionEnum[LocationName]Teleport enumOptionEnum[LocationName]Teleport() { 
    return OptionEnum[LocationName]Teleport.[Default_Option]; 
}

enum OptionEnum[LocationName]Teleport implements OptionEnumTeleport
{
    [Enum_Value_1],  // Must match enumOption in TeleportData
    [Enum_Value_2],
    [Enum_Value_3]
    // ... more options
}
```

**Important:**
- Enum value names must **exactly match** the `enumOption` strings used in `TeleportData` (case-sensitive)
- Enum values use underscores (e.g., `Portal_Nexus`, `Camelot_Teleport`, `Ardy_cloak`)
- The default return value should be one of the enum values
- The enum must implement `OptionEnumTeleport`
- For tree locations, the enum name pattern is `OptionEnumTree[LocationName]Teleport` (e.g., `OptionEnumTreeGnomeStrongoldTeleport`)
- For fruit tree locations, the enum name pattern is `OptionEnumFruitTree[LocationName]Teleport` (e.g., `OptionEnumFruitTreeCatherbyTeleport`)

### Step 6: Add Location Getter (if needed)

If the location needs to be accessed from other parts of the codebase, add a getter method in `EasyFarmingPlugin.java`:

```java
public Location get[LocationName]Location() {
    return [itemAndLocationClass].[locationName]Location;
}
```

### Step 7: Update Location Enablement Logic (if needed)

If the location needs to be conditionally enabled/disabled, update the appropriate method in `EasyFarmingPlugin.java`:

**For herb locations:**
```java
public boolean getHerbLocationEnabled(String locationName) {
    switch (locationName) {
        // ... existing cases ...
        case "[Location Name]":
            return config.[locationName]();
        // ...
    }
}
```

And add the corresponding config item in `EasyFarmingConfig.java`.

## Complete Example: Adding a New Herb Location

Let's say we want to add "Port Phasmatys" as a new herb location:

### 1. Create `PortPhasmatysLocationData.java`

```java
package com.easyfarming.locations;

import com.easyfarming.EasyFarmingConfig;
import com.easyfarming.ItemRequirement;
import com.easyfarming.core.Teleport;
import com.easyfarming.locations.LocationData;
import com.easyfarming.locations.TeleportData;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class PortPhasmatysLocationData {
    
    private static final WorldPoint PORT_PHASMATYS_HERB_PATCH_POINT = new WorldPoint(3608, 3530, 0);
    
    public static LocationData create() {
        LocationData locationData = new LocationData(
            "Port Phasmatys",
            true, // farmLimps
            PORT_PHASMATYS_HERB_PATCH_POINT,
            EasyFarmingConfig::enumOptionEnumPortPhasmatysTeleport
        );
        
        // Ectophial teleport
        locationData.addTeleport(new TeleportData(
            "Ectophial",
            Teleport.Category.ITEM,
            "Teleport to Port Phasmatys with Ectophial.",
            ItemID.ECTOPHIAL,
            "",
            0,
            0,
            14647,
            PORT_PHASMATYS_HERB_PATCH_POINT,
            () -> Collections.singletonList(
                new ItemRequirement(ItemID.ECTOPHIAL, 1)
            )
        ));
        
        return locationData;
    }
}
```

### 2. Register in `HerbRunItemAndLocation.java`

```java
private void setupPortPhasmatysLocation()
{
    com.easyfarming.locations.LocationData portPhasmatysData = 
        com.easyfarming.locations.PortPhasmatysLocationData.create();
    
    portPhasmatysLocation = com.easyfarming.locations.LocationFactory.createLocation(portPhasmatysData, config);
    locations.add(portPhasmatysLocation);
}
```

Add to `setupLocations()`:
```java
setupPortPhasmatysLocation();
```

### 3. Add Config Enum in `EasyFarmingConfig.java`

```java
@ConfigItem(
    position = 10,
    keyName = "enumOptionEnumPortPhasmatysTeleport",
    name = "Port Phasmatys Teleport",
    description = "Select teleport method for Port Phasmatys"
)
default OptionEnumPortPhasmatysTeleport enumOptionEnumPortPhasmatysTeleport() { 
    return OptionEnumPortPhasmatysTeleport.Ectophial; 
}

enum OptionEnumPortPhasmatysTeleport implements OptionEnumTeleport
{
    Ectophial
}
```

## Summary

The refactored process simplifies adding locations by:
1. **Data-driven approach**: Locations are defined as data structures rather than complex code
2. **Consistent pattern**: All locations follow the same structure
3. **Separation of concerns**: Location data, teleport data, and item requirements are clearly separated
4. **Easy to extend**: Adding new teleport options is just adding another `addTeleport()` call

The key files involved:
- `LocationData.java` - Base data class
- `TeleportData.java` - Teleport option data class
- `LocationFactory.java` - Converts data to Location instances
- `ItemRequirement.java` - Simple item requirement class
- `[LocationName]LocationData.java` - Your new location definition
- `[RunType]ItemAndLocation.java` - Where locations are registered
- `EasyFarmingConfig.java` - Configuration enums

