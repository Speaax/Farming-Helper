package com.farminghelper.speaax;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import net.runelite.api.coords.WorldPoint;

public class Location {
    public enum TeleportCategory {
        ITEM,
        PORTAL_NEXUS,
        SPIRIT_TREE,
        JEWELLERY_BOX,
        MOUNTED_XERICS,
        MOUNTED_DIGSITE,
        SPELLBOOK
    }
    private String name;

    public String getName() {
        return name;
    }
    private Boolean farmLimps;

    public Boolean getFarmLimps() {
        return farmLimps;
    }

    private List<Teleport> teleportOptions;
    private FarmingHelperConfig config;
    private final Function<FarmingHelperConfig, FarmingHelperConfig.OptionEnumTeleport> selectedTeleportFunction;

    public Location(Function<FarmingHelperConfig, FarmingHelperConfig.OptionEnumTeleport> selectedTeleportFunction, FarmingHelperConfig config, String name, Boolean farmLimps) {
        this.config = config;
        this.selectedTeleportFunction = selectedTeleportFunction;
        this.name = name;
        this.farmLimps = farmLimps;
        this.teleportOptions = new ArrayList<>();
    }

    public void addTeleportOption(Teleport teleport) {
        teleportOptions.add(teleport);
    }

    public Teleport getSelectedTeleport() {
        String selectedEnumOption = selectedTeleportFunction.apply(config).name();
        for (Teleport teleport : teleportOptions) {
            if (teleport.getEnumOption().equalsIgnoreCase(selectedEnumOption)) {
                return teleport;
            }
        }
        return teleportOptions.isEmpty() ? null : teleportOptions.get(0);
    }





    public class Teleport {
        private TeleportCategory category;
        private String description;
        private Color color;
        private int id;
        private int interfaceGroupId;
        private int interfaceChildId;

        private int regionId;
        private String enumOption;
        private String rightClickOption;
        private List<ItemRequirement> itemRequirements;
        private WorldPoint point;
        private String overrideLocationName;

        public Teleport(String enumOption, TeleportCategory category, String description, int id, String rightClickOption, int interfaceGroupId, int interfaceChildId, int regionId, WorldPoint point, List<ItemRequirement> itemRequirements) {
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
            this.overrideLocationName = "";
        }

        public Map<Integer, Integer> getItemRequirements() {
            Map<Integer, Integer> requirements = new HashMap<>();
            for (ItemRequirement itemRequirement : itemRequirements) {
                requirements.put(itemRequirement.getItemId(), itemRequirement.getQuantity());
            }
            return requirements;
        }

        public WorldPoint getPoint(){return point;}

        public void addItemRequirement(int itemId, int quantity) {
            ItemRequirement itemRequirement = new ItemRequirement(itemId, quantity);
            this.itemRequirements.add(itemRequirement);
        }
        public void addAllItemRequirements(List<ItemRequirement> itemRequirements) {
            this.itemRequirements.addAll(itemRequirements);
        }

        public void updateTeleportItemId(int newItemId) {
            this.id = newItemId;
        }

        public Location.Teleport overrideLocationName(String overrideLocationName)
        {
            this.overrideLocationName = overrideLocationName;

            return this;
        }

        public String overrideLocationName()
        {
            return this.overrideLocationName;
        }

        public TeleportCategory getCategory() {
            return category;
        }
        public int getRegionId() {
            return regionId;
        }
        public String getEnumOption() {
            return enumOption;
        }

        public String getDescription() {
            return description;
        }
        public String getRightClickOption() {
            return rightClickOption;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getInterfaceGroupId() {
            return interfaceGroupId;
        }

        public void setInterfaceGroupId(int interfaceGroupId) {
            this.interfaceGroupId = interfaceGroupId;
        }

        public int getInterfaceChildId() {
            return interfaceChildId;
        }

        public void setInterfaceChildId(int interfaceChildId) {
            this.interfaceChildId = interfaceChildId;
        }

        public Color getColor() {
            return  color;
        }
    }
}