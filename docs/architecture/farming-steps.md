# Farming steps architecture

Shared patch-step pipeline for Easy Farming overlay instructions and highlights.

## Intended flow

```
RegionLocationRegistry
        ↓
PatchLocationResolver   (names, region from Client, herb varbit fallback)
        ↓
PatchStepContext        (one frame: graphics, teleport, region, colors, varbit, …)
        ↓
PatchStep               (process(context) — herb/hops/… step classes)
        ↓
highlighters + info box text
```

`FarmingStepHandler` remains the **facade**: overlay code continues to call
`herbSteps` / `hopsSteps` / etc. Each method delegates to a `*PatchStep.run(...)`.

`PatchFactory` (with `PatchStepBundle` / `PatchStepHost`) builds the six step
instances and Progress/Support adapters in the handler constructor so wiring stays
out of the facade.

Progress flags (done / composted / allotment index) live in
`CustomRunPatchProgress`. `FarmingTeleportOverlay` resets via
`farmingStepHandler.resetForNextLocation()` and done accessors — not public fields.

## Step classes

| Class | Patch type | Entry |
|-------|------------|--------|
| `HerbPatchStep` | Herb | `run(Graphics2D, Teleport)` |
| `HopsPatchStep` | Hops | `run(Graphics2D, Teleport)` |
| `FlowerPatchStep` | Flower | `run(Graphics2D, boolean farmLimps)` |
| `AllotmentPatchStep` | Allotment | `run(Graphics2D, Teleport)` |
| `TreePatchStep` | Tree | `run(Graphics2D, Teleport)` |
| `FruitTreePatchStep` | Fruit tree | `run(Graphics2D, Teleport)` |

Shared scaffolding:

- `PatchStepContext` — frame context
- `PatchStep` — step interface
- `PatchLocationResolver` — location / region helpers
- `PatchInstructionText` — shared instruction strings
- `CustomRunPatchProgress` — done/composted/allotment state
- `PatchFactory` / `PatchStepBundle` / `PatchStepHost` — step construction wiring

## Adding a new patch type

1. Add region→name mapping in `RegionLocationRegistry` (+ `Constants` if needed).
2. Add instruction strings to `PatchInstructionText`.
3. Create `FooPatchStep` implementing `PatchStep` with `Progress` / `StepSupport` interfaces.
4. Wire construction in `PatchFactory` (+ host methods if needed) and a one-line delegate in `FarmingStepHandler`.
5. Add progress fields/accessors on `CustomRunPatchProgress` if the overlay advances on “done”.
6. Add unit tests for pure mappings; manual smoke the new patch type.

## Notes

- Instruction wording should go through `PatchInstructionText` so types do not diverge.
- Optional later: `FarmingUiEffect` action list + custom-run state machine (P4/P5).
