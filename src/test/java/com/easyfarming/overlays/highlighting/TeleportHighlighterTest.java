package com.easyfarming.overlays.highlighting;

import static org.junit.Assert.assertEquals;

import com.easyfarming.utils.Constants;
import org.junit.Test;

public class TeleportHighlighterTest {

    @Test
    public void fishingTrawlerUsesSelectorUntilItIsSelected() {
        assertEquals(Constants.GROUPING_DROPDOWN_CHILD,
                TeleportHighlighter.getFishingTrawlerGroupingChild(0));
        assertEquals(Constants.GROUPING_TELEPORT_CHILD,
                TeleportHighlighter.getFishingTrawlerGroupingChild(Constants.GROUPING_FISHING_TRAWLER_GAME_ID));
    }
}
