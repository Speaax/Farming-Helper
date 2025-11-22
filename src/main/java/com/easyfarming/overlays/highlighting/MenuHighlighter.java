package com.easyfarming.overlays.highlighting;

import com.easyfarming.overlays.utils.ColorProvider;
import net.runelite.api.Client;
import net.runelite.api.Menu;
import net.runelite.api.MenuEntry;
import net.runelite.client.util.ColorUtil;

import javax.inject.Inject;
import java.awt.*;

/**
 * Handles highlighting of menu entries (right-click options).
 */
public class MenuHighlighter {
    private final Client client;
    private final ColorProvider colorProvider;
    
    @Inject
    public MenuHighlighter(Client client, ColorProvider colorProvider) {
        this.client = client;
        this.colorProvider = colorProvider;
    }
    
    /**
     * Highlights a right-click menu option.
     */
    public void highlightRightClickOption(Graphics2D graphics, String option) {
        // Skip if option is null or empty to avoid matching all menu entries
        if (option == null || option.trim().isEmpty()) {
            return;
        }
        
        Menu menu = client.getMenu();
        if (menu == null) {
            return;
        }
        
        MenuEntry[] menuEntries = menu.getMenuEntries();
        if (menuEntries == null) {
            return;
        }
        
        Color color = colorProvider.getRightClickColorWithAlpha();
        
        for (int i = 0; i < menuEntries.length; i++) {
            MenuEntry entry = menuEntries[i];
            if (entry == null) {
                continue;
            }
            
            String optionText = entry.getOption();
            String target = entry.getTarget();
            
            // Check if option matches exactly or contains the search text
            // Also check target in case the option is formatted differently
            boolean matches = false;
            if (optionText != null) {
                // Exact match
                if (optionText.equalsIgnoreCase(option)) {
                    matches = true;
                }
                // Check if option starts with the search text (e.g., "Rub > Skills necklace")
                else if (optionText.toLowerCase().startsWith(option.toLowerCase() + " >") ||
                         optionText.toLowerCase().startsWith(option.toLowerCase() + " ")) {
                    matches = true;
                }
            }
            
            // Also check target text - use exact match or starts-with to avoid matching multiple entries
            if (!matches && target != null) {
                // Use exact match or starts-with, NOT contains (to avoid matching "Camelot Teleport" in multiple entries)
                if (target.equalsIgnoreCase(option) || 
                    target.toLowerCase().startsWith(option.toLowerCase())) {
                    matches = true;
                }
            }
            
            if (matches && optionText != null) {
                // Only highlight if not already highlighted
                // Strip color tags using regex (RuneLite color tags are <col=...>text</col>)
                String rawText = optionText.replaceAll("<col=[^>]*>", "").replaceAll("</col>", "");
                // Check if it already starts with ">>>" (with or without space)
                String trimmedText = rawText.trim();
                if (!trimmedText.startsWith(">>>")) {
                    // Remove any existing color tags and highlight markers before adding new highlight
                    String highlightedText = ColorUtil.prependColorTag(">>> " + trimmedText, color);
                    entry.setOption(highlightedText);
                    menu.setMenuEntries(menuEntries);
                }
                break;
            }
        }
    }
}

