package com.easyfarming.overlays.highlighting;

import static org.junit.Assert.assertEquals;

import com.easyfarming.customrun.PatchTypes;
import com.easyfarming.utils.Constants;
import java.awt.Color;
import java.awt.Graphics2D;
import org.junit.Test;

public class PatchHighlighterTest {

    @Test
    public void redwoodGuidanceDoesNotRenderObjectHighlights() {
        RecordingGameObjectHighlighter gameObjects = new RecordingGameObjectHighlighter();
        PatchHighlighter patches = new PatchHighlighter(null, null, gameObjects);

        patches.highlightSpecificSpecialTreePatch(
                null, Constants.FARMING_GUILD_REDWOOD_ROOT_OBJECT_ID, Color.YELLOW);
        patches.highlightSpecialTreePatch("Farming Guild", PatchTypes.REDWOOD, null, Color.YELLOW);

        assertEquals(0, gameObjects.renderCalls);
    }

    @Test
    public void otherSpecialTreesStillRenderObjectHighlights() {
        RecordingGameObjectHighlighter gameObjects = new RecordingGameObjectHighlighter();
        PatchHighlighter patches = new PatchHighlighter(null, null, gameObjects);

        patches.highlightSpecificSpecialTreePatch(
                null, Constants.FARMING_GUILD_CELASTRUS_ROOT_OBJECT_ID, Color.YELLOW);
        patches.highlightSpecialTreePatch("Farming Guild", PatchTypes.CELASTRUS, null, Color.YELLOW);

        assertEquals(2, gameObjects.renderCalls);
    }

    private static final class RecordingGameObjectHighlighter extends GameObjectHighlighter {
        private int renderCalls;

        private RecordingGameObjectHighlighter() {
            super(null, null);
        }

        @Override
        public void renderGameObjectHighlight(Graphics2D graphics, int objectId, Color color) {
            renderCalls++;
        }

        @Override
        public void renderGameObjectHighlights(Graphics2D graphics, Iterable<Integer> objectIds, Color color) {
            renderCalls++;
        }
    }
}
