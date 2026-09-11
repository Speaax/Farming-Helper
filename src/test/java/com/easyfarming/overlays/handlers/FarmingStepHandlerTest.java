package com.easyfarming.overlays.handlers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.easyfarming.AreaCheck;
import com.easyfarming.EasyFarmingConfig;
import com.easyfarming.EasyFarmingPlugin;
import com.easyfarming.core.Location;
import com.easyfarming.core.Teleport;
import com.easyfarming.customrun.LocationCatalog;
import com.easyfarming.customrun.PatchTypes;
import com.easyfarming.overlays.utils.ColorProvider;
import com.easyfarming.utils.Constants;
import java.lang.reflect.Proxy;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;
import org.junit.Test;

public class FarmingStepHandlerTest {

    @Test
    public void redwoodFarmingGuidanceKeepsItsArrowUntilFiveTilesAway() {
        HintArrowClient clientState = new HintArrowClient(new WorldPoint(1240, 3754, 0));
        TestPlugin plugin = new TestPlugin();
        FarmingStepHandler handler = handler(clientState.client(), plugin);

        handler.specialTreeSteps(null, redwoodTeleport(plugin), PatchTypes.REDWOOD, "Farming Guild");

        assertEquals(Constants.FARMING_GUILD_REDWOOD_PATCH_POINT, clientState.hintArrow);

        clientState.playerLocation = new WorldPoint(1237, 3754, 0);
        handler.specialTreeSteps(null, redwoodTeleport(plugin), PatchTypes.REDWOOD, "Farming Guild");

        assertNull(clientState.hintArrow);
        assertEquals(1, clientState.clearCalls);
    }

    @Test
    public void redwoodCloseRangeDoesNotClearAnNpcArrow() {
        HintArrowClient clientState = new HintArrowClient(new WorldPoint(1237, 3754, 0));
        clientState.npcHintActive = true;
        TestPlugin plugin = new TestPlugin();
        FarmingStepHandler handler = handler(clientState.client(), plugin);

        handler.specialTreeSteps(null, redwoodTeleport(plugin), PatchTypes.REDWOOD, "Farming Guild");

        assertEquals(0, clientState.clearCalls);
        assertTrue(clientState.npcHintActive);
    }

    private static FarmingStepHandler handler(Client client, TestPlugin plugin) {
        EasyFarmingConfig config = plugin.getConfig();
        return new FarmingStepHandler(
                client, plugin, config, new AreaCheck(client), null, null, null,
                null, null, new ColorProvider(config), null, null, null);
    }

    private static Teleport redwoodTeleport(TestPlugin plugin) {
        Location location = plugin.getLocationCatalog()
                .getLocationForPatch("Farming Guild", PatchTypes.REDWOOD);
        return location.getTeleportOptions().get(0);
    }

    private static final class HintArrowClient {
        private WorldPoint playerLocation;
        private WorldPoint hintArrow;
        private boolean npcHintActive;
        private int clearCalls;

        private HintArrowClient(WorldPoint playerLocation) {
            this.playerLocation = playerLocation;
        }

        private Client client() {
            Player player = (Player) Proxy.newProxyInstance(
                    Player.class.getClassLoader(), new Class<?>[]{Player.class},
                    (instance, method, args) -> "getWorldLocation".equals(method.getName())
                            ? playerLocation : defaultValue(method.getReturnType()));
            return (Client) Proxy.newProxyInstance(
                    Client.class.getClassLoader(), new Class<?>[]{Client.class},
                    (instance, method, args) -> {
                        if ("getLocalPlayer".equals(method.getName())) {
                            return player;
                        }
                        if ("getVarbitValue".equals(method.getName())) {
                            return 56;
                        }
                        if ("getHintArrowPoint".equals(method.getName())) {
                            return hintArrow;
                        }
                        if ("setHintArrow".equals(method.getName())
                                && args != null && args.length == 1 && args[0] instanceof WorldPoint) {
                            hintArrow = (WorldPoint) args[0];
                            npcHintActive = false;
                            return null;
                        }
                        if ("clearHintArrow".equals(method.getName())) {
                            hintArrow = null;
                            npcHintActive = false;
                            clearCalls++;
                            return null;
                        }
                        return defaultValue(method.getReturnType());
                    });
        }
    }

    private static Object defaultValue(Class<?> returnType) {
        if (returnType == boolean.class) return false;
        if (returnType == byte.class) return (byte) 0;
        if (returnType == short.class) return (short) 0;
        if (returnType == int.class) return 0;
        if (returnType == long.class) return 0L;
        if (returnType == float.class) return 0F;
        if (returnType == double.class) return 0D;
        if (returnType == char.class) return '\0';
        return null;
    }

    private static final class TestPlugin extends EasyFarmingPlugin {
        private final EasyFarmingConfig config = new EasyFarmingConfig() { };
        private LocationCatalog catalog;

        @Override
        public EasyFarmingConfig getConfig() {
            return config;
        }

        @Override
        public LocationCatalog getLocationCatalog() {
            if (catalog == null) {
                catalog = new LocationCatalog(this);
            }
            return catalog;
        }

        @Override
        public void addTextToInfoBox(String text) {
        }
    }
}
