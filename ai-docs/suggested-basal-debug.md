# Suggested basal debug output

- Display surface: appended to `RT.consoleError` so it shows in the APS tab “Script debug” list (`OpenAPSFragment`), and is included in persisted APS results.
- Entry text: `Suggested basal (debug): <rate> U/h (TDD=<tdd> U, basal now=<current> U/h, multiplier=<multiplier>)`.
- Inputs: `SuggestedBasalCalculator.calculateSuggestedBasalRate` receives `SuggestedBasalInput` with (a) TDD (prefers dynamic-ISF TDD; otherwise last 24h via `TddCalculator`), (b) active `Profile` (for current basal), and (c) user settings.
- User settings: toggle `BooleanKey.ApsSuggestedBasalEnabled` and multiplier `DoubleKey.ApsSuggestedBasalMultiplier`, exposed on SMB, AutoISF, and AMA preference screens.
- Current implementation returns a placeholder 1.0 U/h so downstream code has plumbing in place; replace the calculator logic when the real formula is available.
