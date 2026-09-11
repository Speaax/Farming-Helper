package com.easyfarming;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

import com.easyfarming.customrun.PatchTypes;
import com.easyfarming.customrun.RunLocation;
import com.easyfarming.ui.CustomRunFilterBar;
import com.easyfarming.ui.CustomRunLocationSubPanel;
import com.easyfarming.ui.components.WrapLayout;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import net.runelite.api.gameval.ItemID;
import org.junit.Test;

public class SpecialTreeUiTest {

    @Test
    public void farmingGuildPatchIconsUseFiveColumnGrid() throws Exception {
        CustomRunLocationSubPanel panel = new CustomRunLocationSubPanel(
                new TestPlugin(), null, "Farming Guild",
                new RunLocation("Farming Guild", "Spirit_Tree", Collections.emptyList()), null);
        Field field = CustomRunLocationSubPanel.class.getDeclaredField("patchIconsPanel");
        field.setAccessible(true);
        JPanel icons = (JPanel) field.get(panel);

        assertEquals(7, icons.getComponentCount());
        assertTrue(icons.getLayout() instanceof GridLayout);
        assertEquals(5, ((GridLayout) icons.getLayout()).getColumns());
    }

    @Test
    public void otherLocationPatchIconsKeepWrappingLayout() throws Exception {
        CustomRunLocationSubPanel panel = locationPanel("Fossil Island", "Digsite_pendant");
        Field field = CustomRunLocationSubPanel.class.getDeclaredField("patchIconsPanel");
        field.setAccessible(true);
        JPanel icons = (JPanel) field.get(panel);

        assertTrue(icons.getLayout() instanceof WrapLayout);
    }

    @Test
    public void crystalTreeUsesTheShardPileIconInBothSelectors() throws Exception {
        assertEquals(ItemID.PRIF_CRYSTAL_SHARD_25,
                patchIcon(CustomRunFilterBar.class, PatchTypes.CRYSTAL_TREE));
        assertEquals(ItemID.PRIF_CRYSTAL_SHARD_25,
                patchIcon(CustomRunLocationSubPanel.class, PatchTypes.CRYSTAL_TREE));
    }

    @Test
    public void locationPanelKeepsItsPreferredHeightWhenCollapsedAndExpanded() throws Exception {
        CustomRunLocationSubPanel location = new CustomRunLocationSubPanel(
                new TestPlugin(), null, "Farming Guild",
                new RunLocation("Farming Guild", "Spirit_Tree", Collections.emptyList()), null);
        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.add(location);

        Dimension preferred = location.getPreferredSize();
        list.setSize(240, 800);
        list.doLayout();

        assertEquals(preferred.height, location.getHeight());

        Method toggleExpanded = CustomRunLocationSubPanel.class.getDeclaredMethod("toggleExpanded");
        toggleExpanded.setAccessible(true);
        toggleExpanded.invoke(location);
        list.invalidate();
        list.doLayout();

        assertEquals(location.getPreferredSize().height, location.getHeight());
    }

    @Test
    public void farmingGuildKeepsFiveIconsPerRowWhenAnotherLocationExpands() throws Exception {
        CustomRunLocationSubPanel farmingGuild = locationPanel("Farming Guild", "Spirit_Tree");
        CustomRunLocationSubPanel fossilIsland = locationPanel("Fossil Island", "Digsite_pendant");
        CustomRunLocationSubPanel locusOasis = locationPanel("Locus Oasis", "Quetzal_Transport");
        CustomRunLocationSubPanel anglersRetreat = locationPanel("Anglers' Retreat", "Fishing_Trawler");
        expand(farmingGuild);

        JPanel locations = new JPanel();
        locations.setLayout(new BoxLayout(locations, BoxLayout.Y_AXIS));
        locations.setBorder(new EmptyBorder(0, 10, 10, 10));
        locations.add(farmingGuild);
        locations.add(Box.createRigidArea(new Dimension(0, 6)));
        locations.add(fossilIsland);
        locations.add(Box.createRigidArea(new Dimension(0, 6)));
        locations.add(locusOasis);
        locations.add(Box.createRigidArea(new Dimension(0, 6)));
        locations.add(anglersRetreat);

        JScrollPane scrollPane = new JScrollPane(locations);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        JPanel root = new JPanel(new BorderLayout());
        root.add(scrollPane, BorderLayout.CENTER);
        root.setSize(230, 600);
        root.addNotify();
        root.validate();

        layoutTree(root);
        int collapsedOtherLocationCount = firstRowIconCount(farmingGuild);
        int[] collapsedOtherLocationBounds = iconBounds(farmingGuild);

        expand(fossilIsland);
        layoutTree(root);
        int expandedOtherLocationCount = firstRowIconCount(farmingGuild);
        int[] expandedOtherLocationBounds = iconBounds(farmingGuild);

        String counts = "collapsed=" + collapsedOtherLocationCount + ", expanded=" + expandedOtherLocationCount;
        assertEquals(counts, 5, collapsedOtherLocationCount);
        assertEquals(counts, 5, expandedOtherLocationCount);
        assertArrayEquals(collapsedOtherLocationBounds, expandedOtherLocationBounds);
    }

    private static CustomRunLocationSubPanel locationPanel(String name, String teleport) {
        return new CustomRunLocationSubPanel(
                new TestPlugin(), null, name,
                new RunLocation(name, teleport, Collections.emptyList()), null);
    }

    private static void expand(CustomRunLocationSubPanel panel) throws Exception {
        Method toggleExpanded = CustomRunLocationSubPanel.class.getDeclaredMethod("toggleExpanded");
        toggleExpanded.setAccessible(true);
        toggleExpanded.invoke(panel);
    }

    private static int firstRowIconCount(CustomRunLocationSubPanel panel) throws Exception {
        Field field = CustomRunLocationSubPanel.class.getDeclaredField("patchIconsPanel");
        field.setAccessible(true);
        JPanel icons = (JPanel) field.get(panel);
        Component[] components = icons.getComponents();
        int firstRowY = components[0].getY();
        int count = 0;
        for (Component component : components) {
            if (component.getY() == firstRowY) {
                count++;
            }
        }
        return count;
    }

    private static int[] iconBounds(CustomRunLocationSubPanel panel) throws Exception {
        Field field = CustomRunLocationSubPanel.class.getDeclaredField("patchIconsPanel");
        field.setAccessible(true);
        JPanel icons = (JPanel) field.get(panel);
        Component[] components = icons.getComponents();
        int[] bounds = new int[components.length * 4];
        for (int i = 0; i < components.length; i++) {
            bounds[i * 4] = components[i].getX();
            bounds[i * 4 + 1] = components[i].getY();
            bounds[i * 4 + 2] = components[i].getWidth();
            bounds[i * 4 + 3] = components[i].getHeight();
        }
        return bounds;
    }

    private static void layoutTree(Container container) {
        for (int i = 0; i < 3; i++) {
            container.doLayout();
            for (Component component : container.getComponents()) {
                if (component instanceof Container) {
                    layoutTree((Container) component);
                }
            }
        }
    }

    private static int patchIcon(Class<?> owner, String patchType) throws Exception {
        Method method = owner.getDeclaredMethod("itemIdForPatchType", String.class);
        method.setAccessible(true);
        return (int) method.invoke(null, patchType);
    }

    private static class TestPlugin extends EasyFarmingPlugin {
        private final EasyFarmingConfig config = new EasyFarmingConfig() { };

        @Override
        public EasyFarmingConfig getConfig() {
            return config;
        }
    }
}
