# Location architecture

Canonical model for farming locations and teleports in Easy Farming.

## Pipeline

```
*LocationData / LocationData  →  LocationFactory  →  core.Location  →  LocationCatalog
```

| Layer | Package / type | Role |
|-------|----------------|------|
| Declarative data | `com.easyfarming.locations.*LocationData`, `LocationData`, `TeleportData` | Define site names, patch points, config selectors, and teleport options as data |
| Factory | `com.easyfarming.locations.LocationFactory` | Convert `LocationData` + config into runtime `core.Location` with `core.Teleport` options |
| Runtime model | `com.easyfarming.core.Location`, `com.easyfarming.core.Teleport` | Selected teleport, limp flag, and options used by overlay / step logic |
| Catalog | `com.easyfarming.customrun.LocationCatalog` | Aggregates all patch-type locations for custom runs (UI + overlay) |

## Ownership

- **Add or change a farming site** in `locations.*` (herb root package, or `locations/tree`, `locations/fruittree`, `locations/hops`).
- **Wire new sites into custom runs** via `LocationCatalog.rebuild()`.
- **Shared house / fairy-ring item lists** live in `com.easyfarming.utils.TeleportItemRequirements`.

## Notes

- `com.easyfarming.Location` (root package) was a dead duplicate and has been removed; always use `com.easyfarming.core.Location`.
- `LocationData`, `TeleportData`, and `LocationFactory` are the supported API — not deprecated leftovers.
