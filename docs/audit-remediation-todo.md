# Audit remediation TODO

Tracks OpenCode / Codex findings against current tree.

## Final-audit reconciliation

| Finding | Status |
|---------|--------|
| Strategy / isolated patch classes (`PatchStep`) | **Done** |
| PatchFactory for step construction | **Done** |
| Location model consolidate / undeprecate | **Done** |
| Guice instead of lazy null checks | **Done** |
| CustomRunPatchProgress (session state) | **Done** |
| Public done-flag mutation from overlay | **Done** |
| Region magic IDs → registry/Constants | **Done** |
| Farmer NPC name constants | **Done** |
| Hint-arrow empty catch → debug log | **Done** |
| PluginUiLifecycle (nav/panel/overlays) | **Done** |
| CurrentStepInstruction equals dedupe | **Done** (partial vs Enum IDs) |
| Swing EDT strict split | Partial — optional O2 |
| Manual smoke | **Open** (N5) |

---

## Hotfix (2026-10-13)

- [x] **Singleton split-brain**: `PluginUiLifecycle` + plugin each had a different `FarmingTeleportOverlay` (Guice unscoped). Runs appeared to start then instantly end / UI reset to Start. Fixed with `@Singleton` on overlays/handlers + panel always starts via `plugin.getFarmingTeleportOverlay()`.

## Remaining (optional)

- [ ] **N5** Manual smoke: herb + tree custom run, skip-step, compost chat, panel pencil
- [ ] **O1** Enum/stable key for step UI identity beyond `equals`
- [ ] **O2** Instruction resolve fully before EDT; `invokeLater` only applies `setText`
- [ ] **O3** `FarmingUiEffect` action list
- [ ] **O4** Custom-run state machine
- [ ] **O5** `NavigationHandler` decomposition
- [ ] **O6** `@Deprecated` sweep on `EasyFarmingOverlay` / `Constants`

---

## Completed waves

- [x] Waves 1–3: registry, Guice, location docs, PatchStep ×6, CustomRunPatchProgress, step equals
- [x] N1 PatchFactory + PatchStepBundle + PatchStepHost
- [x] N2 PluginUiLifecycle
- [x] N3 TREE/FRUIT_TREE_FARMER_NAMES + NPC_TOOL_LEPRECHAUN in Constants
- [x] N4 hint-arrow `log.debug` on catch
