package com.easyfarming;

import static org.junit.Assert.assertEquals;

import com.easyfarming.overlays.highlighting.GameObjectHighlighter;
import com.easyfarming.utils.Constants;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.Player;
import net.runelite.api.Scene;
import net.runelite.api.Tile;
import net.runelite.api.WorldView;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ObjectID;
import org.junit.Test;

public class GameObjectHighlighterTest {

    @Test
    public void findsEachMultiTileGameObjectOnlyOnce() {
        int objectId = ObjectID.FARMING_REDWOOD_TREE_PATCH_0_5;
        GameObject gameObject = proxy(GameObject.class, values(
                "getId", objectId,
                "getHash", 123456789L));
        Tile tile = proxy(Tile.class, Collections.singletonMap("getGameObjects", new GameObject[]{gameObject}));

        Tile[][][] tiles = new Tile[1][Constants.SCENE_SIZE][Constants.SCENE_SIZE];
        tiles[0][0][0] = tile;
        tiles[0][0][1] = tile;
        tiles[0][1][0] = tile;
        tiles[0][1][1] = tile;

        Scene scene = proxy(Scene.class, Collections.singletonMap("getTiles", tiles));
        WorldView worldView = proxy(WorldView.class, values("getPlane", 0, "getScene", scene));
        Player player = proxy(Player.class, Collections.singletonMap(
                "getWorldLocation", new WorldPoint(1233, 3754, 0)));
        Client client = proxy(Client.class, values(
                "getLocalPlayer", player,
                "getTopLevelWorldView", worldView));

        GameObjectHighlighter highlighter = new GameObjectHighlighter(client, null);

        assertEquals(1, highlighter.findGameObjectsByID(objectId).size());
    }

    private static Map<String, Object> values(Object... entries) {
        Map<String, Object> values = new HashMap<>();
        for (int i = 0; i < entries.length; i += 2) {
            values.put((String) entries[i], entries[i + 1]);
        }
        return values;
    }

    @SuppressWarnings("unchecked")
    private static <T> T proxy(Class<T> type, Map<String, Object> values) {
        return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type}, (instance, method, args) -> {
            if (values.containsKey(method.getName())) {
                return values.get(method.getName());
            }
            Class<?> returnType = method.getReturnType();
            if (returnType == boolean.class) return false;
            if (returnType == byte.class) return (byte) 0;
            if (returnType == short.class) return (short) 0;
            if (returnType == int.class) return 0;
            if (returnType == long.class) return 0L;
            if (returnType == float.class) return 0F;
            if (returnType == double.class) return 0D;
            if (returnType == char.class) return '\0';
            return null;
        });
    }
}
