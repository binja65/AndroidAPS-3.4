# SMB advanced filtering limitation

- SMB “always” and “after carbs” are only permitted when the active BG source declares `advancedFilteringSupported()` to avoid aggressive dosing on noisy/unfiltered CGM data.

## Enforcement
- `core/interfaces/src/main/kotlin/app/aaps/core/interfaces/source/BgSource.kt` defines `advancedFilteringSupported()` (default `false`).
- `plugins/constraints/src/main/kotlin/app/aaps/plugins/constraints/safety/SafetyPlugin.kt` sets the `smbalwaysdisabled` constraint reason when the BG source reports `false`; `ConstraintsChecker.isAdvancedFilteringEnabled()` surfaces this to APS code.
- `plugins/aps/src/main/kotlin/app/aaps/plugins/aps/openAPSSMB/OpenAPSSMBPlugin.kt` and `plugins/aps/src/main/kotlin/app/aaps/plugins/aps/openAPSAutoISF/OpenAPSAutoISFPlugin.kt` only pass `enableSMB_always` and `enableSMB_after_carbs` to the OpenAPS determine-basal call when `advancedFiltering` is `true`; their preference screens hide these toggles when unsupported.
- User-facing wording lives in `plugins/constraints/src/main/res/values/strings.xml` (`smbalwaysdisabled`) and the SMB preference summaries in `plugins/aps/src/main/res/values/strings.xml`.

## BG sources reporting advanced filtering
- `plugins/source/src/main/kotlin/app/aaps/plugins/source/DexcomPlugin.kt`: always `true` (Dexcom patched app G5/G6/G7 native stream).
- `plugins/source/src/main/kotlin/app/aaps/plugins/source/XdripSourcePlugin.kt`: detects per reading; `true` for `SourceSensor` values Dexcom native/xDrip (G5/G6/G7) and Libre 2 / Libre 2 Native / Libre 3, otherwise `false`.
- `plugins/source/src/main/kotlin/app/aaps/plugins/source/NSClientSourcePlugin.kt`: `true` only when incoming data is tagged as Dexcom native or xDrip Dexcom (G5/G6/G7).
- `plugins/source/src/main/kotlin/app/aaps/plugins/source/RandomBgPlugin.kt`: always `true` (test/virtual source).
- All other BG sources (e.g., `GlimpPlugin`, `MM640gPlugin`, `TomatoPlugin`, `OttaiPlugin`, `PoctechPlugin`, `GlunovoPlugin`, `IntelligoPlugin`, `SyaiTagPlugin`) inherit the default `false`, so SMB Always and SMB-after-carbs stay disabled.

## Resulting behavior
- When advanced filtering is unavailable, SMB Always and SMB-after-carbs switches are hidden or forced off, and constraint reasons include “SMB always and after carbs disabled because active BG source doesn’t support advanced filtering.”
- Switching to a supported source re-enables the options after the next BG sample sets `advancedFilteringSupported()` (important for xDrip/NSClient, where the flag depends on the latest `SourceSensor`).
