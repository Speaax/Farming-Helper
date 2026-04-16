package com.easyfarming.overlays.highlighting;

import com.easyfarming.utils.Constants;
import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;

import javax.inject.Inject;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

/**
 * Handles highlighting of farmers for protection payments.
 */
public class FarmerHighlighter {
    private final Client client;
    private final NPCHighlighter npcHighlighter;
    private final WidgetHighlighter widgetHighlighter;
    
    @Inject
    public FarmerHighlighter(Client client, NPCHighlighter npcHighlighter, WidgetHighlighter widgetHighlighter) {
        this.client = client;
        this.npcHighlighter = npcHighlighter;
        this.widgetHighlighter = widgetHighlighter;
    }
    
    /**
     * Highlights farmers by name and their interface if open.
     */
    public void highlightFarmers(Graphics2D graphics, List<String> farmers) {
        if (!isInterfaceOpen(Constants.INTERFACE_FARMER, 1)) {
            for (String farmer : farmers) {
                npcHighlighter.highlightNpc(graphics, farmer);
            }
        } else {
            Widget widget = client.getWidget(Constants.INTERFACE_FARMER, 1);
            widgetHighlighter.highlightDynamicComponent(graphics, widget, 1);
        }
    }
    
    /**
     * Highlights tree farmers.
     */
    public void highlightTreeFarmers(Graphics2D graphics) {
        highlightFarmers(graphics, Arrays.asList(
            "Alain",         // taverley
            "Aub",           // Nemus Retreat
            "Fayeth",        // Lumbridge
            "Heskel",        // Falador
            "Prissy Scilla", // Gnome Stronghold
            "Rosie",         // Farming Guild
            "Treznor"        // Varrock
        ));
    }
    
    /**
     * Highlights fruit tree farmers.
     */
    public void highlightFruitTreeFarmers(Graphics2D graphics) {
        highlightFarmers(graphics, Arrays.asList(
            "Bolongo", // Gnome Stronghold
            "Ellena",  // Catherby
            "Ehecatl", // Kastori
            "Garth",   // Brimhaven
            "Gileth",  // Tree Gnome Village
            "Liliwen", // Lletya
            "Nikkie"   // Farming Guild
        ));
    }

    /**
     * Highlights allotment/herb/flower farmers.
     * These patch types share the same farmer NPC at each location.
     */
    public void highlightAllotmentFarmers(Graphics2D graphics) {
        highlightFarmers(graphics, Arrays.asList(
            "Elstan",   // Falador
            "Dantaera", // Catherby
            "Kragen",   // Ardougne
            "Marisi",   // Kourend
            "Lyra",     // Morytania
            "Alan"      // Farming Guild
        ));
    }

    /**
     * Highlights hops farmers.
     */
    public void highlightHopsFarmers(Graphics2D graphics) {
        highlightFarmers(graphics, Arrays.asList(
            "Vasquen",         // Lumbridge
            "Rhonen",          // Seers Village
            "Selena",          // Yanille
            "Brother Althric", // Entrana
            "Ercos"            // Aldarin
        ));
    }
    
    private boolean isInterfaceOpen(int groupId, int childId) {
        Widget widget = client.getWidget(groupId, childId);
        return widget != null && !widget.isHidden();
    }
}

