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
- Registers player settings, player data, and admin-only `PluginSettings`
  metadata with `PlayerPluginSettingsOverlay`.
- Includes sample admin settings metadata for booleans and strings, plus a hidden
  sensitive value example that should be replaced or removed in real plugins.

## Contributor Workflow

- Review `AGENTS.md`, `PLANS.md`, `.codex/agents.toml`, and `.codex/skills/` before making structural changes.
- Verify Rising World API usage with `scripts/verify-plugin-api.sh` when adding or changing API calls.
- Run `mvn -B -DskipTests package` and `mvn -B test` before release-facing changes are merged.
- Use `RUNTIME_TESTING.md` and `scripts/docker-runtime-smoke.sh <PluginFolderName>` for runtime smoke tests when behavior changes need server validation.
- Keep `README.md` and `HISTORY.md` current and use Conventional Commit titles for commits and PRs.
