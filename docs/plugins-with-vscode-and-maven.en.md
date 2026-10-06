# Developing Rising World plugins with VS Code and Maven

This guide is for beginners who want to write a plugin for the **new version of Rising World (Unity)**. You need Java for that. VS Code is the editor; Maven compiles your code and builds a plugin file. You do not need to understand all of Maven before you start.

## 1. What to install

1. Install a **JDK 25** (Java Development Kit), not just a JRE. Rising World includes a JDK under `RisingWorld/data/Java/JDK`; the local installation used to check this guide contains Temurin 25. This template compiles with `--release 25`.
2. Install [Visual Studio Code](https://code.visualstudio.com/) and the [Extension Pack for Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack). The pack includes Java support and Maven for Java.
3. Install [Apache Maven](https://maven.apache.org/install.html) if the `mvn` command is not available yet. You also need Git if you want to clone the project and track your changes.
4. Have a local installation of the new Rising World version or a test server ready. Do your first experiments there instead of on a production server.

Open a **new terminal in VS Code** (`Terminal` → `New Terminal`) and check:

```text
java -version
javac -version
mvn -version
```

`javac` and the Java version reported by Maven should both be **25**. If you use VS Code in WSL, JDK 25 and Maven must be available inside WSL; the JDK included with the Windows game installation is not set up automatically in a WSL terminal.

## 2. How a plugin is structured

A Rising World plugin has a Java class that extends `net.risingworld.api.Plugin` and a `plugin.yml` file. The `main` entry in `plugin.yml` contains the entry class's **fully qualified package and class name**. `name`, `version`, and `author` describe the plugin. If you move or rename the class, update `main` to match.

Maven reads `pom.xml`, which defines the Java version, dependencies, and build steps. Rising World's `PluginAPI` provides types for events, players, and the world. The `provided` dependency scope means the server supplies this API at runtime; your plugin does not need to bundle another copy.

Methods annotated with `@EventMethod` receive events, such as a player entering a command. In this workspace, **only the entry class** registers as a Rising World `Listener`. Its event methods delegate to suitable handlers. This keeps the entry point, game logic, UI, and data storage manageable.

## 3. Your daily workflow in VS Code

Open the **project folder containing `pom.xml`** in VS Code (`File` → `Open Folder`). Wait for the Java extension to load the Maven project. Edit Java files under `src/` and check errors in the Problems panel.

Build from the project folder in VS Code's integrated terminal:

```bash
mvn clean package
```

`clean` removes old build output; `package` compiles the code and creates the artifacts. For a quick rebuild later, `mvn package` is often enough. You can also run the same goals from VS Code's Maven view. A successful build initially proves only that the project **compiled and was packaged**.

Copy the **finished plugin directory** from `dist/<PluginName>/` into the `Plugins` directory of your local Rising World installation or test server. The result should look like `Plugins/<PluginName>/<PluginName>.jar`. Alternatively, extract the ZIP from `dist/` there; make sure it does not add an extra directory level. Start the game or test server and inspect the latest logs for loading errors. Then try the command or feature in the game. After changing code, rebuild, replace the files, and reload the plugin or test server.

**A good first exercise:** Make a command send a short message to the player. Change the message, rebuild, and check in the game that the new version is actually running.

## 4. Troubleshooting

| Symptom | What to check |
| --- | --- |
| `mvn` or `javac` is not found | Install Maven or a **JDK**, then open a new terminal. |
| Maven reports the wrong Java version | Check `mvn -version`; Maven must run on JDK 25. |
| `net.risingworld.api` is not found | Check `libs/PluginAPI.jar`, the version in `pom.xml`, and whether Maven has completed a full build. |
| Maven cannot resolve OZ Tools | Check network access and GitHub Packages permissions; see section 5. |
| The build works but the plugin does not load | Check `plugin.yml` (`main`/`version`), the plugin directory layout, matching API version, required plugins, and server logs. |
| A code change is not visible in the game | Check that you copied the **newly built** directory and reloaded the plugin. |

The [official VS Code Java guide](https://code.visualstudio.com/docs/java/java-tutorial), [Maven support in VS Code](https://code.visualstudio.com/docs/java/java-build), and [Rising World's Plugin API introduction](https://forum.rising-world.net/thread/4757-create-a-plugin/) cover the basics. Make sure Rising World examples use the **new Plugin API**; guides for the old Java game version may differ.

## 5. Using this Maven template

This repository is the starting point for new plugins in this workspace. Create a new repository from `Devidian/rw-plugin-maven-template` on GitHub (the “Use this template” button), or copy the repository without its Git history. Clone your new repository and open **its** folder in VS Code. Do not create a second, empty Maven project for this template.

Work through these places:

1. **Identity:** Change `groupId`, `artifactId`, `version`, `name`, and `description` in `pom.xml`. The `artifactId` should match the assembly filename `src/assembly/<artifactId>.xml`. Keep the versions in `pom.xml` and `src/resources/plugin.yml` the same.
2. **Entry class:** Rename `src/de/omegazirkel/risingworld/MavenTemplate.java` and its package for your plugin. Update `main` in `src/resources/plugin.yml`. Rename the classes and packages under `src/de/omegazirkel/risingworld/template/` as appropriate, and update their imports.
3. **Distribution package:** Rename `src/assembly/rw-plugin-maven-template.xml` to match the `artifactId`. Set its `directory` and `outputDirectory` to the new plugin name. The template uses `${project.name}` for the `dist/<PluginName>/` directory and JAR name.
4. **Player text and assets:** Replace the sample names, the `mt` command, translations under `src/i18n/`, icons under `src/main/resources/assets/`, the `maven-template` icon key, and the sample description. Adapt `src/settings.default.json` and the existing `PluginSettings` class instead of adding a second settings system.
5. **Project information:** Adapt `README.md`, `HISTORY.md`, and the GitHub workflows to your new repository. Keep the supplied runtime, UI, and integration classes in their roles and extend them for your feature.

For a **first exercise before renaming anything**, add `case "ping" -> player.sendTextMessage("Pong!");` to the switch in `TemplatePlayerEventHandler.onPlayerCommand(...)`. After `mvn clean package` and installation of `dist/MavenTemplate/`, `/mt ping` should reply “Pong!” in the game. This checks the whole path from editor to running plugin before you adapt the template to your own project.

### Dependencies and other plugins

The template contains a `PluginAPI.jar` from the Rising World installation used to check this guide (`RisingWorld/data/Java/PluginAPI.jar`). On that Windows machine it is at `G:\SteamLibrary\steamapps\common\RisingWorld\data\Java\PluginAPI.jar`. If your game has been updated, compare that file with `libs/PluginAPI.jar` and use the current version. When the API changes, also check the version declarations in `pom.xml` and `.github/workflows/ci.yml`.

The template uses `PluginAPI` **0.9.3.2** from `libs/PluginAPI.jar` and `rw-plugin-oz-tools` **0.27.3** from GitHub Packages. The OZ Tools dependency must be available for a local build. If Maven reports an authentication error while downloading it, configure GitHub Packages access for repository ID `github-oz-tools` in your personal `~/.m2/settings.xml`. [GitHub explains Maven authentication](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry). Do **not** put credentials in `pom.xml` or Git. Plugins built from this template also need OZ Tools on the test server.

OZ Tools provides shared components for menus, UI, translations, settings, and persistence. The template demonstrates them in `TemplatePluginRuntime`, `PluginGUI`, and `PluginSettings`. Three small bridges for optional integrations are included in the `template/` package:

| Interface | Purpose | Template entry point |
| --- | --- | --- |
| `WalletBridge` | Currencies, balances, deposits, and withdrawals | `src/de/omegazirkel/risingworld/template/WalletBridge.java` |
| `MailBridge` | Messages and attachments for players | `src/de/omegazirkel/risingworld/template/MailBridge.java` |
| `DiscordBridge` | Messages through the Discord plugin | `src/de/omegazirkel/risingworld/template/DiscordBridge.java` |

The bridges delegate to OZ Tools and keep calls to other plugins in one place. Before using an optional feature, check whether its interface is available and handle failed calls. Install the target plugin only if you use that feature. Start your first plugin with a simple command, then add integrations one at a time.

The [English OZ plugin interface directory](oz-plugin-interfaces.en.md) has a separate section for **every OZ plugin**, including plugins that do not currently expose a general cross-plugin API.

**Before sharing your plugin:** Run `mvn clean package`, start the generated directory in a local game or test server, and check the logs and at least one player action. See [README.md](../README.md) and [RUNTIME_TESTING.md](../RUNTIME_TESTING.md) for more detail.
