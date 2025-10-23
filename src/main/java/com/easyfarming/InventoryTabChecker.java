package com.easyfarming;

import net.runelite.api.Client;
import net.runelite.api.gameval.VarbitID;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.widgets.Widget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Arrays;
import java.util.List;

public class InventoryTabChecker {
    private static final Logger log = LoggerFactory.getLogger(InventoryTabChecker.class);
    
    // Lists for each tab state
    private static final List<Integer> INVENTORY = Arrays.asList(3);
    private static final List<Integer> SPELLBOOK = Arrays.asList(6);

    public enum TabState {
        INVENTORY,
        SPELLBOOK,
        REST
    }

    public static TabState checkTab(Client client, int varbitIndex) {
        int varbitValue = client.getVarcIntValue(varbitIndex);
        
        // Original working logic - keep this for now
        List<Integer> INVENTORY = Arrays.asList(3);
        List<Integer> SPELLBOOK = Arrays.asList(6);
        
        if (INVENTORY.contains(varbitValue)) {
            return TabState.INVENTORY;
        } else if (SPELLBOOK.contains(varbitValue)) {
            return TabState.SPELLBOOK;
        } else {
            return TabState.REST;
        }
    }}