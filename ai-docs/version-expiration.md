# Version expiration guard

- Purpose: encourage timely updates by warning when a newer build exists and clamping automation once the current build passes its expiry date.

## Key components
- `plugins/constraints/src/main/kotlin/app/aaps/plugins/constraints/versionChecker/VersionCheckerUtilsImpl.kt`: loads version metadata from `app/src/main/assets/definition.json`, compares `Config.VERSION_NAME` against the latest per API level, stores an expiration timestamp in `SP` (`app_expiration_<version>`) when a newer build is present (or dev build), and emits `NEW_VERSION_DETECTED` / `VERSION_EXPIRE` notifications using `UiInteraction`.
- `plugins/constraints/src/main/kotlin/app/aaps/plugins/constraints/versionChecker/VersionCheckerPlugin.kt`: always-enabled constraint plugin; `applyMaxIOBConstraints` runs `triggerCheckVersion()` and, once `now > expiration`, forces `maxIob` to `0.0` with the `application_expired` message—effectively LGS/no automatic insulin delivery.
- `plugins/constraints/src/main/kotlin/app/aaps/plugins/constraints/versionChecker/AllowedVersions.kt`: parses the JSON to retrieve per-API latest versions and per-version expiry dates (`yyyy-MM-dd` to epoch millis).
- `app/src/main/kotlin/app/aaps/MainApp.kt`: posts a delayed `versionCheckersUtils.triggerCheckVersion()` on app start (30s) so the expiration data/notifications seed immediately after launch.
- `app/src/main/assets/definition.json`: shipped metadata listing latest versions per Android API and `Expire dates` entries (e.g., `3.3.0.0` → date). `PluginsConstraintsModule` reads it via `SignatureVerifierPlugin.readInputStream(...)`.
- UI cues: `plugins/main/src/main/kotlin/app/aaps/plugins/main/general/overview/OverviewPlugin.kt` tints the version label with a warning color when an expiration record exists for the current build.

## Behavior flow
1) `triggerCheckVersion()` retrieves the allowed version for the device API; if newer than the current build (or dev build), it stores the parsed expiry (`expire date + 1 day`) in `SP` under `key_app_expiration_<version>`.
2) Before expiry, it issues low-priority `VERSION_EXPIRE` warnings on a cadence from `warnEvery(...)` (7/3/1 days as the date approaches).
3) After expiry, it raises an urgent `VERSION_EXPIRE` notification and `VersionCheckerPlugin` clamps `maxIob` to zero, disabling automated insulin delivery until the app is updated.

## Notes for changes
- Update `app/src/main/assets/definition.json` to adjust expiry dates or bump latest versions; keep the date format `yyyy-MM-dd`.
- SP keys involved: `key_app_expiration_<version>`, `key_last_versionchecker_warning`, `key_last_expired_versionchecker_warning`, `key_last_versionchecker_plugin_warning_timestamp`.
- Tests: `plugins/constraints/src/test/kotlin/app/aaps/plugins/constraints/signatureVerifier/VersionCheckerUtilsKtTest.kt` covers version comparison logic.
