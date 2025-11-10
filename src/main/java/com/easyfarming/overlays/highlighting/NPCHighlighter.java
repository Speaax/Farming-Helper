package com.easyfarming.overlays.highlighting;

import com.easyfarming.overlays.utils.ColorProvider;
import net.runelite.api.Client;
import net.runelite.api.IndexedObjectSet;
import net.runelite.api.NPC;

import javax.inject.Inject;
import java.awt.*;
import java.util.List;

/**
 * Handles highlighting of NPCs in the world.
 */
public class NPCHighlighter {
    private final Client client;
    private final ColorProvider colorProvider;
    
    @Inject
    public NPCHighlighter(Client client, ColorProvider colorProvider) {
        this.client = client;
        this.colorProvider = colorProvider;
    }
    
    /**
     * Highlights an NPC by name.
     */
    public void highlightNpc(Graphics2D graphics, String npcName) {
        IndexedObjectSet<? extends NPC> npcs = client.getTopLevelWorldView().npcs();
        
        if (npcs != null) {
            Color color = colorProvider.getLeftClickColorWithAlpha();
            for (NPC npc : npcs) {
                if (npc != null && npc.getName() != null && npc.getName().equals(npcName)) {
                    Polygon tilePolygon = npc.getCanvasTilePoly();
                    
                    if (tilePolygon != null) {
                        graphics.setColor(color);
                        graphics.draw(tilePolygon);
                        graphics.setColor(new Color(color.getRed(), 
                                                   color.getGreen(), 
                                                   color.getBlue(), 
                                                   color.getAlpha() / 5));
                        graphics.fill(tilePolygon);
                    }
                }
            }
        }
    }
    
    /**
     * Highlights multiple NPCs by name.
     */
    public void highlightNpcs(Graphics2D graphics, List<String> npcNames) {
        for (String npcName : npcNames) {
            highlightNpc(graphics, npcName);
        }
    }
}

