# History / Changelog / Commitlog

<https://www.conventionalcommits.org/en/v1.0.0/>

## [unreleased]

- change: rename template and shared menu icon keys to their final semantic names
- feat: adopt Plan 04 shortcut visibility and runtime standard conventions
- build: update the OZTools baseline dependency and CI checkout reference to `0.21.0`

- feat: add canonical optional WalletBridge scaffold and shared Info/Status radial-menu action
- refactor: route the template status command to the shared Tools Info/Status panel
- feat: scaffold shared Tools UI, indicator, info/status, grouped settings, and logger conventions
- refactor: remove feature-plugin integration helper stubs from the generic template baseline
- build: align template Tools dependency with 0.18.0 shared settings baseline
- feat: add admin-only `PluginSettings` metadata registration example
- feat: default `reloadOnChange` to true in template settings
- docs: add synchronized portfolio `DESIGN.md` baseline
- fix: restore colored one-line plugin welcome message
- docs: standardize agent prompts, PR checklist, and runtime smoke-test guidance
- build: add API verification helper and stricter CI/release validation flow
- build: package only `README.md` and `HISTORY.md` into release artifacts
