package com.easyfarming;

import net.runelite.api.Client;
import java.util.List;

public class InventoryTabChecker {
    public enum TabState {
        INVENTORY,
        SPELLBOOK,
        REST
    }

    public static TabState checkTab(Client client, int varbitIndex) {
        int varbitValue = client.getVarcIntValue(varbitIndex);
        
        // Original working logic - keep this for now
        List<Integer> INVENTORY = List.of(3);
        List<Integer> SPELLBOOK = List.of(6);
        
        if (INVENTORY.contains(varbitValue)) {
            return TabState.INVENTORY;
        } else if (SPELLBOOK.contains(varbitValue)) {
            return TabState.SPELLBOOK;
        } else {
            return TabState.REST;
        }
    }}