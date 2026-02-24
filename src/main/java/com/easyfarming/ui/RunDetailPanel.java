package com.easyfarming.ui;

import com.easyfarming.EasyFarmingPlugin;
import com.easyfarming.EasyFarmingPanel;
import com.easyfarming.StartStopJButton;
import com.easyfarming.core.Location;
import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Run detail view in the same style as before: back, title, Start, tools row, then location rows.
 * Uses master's run types and Location list from plugin.
 */
public class RunDetailPanel extends JPanel {
    private static final String HERB_RUN = "Herb Run";
    private static final String TREE_RUN = "Tree Run";
    private static final String FRUIT_TREE_RUN = "Fruit Tree Run";
    private static final String HOPS_RUN = "Hops Run";

    private final EasyFarmingPlugin plugin;
    private final EasyFarmingPanel parentPanel;
    private final String runName;
    private final net.runelite.client.game.ItemManager itemManager;

    public RunDetailPanel(EasyFarmingPlugin plugin, EasyFarmingPanel parentPanel, String runName, net.runelite.client.game.ItemManager itemManager) {
        this.plugin = plugin;
        this.parentPanel = parentPanel;
        this.runName = runName;
        this.itemManager = itemManager;

        setLayout(new BorderLayout());
        setBackground(ColorScheme.DARK_GRAY_COLOR);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(ColorScheme.DARK_GRAY_COLOR);
        headerPanel.setBorder(new EmptyBorder(5, 5, 10, 5));

        JButton backButton = new JButton("<");
        backButton.setPreferredSize(new Dimension(40, 30));
        backButton.setFocusable(false);
        backButton.setToolTipText("Back to Overview");
        backButton.addActionListener(e -> parentPanel.showOverview());
        headerPanel.add(backButton, BorderLayout.WEST);

        JLabel titleLabel = new JLabel(runName);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(net.runelite.client.ui.FontManager.getRunescapeBoldFont());
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        StartStopJButton startButton = new StartStopJButton(runName);
        startButton.setPreferredSize(new Dimension(80, 25));
        startButton.addActionListener(e -> {
            boolean toggleToStop = startButton.getText().equals("Start");
            startButton.setStartStopState(toggleToStop);
            if (toggleToStop) {
                parentPanel.startRun(runName);
            } else {
                plugin.getFarmingTeleportOverlay().removeOverlay();
                plugin.setOverlayActive(false);
            }
        });
        headerPanel.add(startButton, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        JPanel scrollContent = new JPanel();
        scrollContent.setLayout(new BoxLayout(scrollContent, BoxLayout.Y_AXIS));
        scrollContent.setBackground(ColorScheme.DARK_GRAY_COLOR);
        scrollContent.setBorder(new EmptyBorder(0, 10, 10, 10));

        JPanel toolsRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        toolsRow.setBackground(ColorScheme.DARK_GRAY_COLOR);
        toolsRow.setBorder(new EmptyBorder(0, 0, 4, 0));
        toolsRow.add(makeToolButton(net.runelite.api.gameval.ItemID.SPADE, "Spade", true, false, null));
        toolsRow.add(makeToolButton(net.runelite.api.gameval.ItemID.FAIRY_ENCHANTED_SECATEURS, "Secateurs", true, false, null));
        toolsRow.add(makeToolButton(net.runelite.api.gameval.ItemID.DIBBER, "Dibber", true, false, null));
        toolsRow.add(makeToolButton(net.runelite.api.gameval.ItemID.RAKE, "Rake", true, false, null));
        scrollContent.add(toolsRow);

        JPanel separator = new JPanel();
        separator.setBackground(new Color(60, 60, 60));
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        separator.setPreferredSize(new Dimension(0, 1));
        scrollContent.add(separator);
        scrollContent.add(Box.createRigidArea(new Dimension(0, 6)));

        List<Location> locations = getLocationsForRunType(plugin, runName);
        for (Location loc : locations) {
            LocationRowPanel row = new LocationRowPanel(loc);
            row.setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(0, 0, 5, 0), BorderFactory.createMatteBorder(1, 1, 1, 1, ColorScheme.DARK_GRAY_COLOR)));
            scrollContent.add(row);
        }

        JScrollPane scrollPane = new JScrollPane(scrollContent);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
        scrollPane.getViewport().setBackground(ColorScheme.DARK_GRAY_COLOR);
        add(scrollPane, BorderLayout.CENTER);
    }

    private static List<Location> getLocationsForRunType(EasyFarmingPlugin plugin, String runName) {
        List<Location> out = new ArrayList<>();
        switch (runName) {
            case HERB_RUN:
                out.add(plugin.getArdougneLocation());
                out.add(plugin.getCatherbyLocation());
                out.add(plugin.getFaladorLocation());
                out.add(plugin.getFarmingGuildLocation());
                out.add(plugin.getHarmonyLocation());
                out.add(plugin.getKourendLocation());
                out.add(plugin.getMorytaniaLocation());
                out.add(plugin.getTrollStrongholdLocation());
                out.add(plugin.getWeissLocation());
                out.add(plugin.getCivitasLocation());
                break;
            case TREE_RUN:
                out.add(plugin.getFaladorTreeLocation());
                out.add(plugin.getFarmingGuildTreeLocation());
                out.add(plugin.getGnomeStrongholdTreeLocation());
                out.add(plugin.getLumbridgeTreeLocation());
                out.add(plugin.getTaverleyTreeLocation());
                out.add(plugin.getVarrockTreeLocation());
                break;
            case FRUIT_TREE_RUN:
                out.add(plugin.getBrimhavenFruitTreeLocation());
                out.add(plugin.getCatherbyFruitTreeLocation());
                out.add(plugin.getFarmingGuildFruitTreeLocation());
                out.add(plugin.getGnomeStrongholdFruitTreeLocation());
                out.add(plugin.getLletyaFruitTreeLocation());
                out.add(plugin.getTreeGnomeVillageTreeLocation());
                break;
            case HOPS_RUN:
                out.add(plugin.getLumbridgeHopsLocation());
                out.add(plugin.getSeersVillageHopsLocation());
                out.add(plugin.getYanilleHopsLocation());
                out.add(plugin.getEntranaHopsLocation());
                out.add(plugin.getAldarinHopsLocation());
                break;
            default:
                break;
        }
        return out;
    }

    private JButton makeToolButton(int itemId, String tooltip, boolean active, boolean toggleable, java.util.function.Consumer<Boolean> onToggle) {
        final boolean[] state = {active};
        JButton btn = new JButton();
        btn.setPreferredSize(new Dimension(36, 36));
        btn.setFocusable(false);
        btn.setToolTipText(tooltip);
        btn.setBackground(state[0] ? new Color(30, 60, 30) : ColorScheme.DARKER_GRAY_COLOR);
        btn.setForeground(Color.WHITE);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        if (itemManager != null) {
            itemManager.getImage(itemId).addTo(btn);
        } else {
            btn.setText(tooltip);
        }
        if (toggleable && onToggle != null) {
            btn.addActionListener(e -> {
                state[0] = !state[0];
                btn.setBackground(state[0] ? new Color(30, 60, 30) : ColorScheme.DARKER_GRAY_COLOR);
                onToggle.accept(state[0]);
            });
        }
        return btn;
    }
}
