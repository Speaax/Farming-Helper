package com.easyfarming.overlays.highlighting;

import net.runelite.api.Point;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Multi-tile GameObjects are present on every occupied tile; only the SW/min tile is the
 * highlight anchor (RuneLite DevToolsOverlay pattern). Without this filter, allotment/hops
 * patches redraw overlapping clickboxes and tank FPS (issue #101).
 */
public class GameObjectHighlighterAnchorTileTest {

    @Test
    public void anchorTileMatchesSceneMinLocation() {
        Point min = new Point(10, 20);
        assertTrue(GameObjectHighlighter.isGameObjectAnchorTile(min, new Point(10, 20)));
    }

    @Test
    public void nonAnchorOccupiedTileIsRejected() {
        Point min = new Point(10, 20);
        assertFalse(GameObjectHighlighter.isGameObjectAnchorTile(min, new Point(11, 20)));
        assertFalse(GameObjectHighlighter.isGameObjectAnchorTile(min, new Point(10, 21)));
        assertFalse(GameObjectHighlighter.isGameObjectAnchorTile(min, new Point(12, 22)));
    }

    @Test
    public void nullLocationsAreRejected() {
        assertFalse(GameObjectHighlighter.isGameObjectAnchorTile(null, new Point(0, 0)));
        assertFalse(GameObjectHighlighter.isGameObjectAnchorTile(new Point(0, 0), null));
        assertFalse(GameObjectHighlighter.isGameObjectAnchorTile(null, null));
    }
}
