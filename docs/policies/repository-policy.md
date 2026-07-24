# Repository Policy

## Runtime Policy
- Java 20 is the baseline for Rising World Unity plugin repositories.
- Do not lower Maven compiler source, target, or release settings below 20.
- Runtime/tooling changes must be reflected in `README.md`, `HISTORY.md`, CI, and packaging files when affected.

## Plugin Entry-Point Policy
- The class declared as `main` in `plugin.yml` is the sole Rising World
  `net.risingworld.api.events.Listener` and the sole registration target for
  `registerEventListener(...)`.
- Keep it minimal: lifecycle composition, listener registration, and thin
  dispatch only.
- Place feature logic in focused thematic classes and subpackages. Event
  methods delegate there; delegated classes must not implement the Rising World
  `Listener` interface.

## Template Adaptation Policy
- New feature plugins begin as an adapted copy of this repository. Rename the
  template packages, classes, assets, descriptor metadata, documentation, and
  player-facing wording for the owning feature.
- Retain the supplied template classes and their responsibilities. Do not
  delete them or replace their logic with a parallel implementation without a
  documented technical reason and coverage for the responsibility being changed.
- Use the supplied `PluginSettings` class and settings-file structure for
  configuration. Do not introduce a settings `record` or an independent
  configuration model as a replacement.

## Dependency Policy
- Keep dependencies minimal.
- Add external libraries only when technically necessary and compatible with the plugin runtime.
- Shared runtime helpers, UI helpers, i18n, persistence helpers, WebSocket helpers, logging, and common settings integration belong in `rw-plugin-oz-tools`.
- Feature-specific business logic must stay in the owning feature plugin.

## Shared Library Policy
- `rw-plugin-oz-tools` is a shared foundation, not a feature dumping ground.
- Move logic to `rw-plugin-oz-tools` only when at least one current or likely sibling plugin benefits from the abstraction.
- Do not introduce dependencies from `rw-plugin-oz-tools` back into feature plugins.

## API Verification Policy
- Verify new Rising World API usage before relying on it.
- Preferred checks are Maven compile, `jar tf`, `javap`, and searching existing source usage with `rg`.
- No agent may silently assume uncertain PluginAPI methods.

## Release Policy
- Preserve Maven workflows and GitHub tag-release behavior.
- Release tags use the existing repository convention, normally `v*`.
- User-visible changes require `HISTORY.md` updates.
- Installation or configuration changes require `README.md` updates.

## Documentation Policy
- `PLANS.md` stays intentionally minimal and links to `docs/active/`, `docs/roadmaps/`, and `docs/phase-archive.md`.
- Active tasks belong in `docs/active/`.
- Large plans and roadmaps belong in `docs/roadmaps/`.
- Completed work is summarized in `docs/phase-archive.md`.
- Planning documents must include objective, ownership, dependencies, risks, validation strategy, affected repositories/plugins, rollback considerations, and checkbox progress.
