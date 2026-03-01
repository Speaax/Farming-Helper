package com.easyfarming.customrun;

import com.easyfarming.EasyFarmingConfig;
import com.easyfarming.EasyFarmingPlugin;
import com.easyfarming.ItemRequirement;
import com.easyfarming.core.Location;
import com.easyfarming.core.Teleport;
import com.easyfarming.locations.ArdougneLocationData;
import com.easyfarming.locations.CatherbyLocationData;
import com.easyfarming.locations.CivitasLocationData;
import com.easyfarming.locations.FaladorLocationData;
import com.easyfarming.locations.FarmingGuildLocationData;
import com.easyfarming.locations.HarmonyLocationData;
import com.easyfarming.locations.KourendLocationData;
import com.easyfarming.locations.MorytaniaLocationData;
import com.easyfarming.locations.TrollStrongholdLocationData;
import com.easyfarming.locations.WeissLocationData;
import com.easyfarming.locations.fruittree.BrimhavenFruitTreeLocationData;
import com.easyfarming.locations.fruittree.CatherbyFruitTreeLocationData;
import com.easyfarming.locations.fruittree.FarmingGuildFruitTreeLocationData;
import com.easyfarming.locations.fruittree.GnomeStrongholdFruitTreeLocationData;
import com.easyfarming.locations.fruittree.LletyaFruitTreeLocationData;
import com.easyfarming.locations.fruittree.TreeGnomeVillageFruitTreeLocationData;
import com.easyfarming.locations.hops.AldarinHopsLocationData;
import com.easyfarming.locations.hops.EntranaHopsLocationData;
import com.easyfarming.locations.hops.LumbridgeHopsLocationData;
import com.easyfarming.locations.hops.SeersVillageHopsLocationData;
import com.easyfarming.locations.hops.YanilleHopsLocationData;
import com.easyfarming.locations.tree.FaladorTreeLocationData;
import com.easyfarming.locations.tree.FarmingGuildTreeLocationData;
import com.easyfarming.locations.tree.GnomeStrongholdTreeLocationData;
import com.easyfarming.locations.tree.LumbridgeTreeLocationData;
import com.easyfarming.locations.tree.TaverleyTreeLocationData;
import com.easyfarming.locations.tree.VarrockTreeLocationData;
import com.easyfarming.utils.TeleportItemRequirements;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;

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

    /** Rebuild catalog from LocationData classes and config. */
    public void rebuild() {
        locationByPatch.clear();
        teleportOptionsByLocation.clear();
        patchTypesByLocation.clear();
        allLocationNames.clear();

        Set<String> seenNames = new LinkedHashSet<>();
        EasyFarmingConfig config = plugin.getConfig();
        Supplier<List<ItemRequirement>> houseTeleportSupplier = () -> TeleportItemRequirements.getHouseTeleportItemRequirements(config);
        Supplier<List<ItemRequirement>> fairyRingSupplier = () -> TeleportItemRequirements.getFairyRingItemRequirements(config, plugin.getClient());

        // Herb locations (order matches HerbRunItemAndLocation.setupLocations: Farming Guild, Ardougne, Catherby, Falador, Harmony, Kourend, Morytania, Troll Stronghold, Weiss, Civitas)
        Location farmingGuild = FarmingGuildLocationData.create(config, houseTeleportSupplier, fairyRingSupplier);
        seenNames.add(farmingGuild.getName());
        putLocationForPatch(farmingGuild.getName(), PatchTypes.HERB, farmingGuild);
        putLocationForPatch(farmingGuild.getName(), PatchTypes.FLOWER, farmingGuild);
        putLocationForPatch(farmingGuild.getName(), PatchTypes.ALLOTMENT, farmingGuild);
        addTeleports(farmingGuild.getName(), farmingGuild);
        addPatchTypes(farmingGuild.getName(), Arrays.asList(PatchTypes.HERB, PatchTypes.FLOWER, PatchTypes.ALLOTMENT));

        Location ardougne = ArdougneLocationData.create(config, houseTeleportSupplier);
        seenNames.add(ardougne.getName());
        putLocationForPatch(ardougne.getName(), PatchTypes.HERB, ardougne);
        putLocationForPatch(ardougne.getName(), PatchTypes.FLOWER, ardougne);
        putLocationForPatch(ardougne.getName(), PatchTypes.ALLOTMENT, ardougne);
        addTeleports(ardougne.getName(), ardougne);
        addPatchTypes(ardougne.getName(), Arrays.asList(PatchTypes.HERB, PatchTypes.FLOWER, PatchTypes.ALLOTMENT));

        Location catherby = CatherbyLocationData.create(config, houseTeleportSupplier);
        seenNames.add(catherby.getName());
        putLocationForPatch(catherby.getName(), PatchTypes.HERB, catherby);
        putLocationForPatch(catherby.getName(), PatchTypes.FLOWER, catherby);
        putLocationForPatch(catherby.getName(), PatchTypes.ALLOTMENT, catherby);
        addTeleports(catherby.getName(), catherby);
        addPatchTypes(catherby.getName(), Arrays.asList(PatchTypes.HERB, PatchTypes.FLOWER, PatchTypes.ALLOTMENT));

        Location falador = FaladorLocationData.create(config, houseTeleportSupplier);
        seenNames.add(falador.getName());
        putLocationForPatch(falador.getName(), PatchTypes.HERB, falador);
        putLocationForPatch(falador.getName(), PatchTypes.FLOWER, falador);
        putLocationForPatch(falador.getName(), PatchTypes.ALLOTMENT, falador);
        addTeleports(falador.getName(), falador);
        addPatchTypes(falador.getName(), Arrays.asList(PatchTypes.HERB, PatchTypes.FLOWER, PatchTypes.ALLOTMENT));

        Location harmony = HarmonyLocationData.create(config, houseTeleportSupplier);
        seenNames.add(harmony.getName());
        putLocationForPatch(harmony.getName(), PatchTypes.HERB, harmony);
        putLocationForPatch(harmony.getName(), PatchTypes.FLOWER, harmony);
        putLocationForPatch(harmony.getName(), PatchTypes.ALLOTMENT, harmony);
        addTeleports(harmony.getName(), harmony);
        addPatchTypes(harmony.getName(), Arrays.asList(PatchTypes.HERB, PatchTypes.FLOWER, PatchTypes.ALLOTMENT));

        Location kourend = KourendLocationData.create(config, houseTeleportSupplier);
        seenNames.add(kourend.getName());
        putLocationForPatch(kourend.getName(), PatchTypes.HERB, kourend);
        putLocationForPatch(kourend.getName(), PatchTypes.FLOWER, kourend);
        putLocationForPatch(kourend.getName(), PatchTypes.ALLOTMENT, kourend);
        addTeleports(kourend.getName(), kourend);
        addPatchTypes(kourend.getName(), Arrays.asList(PatchTypes.HERB, PatchTypes.FLOWER, PatchTypes.ALLOTMENT));

        Location morytania = MorytaniaLocationData.create(config, houseTeleportSupplier, fairyRingSupplier);
        seenNames.add(morytania.getName());
        putLocationForPatch(morytania.getName(), PatchTypes.HERB, morytania);
        putLocationForPatch(morytania.getName(), PatchTypes.FLOWER, morytania);
        putLocationForPatch(morytania.getName(), PatchTypes.ALLOTMENT, morytania);
        addTeleports(morytania.getName(), morytania);
        addPatchTypes(morytania.getName(), Arrays.asList(PatchTypes.HERB, PatchTypes.FLOWER, PatchTypes.ALLOTMENT));

        Location trollStronghold = TrollStrongholdLocationData.create(config, houseTeleportSupplier);
        seenNames.add(trollStronghold.getName());
        putLocationForPatch(trollStronghold.getName(), PatchTypes.HERB, trollStronghold);
        putLocationForPatch(trollStronghold.getName(), PatchTypes.FLOWER, trollStronghold);
        putLocationForPatch(trollStronghold.getName(), PatchTypes.ALLOTMENT, trollStronghold);
        addTeleports(trollStronghold.getName(), trollStronghold);
        addPatchTypes(trollStronghold.getName(), Arrays.asList(PatchTypes.HERB, PatchTypes.FLOWER, PatchTypes.ALLOTMENT));

        Location weiss = WeissLocationData.create(config, houseTeleportSupplier);
        seenNames.add(weiss.getName());
        putLocationForPatch(weiss.getName(), PatchTypes.HERB, weiss);
        putLocationForPatch(weiss.getName(), PatchTypes.FLOWER, weiss);
        putLocationForPatch(weiss.getName(), PatchTypes.ALLOTMENT, weiss);
        addTeleports(weiss.getName(), weiss);
        addPatchTypes(weiss.getName(), Arrays.asList(PatchTypes.HERB, PatchTypes.FLOWER, PatchTypes.ALLOTMENT));

        Location civitas = CivitasLocationData.create(config, houseTeleportSupplier);
        seenNames.add(civitas.getName());
        putLocationForPatch(civitas.getName(), PatchTypes.HERB, civitas);
        putLocationForPatch(civitas.getName(), PatchTypes.FLOWER, civitas);
        putLocationForPatch(civitas.getName(), PatchTypes.ALLOTMENT, civitas);
        addTeleports(civitas.getName(), civitas);
        addPatchTypes(civitas.getName(), Arrays.asList(PatchTypes.HERB, PatchTypes.FLOWER, PatchTypes.ALLOTMENT));

        // Tree locations (order matches TreeRunItemAndLocation.setupLocations: Farming Guild, Falador, Taverley, Lumbridge, Varrock, Gnome Stronghold)
        Location farmingGuildTree = FarmingGuildTreeLocationData.create(config, houseTeleportSupplier, fairyRingSupplier);
        seenNames.add(farmingGuildTree.getName());
        putLocationForPatch(farmingGuildTree.getName(), PatchTypes.TREE, farmingGuildTree);
        addTeleports(farmingGuildTree.getName(), farmingGuildTree);
        addPatchTypes(farmingGuildTree.getName(), singletonList(PatchTypes.TREE));

        Location faladorTree = FaladorTreeLocationData.create(config, houseTeleportSupplier);
        seenNames.add(faladorTree.getName());
        putLocationForPatch(faladorTree.getName(), PatchTypes.TREE, faladorTree);
        addTeleports(faladorTree.getName(), faladorTree);
        addPatchTypes(faladorTree.getName(), singletonList(PatchTypes.TREE));

        Location taverleyTree = TaverleyTreeLocationData.create(config, houseTeleportSupplier);
        seenNames.add(taverleyTree.getName());
        putLocationForPatch(taverleyTree.getName(), PatchTypes.TREE, taverleyTree);
        addTeleports(taverleyTree.getName(), taverleyTree);
        addPatchTypes(taverleyTree.getName(), singletonList(PatchTypes.TREE));

        Location lumbridgeTree = LumbridgeTreeLocationData.create(config, houseTeleportSupplier);
        seenNames.add(lumbridgeTree.getName());
        putLocationForPatch(lumbridgeTree.getName(), PatchTypes.TREE, lumbridgeTree);
        addTeleports(lumbridgeTree.getName(), lumbridgeTree);
        addPatchTypes(lumbridgeTree.getName(), singletonList(PatchTypes.TREE));

        Location varrockTree = VarrockTreeLocationData.create(config, houseTeleportSupplier);
        seenNames.add(varrockTree.getName());
        putLocationForPatch(varrockTree.getName(), PatchTypes.TREE, varrockTree);
        addTeleports(varrockTree.getName(), varrockTree);
        addPatchTypes(varrockTree.getName(), singletonList(PatchTypes.TREE));

        Location gnomeStrongholdTree = GnomeStrongholdTreeLocationData.create(config);
        seenNames.add(gnomeStrongholdTree.getName());
        putLocationForPatch(gnomeStrongholdTree.getName(), PatchTypes.TREE, gnomeStrongholdTree);
        addTeleports(gnomeStrongholdTree.getName(), gnomeStrongholdTree);
        addPatchTypes(gnomeStrongholdTree.getName(), singletonList(PatchTypes.TREE));

        // Fruit tree locations (order matches FruitTreeRunItemAndLocation.setupLocations: Farming Guild, Brimhaven, Catherby, Lletya, Gnome Stronghold, Tree Gnome Village)
        Location farmingGuildFruitTree = FarmingGuildFruitTreeLocationData.create(config, houseTeleportSupplier, fairyRingSupplier);
        seenNames.add(farmingGuildFruitTree.getName());
        putLocationForPatch(farmingGuildFruitTree.getName(), PatchTypes.FRUIT_TREE, farmingGuildFruitTree);
        addTeleports(farmingGuildFruitTree.getName(), farmingGuildFruitTree);
        addPatchTypes(farmingGuildFruitTree.getName(), singletonList(PatchTypes.FRUIT_TREE));

        Location brimhavenFruitTree = BrimhavenFruitTreeLocationData.create(config, houseTeleportSupplier);
        seenNames.add(brimhavenFruitTree.getName());
        putLocationForPatch(brimhavenFruitTree.getName(), PatchTypes.FRUIT_TREE, brimhavenFruitTree);
        addTeleports(brimhavenFruitTree.getName(), brimhavenFruitTree);
        addPatchTypes(brimhavenFruitTree.getName(), singletonList(PatchTypes.FRUIT_TREE));

        Location catherbyFruitTree = CatherbyFruitTreeLocationData.create(config, houseTeleportSupplier);
        seenNames.add(catherbyFruitTree.getName());
        putLocationForPatch(catherbyFruitTree.getName(), PatchTypes.FRUIT_TREE, catherbyFruitTree);
        addTeleports(catherbyFruitTree.getName(), catherbyFruitTree);
        addPatchTypes(catherbyFruitTree.getName(), singletonList(PatchTypes.FRUIT_TREE));

        Location lletyaFruitTree = LletyaFruitTreeLocationData.create(config);
        seenNames.add(lletyaFruitTree.getName());
        putLocationForPatch(lletyaFruitTree.getName(), PatchTypes.FRUIT_TREE, lletyaFruitTree);
        addTeleports(lletyaFruitTree.getName(), lletyaFruitTree);
        addPatchTypes(lletyaFruitTree.getName(), singletonList(PatchTypes.FRUIT_TREE));

        Location gnomeStrongholdFruitTree = GnomeStrongholdFruitTreeLocationData.create(config);
        seenNames.add(gnomeStrongholdFruitTree.getName());
        putLocationForPatch(gnomeStrongholdFruitTree.getName(), PatchTypes.FRUIT_TREE, gnomeStrongholdFruitTree);
        addTeleports(gnomeStrongholdFruitTree.getName(), gnomeStrongholdFruitTree);
        addPatchTypes(gnomeStrongholdFruitTree.getName(), singletonList(PatchTypes.FRUIT_TREE));

        Location treeGnomeVillageFruitTree = TreeGnomeVillageFruitTreeLocationData.create(config);
        seenNames.add(treeGnomeVillageFruitTree.getName());
        putLocationForPatch(treeGnomeVillageFruitTree.getName(), PatchTypes.FRUIT_TREE, treeGnomeVillageFruitTree);
        addTeleports(treeGnomeVillageFruitTree.getName(), treeGnomeVillageFruitTree);
        addPatchTypes(treeGnomeVillageFruitTree.getName(), singletonList(PatchTypes.FRUIT_TREE));

        // Hops locations (order matches HopsRunItemAndLocation.setupLocations: Lumbridge, Seers Village, Yanille, Entrana, Aldarin)
        Location lumbridgeHops = LumbridgeHopsLocationData.create(config, houseTeleportSupplier);
        seenNames.add(lumbridgeHops.getName());
        putLocationForPatch(lumbridgeHops.getName(), PatchTypes.HOPS, lumbridgeHops);
        addTeleports(lumbridgeHops.getName(), lumbridgeHops);
        addPatchTypes(lumbridgeHops.getName(), singletonList(PatchTypes.HOPS));

        Location seersVillageHops = SeersVillageHopsLocationData.create(config, houseTeleportSupplier, fairyRingSupplier);
        seenNames.add(seersVillageHops.getName());
        putLocationForPatch(seersVillageHops.getName(), PatchTypes.HOPS, seersVillageHops);
        addTeleports(seersVillageHops.getName(), seersVillageHops);
        addPatchTypes(seersVillageHops.getName(), singletonList(PatchTypes.HOPS));

        Location yanilleHops = YanilleHopsLocationData.create(config, houseTeleportSupplier);
        seenNames.add(yanilleHops.getName());
        putLocationForPatch(yanilleHops.getName(), PatchTypes.HOPS, yanilleHops);
        addTeleports(yanilleHops.getName(), yanilleHops);
        addPatchTypes(yanilleHops.getName(), singletonList(PatchTypes.HOPS));

        Location entranaHops = EntranaHopsLocationData.create(config);
        seenNames.add(entranaHops.getName());
        putLocationForPatch(entranaHops.getName(), PatchTypes.HOPS, entranaHops);
        addTeleports(entranaHops.getName(), entranaHops);
        addPatchTypes(entranaHops.getName(), singletonList(PatchTypes.HOPS));

        Location aldarinHops = AldarinHopsLocationData.create(config, houseTeleportSupplier, fairyRingSupplier);
        seenNames.add(aldarinHops.getName());
        putLocationForPatch(aldarinHops.getName(), PatchTypes.HOPS, aldarinHops);
        addTeleports(aldarinHops.getName(), aldarinHops);
        addPatchTypes(aldarinHops.getName(), singletonList(PatchTypes.HOPS));

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

    /**
     * Returns the config default teleport enum option for a location when creating a new custom run.
     * Uses the config default from each patch type at this location; for a single teleport per location
     * we use the first patch type's config default (herb/allotment before tree/fruit tree/hops).
     * If the config default is not in the allowed teleport list, falls back to the first allowed option.
     */
    public String getDefaultTeleportOptionForNewRun(String locationName) {
        List<String> patchTypes = getPatchTypesAtLocation(locationName);
        if (patchTypes.isEmpty()) return null;
        Location location = getLocationForPatch(locationName, patchTypes.get(0));
        if (location == null) return null;
        Teleport selected = location.getSelectedTeleport();
        String configDefault = (selected != null && selected.getEnumOption() != null) ? selected.getEnumOption() : null;
        List<String> allowed = getTeleportOptionsForLocation(locationName);
        if (configDefault != null && allowed.stream().anyMatch(opt -> configDefault.equalsIgnoreCase(opt))) {
            return configDefault;
        }
        return allowed.isEmpty() ? null : allowed.get(0);
    }

    public List<String> getAllLocationNames() {
        return new ArrayList<>(allLocationNames);
    }
}
