# Maven Plugin Template for Rising World

Use this repository as template for new Rising World Plugins.

## Files included

- [.github/workflows/ci.yml](.github/workflows/ci.yml)
  - for GitHub Action, you have to change `Devidian/rw-plugin-maven-template` here (2x)
- [src/assembly/rw-plugin-maven-template.xml](src/assembly/rw-plugin-maven-template.xml)
  - this is needed to pack you plugin as zip, change atleast directory/outputDirectory here
  - change name to your `pom.project.artifactId`
  - i use `pom.project.name` for directory/outputDirectory
- [src/resources/plugin.yml](src/resources/plugin.yml)
  - your plugin definition file, change as you need
- [src/de/omegazirkel/risingworld/MavenTemplate.java](src/de/omegazirkel/risingworld/MavenTemplate.java)
  - sample main file for your plugin, change name and path as you need (dont forget to change it in plugin.yml too)
- [pom.xml](pom.xml)
  - maven file, change as you need it
- [HISTORY.md](HISTORY.md)
  - for your changelog
- [README.md](README.md)
  - this file, override it as you like
- [DESIGN.md](DESIGN.md)
  - synchronized portfolio UI/design baseline; keep it aligned with the root copy

## Baseline behavior

- Requires `rw-plugin-oz-tools`.
- Uses the shared file watcher path by implementing `FileChangeListener`; changes
  to `settings.properties` reload plugin settings.
- Defaults `reloadOnChange=true` in `settings.default.properties`.
- Registers a shared inventory overlay button through `InventoryOverlayButtons`
  so players get a compact entrypoint below the inventory.
- Registers a `SharedIndicators` provider stub. The template returns `false` by
  default; real plugins should only show indicators when they have meaningful
  player-specific state.
- Registers a `PluginInfoStatusProvider` with generic RichText info/status
  content for the shared Tools Info/Status panel.
- Adds an `Info / Status` action to the plugin-owned radial menu. It uses the
  Tools-registered `icon-ki-info-status` asset key; generated plugins should not
  register a duplicate copy of that shared icon.
- Registers player settings, player data, and admin-only `PluginSettings`
  metadata with `PlayerPluginSettingsOverlay`.
- Includes an optional reflection-based `WalletBridge` scaffold for economy
  integrations. It covers Wallet availability, default currency, currency
  listing, currency registration, deposit, withdraw, balance, and default
  currency convenience calls without a compile-time Wallet dependency.
- Includes grouped sample admin settings metadata for booleans and strings, plus
  a hidden sensitive value example that should be replaced or removed in real
  plugins.
- Uses one main plugin logger name. Helper classes should call the main plugin
  logger instead of creating subsystem logger names.

## Shared Tools conventions

Future plugins generated from this template should route shared infrastructure
through `rw-plugin-oz-tools`:

- UI entrypoints: use `InventoryOverlayButtons` for compact inventory actions and
  `PluginMenuManager` for the `/ozt` main plugin menu.
- Indicators: use `SharedIndicators` for reusable HUD indicator slots. Return an
  `AssetManager` icon key from the provider, not a file path.
- Info/status: expose player-facing RichText through `PluginInfoStatusProvider`
  and open it with `PluginInfoStatusProviders.show(player, pluginName)` from
  plugin-owned buttons, menu items, or commands when appropriate. Use the shared
  `icon-ki-info-status` icon key for radial Info/Status buttons.
- Wallet: use the template `WalletBridge` pattern for optional economy
  integrations. Keep feature-specific spending and fulfillment rules inside the
  generated plugin, and disable economy features when Wallet is unavailable.
- Settings: register admin metadata through `PlayerPluginSettingsOverlay`; use
  `AdminSettingsEntry.group(...)` for sections and `AdminSettingsType.INTEGER`
  for numeric settings so Tools can apply shared numeric input filtering.
- i18n, persistence helpers, common UI helpers, and runtime watchers should use
  Tools contracts instead of duplicating helper code in feature plugins.

## Contributor Workflow

- Review `AGENTS.md`, `PLANS.md`, `.codex/agents.toml`, and `.codex/skills/` before making structural changes.
- Verify Rising World API usage with `scripts/verify-plugin-api.sh` when adding or changing API calls.
- Run `mvn -B -DskipTests package` and `mvn -B test` before release-facing changes are merged.
- Use `RUNTIME_TESTING.md` and `scripts/docker-runtime-smoke.sh <PluginFolderName>` for runtime smoke tests when behavior changes need server validation.
- Keep `README.md` and `HISTORY.md` current and use Conventional Commit titles for commits and PRs.
