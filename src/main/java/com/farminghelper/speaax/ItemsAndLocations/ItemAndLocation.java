package com.farminghelper.speaax.ItemsAndLocations;

import com.farminghelper.speaax.FarmingHelperConfig;
import com.farminghelper.speaax.FarmingHelperPlugin;
import com.farminghelper.speaax.Patch.ItemRequirement;
import com.farminghelper.speaax.Patch.Location;
import net.runelite.api.ItemID;

import java.util.ArrayList;
import java.util.List;

public class ItemAndLocation
{
    protected FarmingHelperConfig config;

    protected FarmingHelperPlugin plugin;

    public List<Location> locations = new ArrayList<>();

    public ItemAndLocation()
    {
    }

    public ItemAndLocation(FarmingHelperConfig config, FarmingHelperPlugin plugin)
    {
        this.config = config;
        this.plugin = plugin;
    }

    public Integer selectedCompostID()
    {
        FarmingHelperConfig.OptionEnumCompost selectedCompost = config.enumConfigCompost();

        switch (selectedCompost) {
            case Compost:
                return ItemID.COMPOST;

            case Supercompost:
                return ItemID.SUPERCOMPOST;

            case Ultracompost:
                return ItemID.ULTRACOMPOST;

            case Bottomless:
                return ItemID.BOTTOMLESS_COMPOST_BUCKET_22997;
        }

        return - 1;
    }

    public void setupLocations()
    {
        locations.clear();
    }
}
