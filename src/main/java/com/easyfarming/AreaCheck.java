package com.easyfarming;

import net.runelite.api.Client;
import net.runelite.api.coords.WorldPoint;

import javax.inject.Inject;

public class AreaCheck {
    private Client client;

    @Inject
    public AreaCheck(Client client) {
        this.client = client;
    }

    public boolean isPlayerWithinArea(WorldPoint centerTile, int range) {
        WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();

        int minX = centerTile.getX() - range;
        int maxX = centerTile.getX() + range;
        int minY = centerTile.getY() - range;
        int maxY = centerTile.getY() + range;

        return playerLocation.getX() >= minX &&
                playerLocation.getX() <= maxX &&
                playerLocation.getY() >= minY &&
                playerLocation.getY() <= maxY;
    }
}