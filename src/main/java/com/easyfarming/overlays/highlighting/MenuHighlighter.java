package com.easyfarming.overlays.highlighting;

import com.easyfarming.overlays.utils.ColorProvider;
import net.runelite.api.Client;
import net.runelite.api.Menu;
import net.runelite.api.MenuEntry;
import net.runelite.client.util.ColorUtil;

import javax.inject.Inject;
import java.awt.*;

/**
 * Handles highlighting of menu entries (right-click options) for teleport items.
 * When the user right-clicks an item (e.g. Ardougne cloak, Skills necklace, Construction cape),
 * the correct submenu option (e.g. "Farm Teleport", "Farming Guild", "Teleport") is highlighted
 * in green so the user knows which option to click.
 */
public class MenuHighlighter {
    private final Client client;
    private final ColorProvider colorProvider;

    /** Solid green for menu option text so it is clearly visible. */
    private static final Color MENU_HIGHLIGHT_GREEN = new Color(0, 255, 0);

    @Inject
    public MenuHighlighter(Client client, ColorProvider colorProvider) {
        this.client = client;
        this.colorProvider = colorProvider;
    }

    /**
     * Highlights the right-click menu option the user should click (e.g. "Farm Teleport"
     * for Ardy cloak, "Farming Guild" for Skills necklace). Uses solid green text.
     */
    public void highlightRightClickOption(Graphics2D graphics, String option) {
        if (option == null || option.trim().isEmpty()) {
            return;
        }

        Menu menu = client.getMenu();
        if (menu == null) {
            return;
        }

        MenuEntry[] menuEntries = menu.getMenuEntries();
        if (menuEntries == null || menuEntries.length == 0) {
            return;
        }

        String search = option.trim().toLowerCase();

        for (int i = 0; i < menuEntries.length; i++) {
            MenuEntry entry = menuEntries[i];
            if (entry == null) {
                continue;
            }

            String optionText = entry.getOption();
            String target = entry.getTarget();

            if (!menuOptionMatches(optionText, target, search)) {
                continue;
            }

            if (optionText == null) {
                continue;
            }

            String rawText = optionText.replaceAll("<col=[^>]*>", "").replaceAll("</col>", "").trim();
            if (rawText.startsWith(">>>")) {
                break;
            }

            String highlightedText = ColorUtil.prependColorTag(rawText, MENU_HIGHLIGHT_GREEN);
            entry.setOption(highlightedText);
            menu.setMenuEntries(menuEntries);
            break;
        }
    }

    private boolean menuOptionMatches(String optionText, String target, String search) {
        if (search.isEmpty()) {
            return false;
        }
        if (optionText != null) {
            String opt = optionText.trim().toLowerCase();
            if (opt.equals(search)) {
                return true;
            }
            if (opt.startsWith(search + " ") || opt.startsWith(search + ">")) {
                return true;
            }
            if (opt.contains(" " + search + " ") || opt.endsWith(" " + search)) {
                return true;
            }
        }
        if (target != null) {
            String t = target.trim().toLowerCase();
            if (t.equals(search) || t.startsWith(search + " ")) {
                return true;
            }
        }
        return false;
    }
}

