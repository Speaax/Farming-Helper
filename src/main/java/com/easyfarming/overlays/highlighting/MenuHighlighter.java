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
        Menu menu = client.getMenu();
        MenuEntry[] menuEntries = menu.getMenuEntries();
        Color color = colorProvider.getRightClickColorWithAlpha();
        
        for (int i = 0; i < menuEntries.length; i++) {
            MenuEntry entry = menuEntries[i];
            String optionText = entry.getOption();
            
            if (optionText.equalsIgnoreCase(option)) {
                String highlightedText = ColorUtil.prependColorTag(">>> " + optionText, color);
                entry.setOption(highlightedText);
                menu.setMenuEntries(menuEntries);
                break;
            }
        }
    }
}

