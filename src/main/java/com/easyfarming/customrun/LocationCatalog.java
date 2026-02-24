package com.easyfarming.customrun;

import com.easyfarming.EasyFarmingPlugin;
import com.easyfarming.core.Location;
import com.easyfarming.core.Teleport;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Catalog of all locations with their teleport options and patch types,
 * built from the plugin's herb/tree/fruit tree/hops runs. Used for custom run UI and overlay.
 */
public class LocationCatalog {
    private final EasyFarmingPlugin plugin;

    /** (locationName, patchType) -> Location from the appropriate run. */
    private final Map<String, Map<String, Location>> locationByPatch = new HashMap<>();
    /** locationName -> merged teleport enum option strings. */
    private final Map<String, List<String>> teleportOptionsByLocation = new HashMap<>();
    /** locationName -> patch types available at that location. */
    private final Map<String, List<String>> patchTypesByLocation = new HashMap<>();
    /** All unique location names in stable order. */
    private final List<String> allLocationNames = new ArrayList<>();

    public LocationCatalog(EasyFarmingPlugin plugin) {
        this.plugin = plugin;
        rebuild();
    }

    /** Rebuild catalog from plugin (call after plugin's ItemAndLocation are ready). */
    public void rebuild() {
        locationByPatch.clear();
        teleportOptionsByLocation.clear();
        patchTypesByLocation.clear();
        allLocationNames.clear();

        Set<String> seenNames = new LinkedHashSet<>();

        if (plugin.getHerbRunItemAndLocation() != null) {
            plugin.getHerbRunItemAndLocation().setupLocations();
            for (Location loc : plugin.getHerbRunItemAndLocation().locations) {
                String name = loc.getName();
                seenNames.add(name);
                putLocationForPatch(name, PatchTypes.HERB, loc);
                putLocationForPatch(name, PatchTypes.FLOWER, loc);
                putLocationForPatch(name, PatchTypes.ALLOTMENT, loc);
                addTeleports(name, loc);
                addPatchTypes(name, Arrays.asList(PatchTypes.HERB, PatchTypes.FLOWER, PatchTypes.ALLOTMENT));
            }
        }

        if (plugin.getTreeRunItemAndLocation() != null) {
            plugin.getTreeRunItemAndLocation().setupLocations();
            for (Location loc : plugin.getTreeRunItemAndLocation().locations) {
                String name = loc.getName();
                seenNames.add(name);
                putLocationForPatch(name, PatchTypes.TREE, loc);
                addTeleports(name, loc);
                addPatchTypes(name, Collections.singletonList(PatchTypes.TREE));
            }
        }

        if (plugin.getFruitTreeRunItemAndLocation() != null) {
            plugin.getFruitTreeRunItemAndLocation().setupLocations();
            for (Location loc : plugin.getFruitTreeRunItemAndLocation().locations) {
                String name = loc.getName();
                seenNames.add(name);
                putLocationForPatch(name, PatchTypes.FRUIT_TREE, loc);
                addTeleports(name, loc);
                addPatchTypes(name, Collections.singletonList(PatchTypes.FRUIT_TREE));
            }
        }

        if (plugin.getHopsRunItemAndLocation() != null) {
            plugin.getHopsRunItemAndLocation().setupLocations();
            for (Location loc : plugin.getHopsRunItemAndLocation().locations) {
                String name = loc.getName();
                seenNames.add(name);
                putLocationForPatch(name, PatchTypes.HOPS, loc);
                addTeleports(name, loc);
                addPatchTypes(name, Collections.singletonList(PatchTypes.HOPS));
            }
        }

        allLocationNames.addAll(seenNames);
    }

    private void putLocationForPatch(String locationName, String patchType, Location loc) {
        locationByPatch.computeIfAbsent(locationName, k -> new HashMap<>()).put(patchType, loc);
    }

    private void addTeleports(String locationName, Location loc) {
        List<String> opts = loc.getTeleportOptions() == null
                ? Collections.emptyList()
                : loc.getTeleportOptions().stream()
                        .map(Teleport::getEnumOption)
                        .collect(Collectors.toList());
        teleportOptionsByLocation
                .computeIfAbsent(locationName, k -> new ArrayList<>())
                .addAll(opts);
    }

    private void addPatchTypes(String locationName, List<String> types) {
        List<String> existing = patchTypesByLocation.computeIfAbsent(locationName, k -> new ArrayList<>());
        for (String t : types) {
            if (!existing.contains(t)) {
                existing.add(t);
            }
        }
    }

    public Location getLocationForPatch(String locationName, String patchType) {
        Map<String, Location> byType = locationByPatch.get(locationName);
        return byType != null ? byType.get(patchType) : null;
    }

    /** Returns merged teleport enum option strings for the location (may contain duplicates; caller can dedupe). */
    public List<String> getTeleportOptionsForLocation(String locationName) {
        List<String> opts = teleportOptionsByLocation.get(locationName);
        if (opts == null) return Collections.emptyList();
        return new ArrayList<>(new LinkedHashSet<>(opts));
    }

    public List<String> getPatchTypesAtLocation(String locationName) {
        List<String> types = patchTypesByLocation.get(locationName);
        return types != null ? new ArrayList<>(types) : Collections.emptyList();
    }

    public List<String> getAllLocationNames() {
        return new ArrayList<>(allLocationNames);
    }
}
