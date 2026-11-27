# rw-plugin-maven-template
<!-- codemap-id: rw-plugin-maven-template -->

## Purpose

Canonical standalone Maven scaffold for Rising World plugins. The [plugin descriptor](../src/resources/plugin.yml) names [MavenTemplate](../src/de/omegazirkel/risingworld/MavenTemplate.java) as its lifecycle entry.

## Modules

- [runtime](modules/runtime.md) — Lifecycle, player events, configuration and UI examples.
- [integrations](modules/integrations.md) — Optional integration examples for sibling plugin APIs.

## Build and tests

[Maven](../pom.xml) defines the Java build. Follow [repository validation](../AGENTS.md) for packaging, API checks and runtime validation.

## Dependencies

- `rw-plugin-oz-tools/shared-api` — build dependency. [Evidence](../pom.xml)
- `rw-plugin-oz-tools/ui-settings` — the runtime retains scoped menu and provider registrations and retires them on disable. [Runtime](../src/de/omegazirkel/risingworld/template/runtime/TemplatePluginRuntime.java)
