package com.farminghelper.speaax.Patch;

import com.farminghelper.speaax.FarmingHelperConfig;
import net.runelite.api.ItemID;

import javax.inject.Inject;
import java.util.*;

public class Teleport
{
    public enum Category
    {
        ITEM,
        PORTAL_NEXUS,
        SPIRIT_TREE,
        JEWELLERY_BOX,
        MOUNTED_XERICS,
        SPELLBOOK
    }

    private Category category;

    private String description;

    private int id;

    private int interfaceGroupId;

    private int interfaceChildId;

    private int regionId;

    private String enumOption;

    private String rightClickOption;

    private List<ItemRequirement> itemRequirements;

    private String overrideLocationName;

    private boolean houseTeleport;

    public Teleport(
        String enumOption,
        Category category,
        String description,
        int id,
        String rightClickOption,
        int interfaceGroupId,
        int interfaceChildId,
        int regionId,
        List<ItemRequirement> itemRequirements
    ) {
        this.enumOption = enumOption;
        this.category = category;
        this.description = description;
        this.id = id;
        this.rightClickOption = rightClickOption;
        this.interfaceGroupId = interfaceGroupId;
        this.interfaceChildId = interfaceChildId;
        this.regionId = regionId;
        this.itemRequirements = itemRequirements;
        this.houseTeleport = false;
        this.overrideLocationName = "";
    }

    public Teleport(
        String enumOption,
        Category category,
        String description,
        int id,
        String rightClickOption,
        int interfaceGroupId,
        int interfaceChildId,
        int regionId,
        boolean houseTeleport
    ) {
        this.enumOption = enumOption;
        this.category = category;
        this.description = description;
        this.id = id;
        this.rightClickOption = rightClickOption;
        this.interfaceGroupId = interfaceGroupId;
        this.interfaceChildId = interfaceChildId;
        this.regionId = regionId;
        this.itemRequirements = new ArrayList<ItemRequirement>();
        this.houseTeleport = houseTeleport;
        this.overrideLocationName = "";
    }

    private void houseTeleportItemRequirements(FarmingHelperConfig config)
    {
        FarmingHelperConfig.OptionEnumHouseTele selectedOption = config.enumConfigHouseTele();

        switch (selectedOption) {
            case Law_air_earth_runes:
                itemRequirements.add(new ItemRequirement(
                    ItemID.AIR_RUNE,
                    1
                ));

                itemRequirements.add(new ItemRequirement(
                    ItemID.EARTH_RUNE,
                    1
                ));

                itemRequirements.add(new ItemRequirement(
                    ItemID.LAW_RUNE,
                    1
                ));

                break;

            // case Law_dust_runes:
            //     itemRequirements.add(new ItemRequirement(
            //         ItemID.DUST_RUNE,
            //         1
            //     ));
            //
            //     itemRequirements.add(new ItemRequirement(
            //         ItemID.LAW_RUNE,
            //         1
            //     ));
            //
            //     break;

            case Teleport_To_House:
                itemRequirements.add(new ItemRequirement(
                    ItemID.TELEPORT_TO_HOUSE,
                    1
                ));

                break;

            case Construction_cape:
                itemRequirements.add(new ItemRequirement(
                    ItemID.CONSTRUCT_CAPE,
                    1
                ));

                break;

            case Construction_cape_t:
                itemRequirements.add(new ItemRequirement(
                    ItemID.CONSTRUCT_CAPET,
                    1
                ));

                break;

            case Max_cape:
                itemRequirements.add(new ItemRequirement(
                    ItemID.MAX_CAPE,
                    1
                ));

                break;

            default:
                throw new IllegalStateException("Unexpected value: " + selectedOption);
        }
    }

    public Map<Integer, Integer> getItemRequirements(FarmingHelperConfig config)
    {
        Map<Integer, Integer> requirements = new HashMap<>();

        if (houseTeleport) {
            houseTeleportItemRequirements(config);
        }

        for (ItemRequirement itemRequirement : itemRequirements) {
            requirements.put(itemRequirement.getItemId(), itemRequirement.getQuantity());
        }

        return requirements;
    }

    public Teleport overrideLocationName(String overrideLocationName)
    {
        this.overrideLocationName = overrideLocationName;

        return this;
    }

    public String overrideLocationName()
    {
        return this.overrideLocationName;
    }

    public Category getCategory()
    {
        return category;
    }

    public int getRegionId()
    {
        return regionId;
    }

    public String getEnumOption()
    {
        return enumOption;
    }

    public String getDescription()
    {
        return description;
    }

    public String getRightClickOption()
    {
        return rightClickOption;
    }

    public int getId()
    {
        return id;
    }

    public int getInterfaceGroupId()
    {
        return interfaceGroupId;
    }

    public int getInterfaceChildId()
    {
        return interfaceChildId;
    }
}
