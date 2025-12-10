package com.easyfarming;

import net.runelite.api.Client;
import net.runelite.api.gameval.VarbitID;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.widgets.Widget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Arrays;
import java.util.List;

/**
 * Utility class for checking which tab is currently active in the client interface.
 * Determines if the player has the inventory or spellbook tab open.
 */
public class InventoryTabChecker {
    private static final Logger log = LoggerFactory.getLogger(InventoryTabChecker.class);
    
    /**
     * Varbit values that indicate the inventory tab is open.
     */
    private static final List<Integer> INVENTORY = Arrays.asList(3);
    
    /**
     * Varbit values that indicate the spellbook tab is open.
     */
    private static final List<Integer> SPELLBOOK = Arrays.asList(6);

    public enum TabState {
        INVENTORY,
        SPELLBOOK,
        REST
    }

    public static TabState checkTab(Client client, int varbitIndex) {
        int varbitValue = client.getVarcIntValue(varbitIndex);
        
        if (INVENTORY.contains(varbitValue)) {
            return TabState.INVENTORY;
        } else if (SPELLBOOK.contains(varbitValue)) {
            return TabState.SPELLBOOK;
        } else {
            return TabState.REST;
        }
    }}