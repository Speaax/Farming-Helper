package com.easyfarming.overlays.utils;

import com.easyfarming.utils.Constants;
import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;

/**
 * Utility class for widget-related operations.
 */
public class WidgetHelper {
    private final Client client;
    
    public WidgetHelper(Client client) {
        this.client = client;
    }
    
    /**
     * Gets the child index of a widget by searching for text after a colon.
     */
    public int getChildIndex(String searchText, Widget parentWidget) {
        if (parentWidget == null) {
            return -1;
        }
        
        Widget[] children = parentWidget.getChildren();
        
        if (children == null) {
            return -1;
        }
        
        for (int index = 0; index < children.length; index++) {
            Widget child = children[index];
            String text = child.getText();
            
            if (text != null) {
                int colonIndex = text.indexOf(':');
                
                if (colonIndex != -1 && colonIndex + 1 < text.length()) {
                    String textAfterColon = text.substring(colonIndex + 1).trim();
                    
                    if (textAfterColon.equals(searchText)) {
                        return index;
                    }
                }
            }
        }
        
        return -1;
    }
    
    /**
     * Gets the child index for Portal Nexus widget.
     */
    public int getChildIndexPortalNexus(String searchText) {
        return getChildIndex(
            searchText,
            client.getWidget(Constants.WIDGET_PORTAL_NEXUS_PARENT, Constants.WIDGET_PORTAL_NEXUS_CHILD)
        );
    }
    
    /**
     * Gets the child index for Spirit Tree widget.
     */
    public int getChildIndexSpiritTree(String searchText) {
        return getChildIndex(
            searchText,
            client.getWidget(Constants.INTERFACE_SPIRIT_TREE, Constants.INTERFACE_SPIRIT_TREE_CHILD)
        );
    }
    
    /**
     * Checks if an interface is open.
     */
    public boolean isInterfaceOpen(int groupId, int childId) {
        Widget widget = client.getWidget(groupId, childId);
        return widget != null && !widget.isHidden();
    }
    
    /**
     * Dynamically detects the correct spellbook tab interface ID based on the current client mode.
     * @return The child ID for the magic spellbook tab, or -1 if not found
     */
    public int getSpellbookTabChildId() {
        if (isInterfaceOpen(161, 65)) {
            return 65;
        }
        if (isInterfaceOpen(164, 58)) {
            return 58;
        }
        if (isInterfaceOpen(161, 58)) {
            return 58;
        }
        if (isInterfaceOpen(164, 65)) {
            return 65;
        }
        return 65;
    }
    
    /**
     * Gets the correct group ID for the spellbook tab based on the current client mode.
     * @return The group ID for the spellbook tab
     */
    public int getSpellbookTabGroupId() {
        if (isInterfaceOpen(161, 65)) {
            return 161;
        }
        if (isInterfaceOpen(164, 58)) {
            return 164;
        }
        if (isInterfaceOpen(161, 58)) {
            return 161;
        }
        if (isInterfaceOpen(164, 65)) {
            return 164;
        }
        return 161;
    }
}

