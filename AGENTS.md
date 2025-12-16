# Repository Guidelines

## Project Structure & Module Organization

- Android multi-module Gradle project; root settings in `settings.gradle` and shared config in `build.gradle.kts`.
- Application modules: `app` (primary Android client) and `wear` (Wear OS companion).
- Core libraries live under `core/*` (data, graphing, utils, UI), with shared implementations in `shared/*`.
- Domain-specific extensions sit in `plugins/*` and `pump/*`; persistence and database code in `database/*`.
- Tests sit alongside code (`src/test` and `src/androidTest` per module) with shared test fixtures in `shared/tests`.

## Build, Test, and Development Commands

- `./gradlew assembleDebug` — build the main app APK; use `assembleRelease` only when signing is configured.
- `./gradlew lint` — Android lint across modules; fix warnings before submitting.
- `./gradlew ktlintCheck` / `./gradlew ktlintFormat` — verify or auto-format Kotlin style.
- `./gradlew testFullDebugUnitTest` — run unit tests for the aggregated debug variant (default in `runtests.sh`); add `-Pcoverage` to collect JaCoCo data.
- `./gradlew :app:connectedDebugAndroidTest` — run instrumented tests on a device/emulator when required.

## Coding Style & Naming Conventions

- Kotlin/Java: 4-space indentation, spaces over tabs; prefer Android Studio auto-format (`Ctrl+Alt+L`).
- Follow ktlint defaults (import order, line length, spacing); keep compiler warnings at zero.
- Classes/objects/interfaces use UpperCamelCase; methods and variables use lowerCamelCase; resource IDs use snake_case.
- Strings should live in `strings.xml` (`@string/…`), with English as the source; avoid hardcoded UI text.

## Testing Guidelines

- Use JUnit (and Robolectric where applicable) for unit tests; instrumented tests with Espresso in `androidTest`.
- Name tests with the subject under test (e.g., `FooRepositoryTest`) and use descriptive method names (`doesSomething_whenCondition`).
- Include coverage for new logic; prefer fast unit tests over slow integration runs when feasible.

## Commit & Pull Request Guidelines

- Write concise, imperative commit subjects (e.g., “Add pump driver guard,” “Fix basal schedule parsing”); keep related changes together.
- Branch from the latest `dev`, rebase before opening a PR, and avoid merge commits in feature branches.
- PRs should describe scope, testing performed (`./gradlew testFullDebugUnitTest`), linked issues, and screenshots for UI changes.
- Ensure lint/ktlint/test tasks pass locally; document any known gaps or follow-ups in the PR description.

## Security & Configuration Tips

- Do not commit secrets or signed `google-services.json`; keep local credentials outside VCS.
- For CI or local runs without Firebase, add `-PfirebaseDisable` to Gradle to skip Firebase-dependent tasks.
- Validate new dependencies for license compatibility and minimal footprint; prefer existing core or plugin modules before adding third-party code.

## AI Docs

The following documents provide detailed explanations of key components and features in the codebase.
Whenever you add a new AI Doc, please remember to update this list:

- ai-docs/smb-limitations.md — SMB “always”/“after carbs” limits enforced by advanced filtering support and how BG sources report it.
- ai-docs/version-expiration.md — Version expiration guard implementation and data map; how the app warns and clamps automation once a build is past its expiry.
