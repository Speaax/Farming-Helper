package com.easyfarming.overlays.highlighting;

import com.easyfarming.EasyFarmingPlugin;
import net.runelite.api.Client;
import net.runelite.api.DecorativeObject;
import net.runelite.api.GameObject;
import net.runelite.api.GroundObject;
import net.runelite.api.ObjectComposition;
import net.runelite.api.Perspective;
import net.runelite.api.Tile;
import net.runelite.api.TileObject;
import net.runelite.api.WorldView;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import com.easyfarming.utils.Constants;

import javax.inject.Inject;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles highlighting of game objects in the world.
 * Caches scene lookups per (objectId, region, plane) to avoid full scene scans every frame,
 * which caused severe FPS drops when e.g. showing "Rake weeds" overlay (issue #63).
 * Multi-tile GameObjects are kept only on their SceneMinLocation tile so allotment/hops
 * patches are not redrawn once per occupied tile (issue #101).
 */
public class GameObjectHighlighter {
    private final Client client;
    private final EasyFarmingPlugin plugin;

    /** Cache of objectId -> list of game objects in current scene. */
    private final Map<Integer, List<GameObject>> objectCache = new HashMap<>();
    /** Cache of objectId -> list of decorative objects (some tree patches are decorative, not game objects). */
    private final Map<Integer, List<DecorativeObject>> decorativeObjectCache = new HashMap<>();
    /** Cache of objectId -> list of ground objects (empty tree patch ready for sapling is often a ground object). */
    private final Map<Integer, List<GroundObject>> groundObjectCache = new HashMap<>();
    /** Scene key when cache was built: regionId * 10 + plane. */
    private int lastSceneKey = -1;

    @Inject
    public GameObjectHighlighter(Client client, EasyFarmingPlugin plugin) {
        this.client = client;
        this.plugin = plugin;
    }

    /**
     * Clears scene object caches. Call when farming patch visuals change (e.g. weeds →
     * growing → harvestable) so a prior empty/stale lookup cannot stick for the region visit.
     */
    public void invalidateCache() {
        objectCache.clear();
        decorativeObjectCache.clear();
        groundObjectCache.clear();
        lastSceneKey = -1;
    }

    /**
     * Finds all game objects with the specified ID in the current scene.
     * Results are cached per scene (region + plane); cache is cleared when the player
     * changes region or plane so we do not scan the full 104x104 scene every frame.
     */
    public List<GameObject> findGameObjectsByID(int objectID) {
        if (client.getLocalPlayer() == null) {
            return new ArrayList<>();
        }
        WorldView wv = client.getTopLevelWorldView();
        if (wv == null || wv.getScene() == null) {
            return new ArrayList<>();
        }
        int plane = wv.getPlane();
        int regionId = client.getLocalPlayer().getWorldLocation().getRegionID();
        int sceneKey = regionId * 10 + plane;

        if (sceneKey != lastSceneKey) {
            objectCache.clear();
            decorativeObjectCache.clear();
            groundObjectCache.clear();
            lastSceneKey = sceneKey;
        }

        List<GameObject> cached = objectCache.get(objectID);
        if (cached != null) {
            return cached;
        }

        List<GameObject> gameObjects = new ArrayList<>();
        Tile[][][] tiles = wv.getScene().getTiles();
        for (int x = 0; x < Constants.SCENE_SIZE; x++) {
            for (int y = 0; y < Constants.SCENE_SIZE; y++) {
                Tile tile = tiles[plane][x][y];
                if (tile == null) {
                    continue;
                }
                for (GameObject gameObject : tile.getGameObjects()) {
                    // Multi-tile objects appear on every occupied tile; only keep the SW/min anchor
                    // (same pattern as RuneLite DevToolsOverlay) to avoid drawing N overlapping clickboxes.
                    if (gameObject != null
                            && isGameObjectAnchorTile(gameObject.getSceneMinLocation(), tile.getSceneLocation())
                            && objectIdMatches(gameObject.getId(), objectID)) {
                        gameObjects.add(gameObject);
                    }
                }
            }
        }
        objectCache.put(objectID, gameObjects);
        return gameObjects;
    }

    /**
     * Returns true when {@code tileSceneLocation} is the southwest/min tile of a (possibly multi-tile)
     * game object. Package-visible for unit tests.
     */
    static boolean isGameObjectAnchorTile(net.runelite.api.Point sceneMinLocation,
                                          net.runelite.api.Point tileSceneLocation) {
        return sceneMinLocation != null
                && tileSceneLocation != null
                && sceneMinLocation.equals(tileSceneLocation);
    }

    /**
     * Finds decorative objects with the given ID (same matching rules as {@link #findGameObjectsByID}).
     */
    public List<DecorativeObject> findDecorativeObjectsByID(int objectID) {
        if (client.getLocalPlayer() == null) {
            return new ArrayList<>();
        }
        WorldView wv = client.getTopLevelWorldView();
        if (wv == null || wv.getScene() == null) {
            return new ArrayList<>();
        }
        int plane = wv.getPlane();
        int regionId = client.getLocalPlayer().getWorldLocation().getRegionID();
        int sceneKey = regionId * 10 + plane;

        if (sceneKey != lastSceneKey) {
            objectCache.clear();
            decorativeObjectCache.clear();
            groundObjectCache.clear();
            lastSceneKey = sceneKey;
        }

        List<DecorativeObject> cached = decorativeObjectCache.get(objectID);
        if (cached != null) {
            return cached;
        }

        List<DecorativeObject> decorativeObjects = new ArrayList<>();
        Tile[][][] tiles = wv.getScene().getTiles();
        for (int x = 0; x < Constants.SCENE_SIZE; x++) {
            for (int y = 0; y < Constants.SCENE_SIZE; y++) {
                Tile tile = tiles[plane][x][y];
                if (tile == null) {
                    continue;
                }
                DecorativeObject decorativeObject = tile.getDecorativeObject();
                if (decorativeObject != null && objectIdMatches(decorativeObject.getId(), objectID)) {
                    decorativeObjects.add(decorativeObject);
                }
            }
        }
        decorativeObjectCache.put(objectID, decorativeObjects);
        return decorativeObjects;
    }

    /**
     * Finds ground objects with the given ID (e.g. cleared tree patch soil — use sapling — is often a ground object).
     */
    public List<GroundObject> findGroundObjectsByID(int objectID) {
        if (client.getLocalPlayer() == null) {
            return new ArrayList<>();
        }
        WorldView wv = client.getTopLevelWorldView();
        if (wv == null || wv.getScene() == null) {
            return new ArrayList<>();
        }
        int plane = wv.getPlane();
        int regionId = client.getLocalPlayer().getWorldLocation().getRegionID();
        int sceneKey = regionId * 10 + plane;

        if (sceneKey != lastSceneKey) {
            objectCache.clear();
            decorativeObjectCache.clear();
            groundObjectCache.clear();
            lastSceneKey = sceneKey;
        }

        List<GroundObject> cached = groundObjectCache.get(objectID);
        if (cached != null) {
            return cached;
        }

        List<GroundObject> groundObjects = new ArrayList<>();
        Tile[][][] tiles = wv.getScene().getTiles();
        for (int x = 0; x < Constants.SCENE_SIZE; x++) {
            for (int y = 0; y < Constants.SCENE_SIZE; y++) {
                Tile tile = tiles[plane][x][y];
                if (tile == null) {
                    continue;
                }
                GroundObject groundObject = tile.getGroundObject();
                if (groundObject != null && objectIdMatches(groundObject.getId(), objectID)) {
                    groundObjects.add(groundObject);
                }
            }
        }
        groundObjectCache.put(objectID, groundObjects);
        return groundObjects;
    }

    /**
     * Returns true if the scene object id matches the target (including multilocs / impostors).
     * Farming patches keep a stable base id on the TileObject; the visible harvestable model is an
     * impostor. Match base↔base, current impostor, and impostor-id arrays (RuneLite ObjectIndicators).
     */
    private boolean objectIdMatches(int sceneId, int targetId) {
        if (sceneId == targetId) {
            return true;
        }
        try {
            ObjectComposition sceneComp = client.getObjectDefinition(sceneId);
            if (sceneComp != null) {
                if (impostorIdsContain(sceneComp, targetId)) {
                    return true;
                }
                ObjectComposition current = resolveCurrentImpostor(sceneComp);
                if (current != null && current.getId() == targetId) {
                    return true;
                }
            }
            ObjectComposition targetComp = client.getObjectDefinition(targetId);
            if (targetComp != null) {
                if (impostorIdsContain(targetComp, sceneId)) {
                    return true;
                }
                ObjectComposition current = resolveCurrentImpostor(targetComp);
                if (current != null && current.getId() == sceneId) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean impostorIdsContain(ObjectComposition comp, int id) {
        int[] impostorIds = comp.getImpostorIds();
        if (impostorIds == null) {
            return false;
        }
        for (int impostorId : impostorIds) {
            if (impostorId == id) {
                return true;
            }
        }
        return false;
    }

    private static ObjectComposition resolveCurrentImpostor(ObjectComposition comp) {
        if (comp == null) {
            return null;
        }
        try {
            if (comp.getImpostorIds() != null) {
                ObjectComposition impostor = comp.getImpostor();
                return impostor != null ? impostor : comp;
            }
        } catch (Exception ignored) {
            // Impostor resolution can NPE when multilocs are unset
        }
        return comp;
    }
    
    /**
     * Draws the clickbox for a game object.
     * Handles null/invalid objects gracefully (e.g. when patch is transitioning).
     */
    public void drawGameObjectClickbox(Graphics2D graphics, GameObject gameObject, Color color) {
        drawTileObjectClickbox(graphics, gameObject, color);
    }

    /**
     * Draws highlight for any tile object. Uses convex hull when clickbox is null (e.g. empty tree
     * patch ready for sapling). Falls back to tile polys when both are null — common for some
     * harvestable herb multilocs (e.g. Civitas / Ortus Farm) where clickbox is missing.
     */
    public void drawTileObjectClickbox(Graphics2D graphics, TileObject tileObject, Color color) {
        if (tileObject == null) {
            return;
        }
        try {
            Shape shape = tileObject.getClickbox();
            if (shape == null) {
                if (tileObject instanceof GameObject) {
                    shape = ((GameObject) tileObject).getConvexHull();
                } else if (tileObject instanceof DecorativeObject) {
                    shape = ((DecorativeObject) tileObject).getConvexHull();
                } else if (tileObject instanceof GroundObject) {
                    shape = ((GroundObject) tileObject).getConvexHull();
                }
            }
            if (shape != null) {
                graphics.setColor(color);
                graphics.draw(shape);
                graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha() / 5));
                graphics.fill(shape);
                return;
            }
            drawTileFootprintFallback(graphics, tileObject, color);
        } catch (Exception e) {
            // Ignore errors when object is transitioning (e.g. patch just cleared)
            try {
                drawTileFootprintFallback(graphics, tileObject, color);
            } catch (Exception ignored) {
                // still transitioning
            }
        }
    }

    /**
     * Highlights the object's tile footprint when clickbox/hull are unavailable.
     */
    private void drawTileFootprintFallback(Graphics2D graphics, TileObject tileObject, Color color) {
        WorldPoint worldPoint = tileObject.getWorldLocation();
        if (worldPoint == null) {
            return;
        }
        WorldView wv = client.getTopLevelWorldView();
        if (wv == null) {
            return;
        }
        LocalPoint localPoint = LocalPoint.fromWorld(wv, worldPoint);
        if (localPoint == null) {
            return;
        }

        int sizeX = 1;
        int sizeY = 1;
        try {
            ObjectComposition comp = client.getObjectDefinition(tileObject.getId());
            ObjectComposition visual = resolveCurrentImpostor(comp);
            if (visual != null) {
                sizeX = Math.max(1, visual.getSizeX());
                sizeY = Math.max(1, visual.getSizeY());
            }
        } catch (Exception ignored) {
            // keep 1x1
        }

        Color fill = new Color(color.getRed(), color.getGreen(), color.getBlue(), Math.max(1, color.getAlpha() / 5));
        for (int dx = 0; dx < sizeX; dx++) {
            for (int dy = 0; dy < sizeY; dy++) {
                LocalPoint tileLocal = localPoint.plus(
                        dx * Perspective.LOCAL_TILE_SIZE,
                        dy * Perspective.LOCAL_TILE_SIZE);
                Polygon poly = Perspective.getCanvasTilePoly(client, tileLocal);
                if (poly == null) {
                    continue;
                }
                graphics.setColor(color);
                graphics.draw(poly);
                graphics.setColor(fill);
                graphics.fill(poly);
            }
        }
    }
    
    /**
     * Draws highlights for all scene objects matching {@code objectId} without allocating an overlay.
     *
     * @return true if at least one matching object was found in the scene
     */
    public boolean renderGameObjectHighlight(Graphics2D graphics, int objectId, Color color) {
        boolean found = false;
        for (GameObject gameObject : findGameObjectsByID(objectId)) {
            drawGameObjectClickbox(graphics, gameObject, color);
            found = true;
        }
        for (DecorativeObject decorativeObject : findDecorativeObjectsByID(objectId)) {
            drawTileObjectClickbox(graphics, decorativeObject, color);
            found = true;
        }
        for (GroundObject groundObject : findGroundObjectsByID(objectId)) {
            drawTileObjectClickbox(graphics, groundObject, color);
            found = true;
        }
        return found;
    }

    /**
     * Highlights a rectangular tile area around {@code worldPoint} (SW corner). Used when a
     * farming patch object cannot be resolved (common for empty Trollheim/Weiss herb soil).
     */
    public void highlightWorldPointArea(Graphics2D graphics, WorldPoint worldPoint, int sizeX, int sizeY, Color color) {
        if (graphics == null || worldPoint == null || color == null) {
            return;
        }
        WorldView wv = client.getTopLevelWorldView();
        if (wv == null) {
            return;
        }
        LocalPoint localPoint = LocalPoint.fromWorld(wv, worldPoint);
        if (localPoint == null) {
            return;
        }
        int tilesX = Math.max(1, sizeX);
        int tilesY = Math.max(1, sizeY);
        Color fill = new Color(color.getRed(), color.getGreen(), color.getBlue(), Math.max(1, color.getAlpha() / 5));
        for (int dx = 0; dx < tilesX; dx++) {
            for (int dy = 0; dy < tilesY; dy++) {
                LocalPoint tileLocal = localPoint.plus(
                        dx * Perspective.LOCAL_TILE_SIZE,
                        dy * Perspective.LOCAL_TILE_SIZE);
                Polygon poly = Perspective.getCanvasTilePoly(client, tileLocal);
                if (poly == null) {
                    continue;
                }
                graphics.setColor(color);
                graphics.draw(poly);
                graphics.setColor(fill);
                graphics.fill(poly);
            }
        }
    }

    /**
     * Draws highlights for multiple object ids (each id uses the scene cache from {@link #findGameObjectsByID}).
     */
    public void renderGameObjectHighlights(Graphics2D graphics, Iterable<Integer> objectIds, Color color) {
        for (Integer objectId : objectIds) {
            if (objectId != null) {
                renderGameObjectHighlight(graphics, objectId, color);
            }
        }
    }
    
    /**
     * Creates an overlay that highlights a game object by ID.
     */
    public Overlay highlightGameObject(int objectId, Color color) {
        return new Overlay() {
            @Override
            public Dimension render(Graphics2D graphics) {
                renderGameObjectHighlight(graphics, objectId, color);
                return null;
            }
        };
    }
}
