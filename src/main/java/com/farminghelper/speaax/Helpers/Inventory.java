package com.farminghelper.speaax.Helpers;

import net.runelite.api.Client;
import net.runelite.api.VarClientInt;

import java.util.Arrays;
import java.util.List;

public class Inventory
{
    private static final List<Integer> inventory = Arrays.asList(3);

    private static final List<Integer> spellbook = Arrays.asList(6);

    public enum Tab
    {
        INVENTORY,
        SPELLBOOK,
        REST
    }

    public static Tab getCurrentTab(Client client)
    {
        int varbitValue = client.getVarcIntValue(VarClientInt.INVENTORY_TAB);

        if (inventory.contains(varbitValue)) {
            return Tab.INVENTORY;
        } else if (spellbook.contains(varbitValue)) {
            return Tab.SPELLBOOK;
        } else {
            return Tab.REST;
        }
    }
}