# Rising-World-Plugins mit VS Code und Maven entwickeln

Diese Anleitung richtet sich an Einsteiger, die ein Plugin für die **neue Rising-World-Version (Unity)** schreiben möchten. Du brauchst dafür Java. VS Code ist der Editor; Maven übersetzt deinen Code und baut daraus eine Plugin-Datei. Du musst Maven nicht erst vollständig verstehen, um anzufangen.

## 1. Was du installierst

1. Installiere ein **JDK 25** (Java Development Kit), kein bloßes JRE. Rising World bringt selbst ein JDK unter `RisingWorld/data/Java/JDK` mit; die hier geprüfte lokale Installation enthält Temurin 25. Dieses Template übersetzt mit `--release 25`.
2. Installiere [Visual Studio Code](https://code.visualstudio.com/) und darin das [Extension Pack for Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack). Es enthält unter anderem Java-Unterstützung und „Maven for Java“.
3. Installiere [Apache Maven](https://maven.apache.org/install.html), falls der Befehl `mvn` noch nicht verfügbar ist. Git brauchst du, wenn du das Projekt klonen und Änderungen versionieren möchtest.
4. Halte eine lokale Rising-World-Installation der neuen Version oder einen Testserver bereit. Auf einem produktiven Server solltest du erste Versuche nicht machen.

Öffne ein **neues Terminal in VS Code** (`Terminal` → `Neues Terminal`) und prüfe:

```text
java -version
javac -version
mvn -version
```

`javac` und die von Maven angezeigte Java-Version sollen **25** sein. Wenn VS Code unter WSL arbeitet, müssen JDK 25 und Maven in WSL verfügbar sein; das mit dem Spiel unter Windows gelieferte JDK ist dann nicht automatisch im WSL-Terminal eingerichtet.

## 2. So ist ein Plugin aufgebaut

Ein Rising-World-Plugin besteht aus einer Java-Klasse, die `net.risingworld.api.Plugin` erweitert, und einer `plugin.yml`. In `plugin.yml` steht unter `main` der **vollständige Paket- und Klassenname** der Einstiegsklasse. `name`, `version` und `author` beschreiben das Plugin. Wenn du die Klasse verschiebst oder umbenennst, musst du `main` entsprechend ändern.

Maven liest die `pom.xml`: Sie legt Java-Version, Abhängigkeiten und Build-Schritte fest. Die Rising-World-`PluginAPI` liefert die Typen für Ereignisse, Spieler und Welt. `provided` bedeutet: Der Server stellt diese API zur Laufzeit bereit; sie muss nicht als zweite Kopie in dein Plugin gepackt werden.

Ereignisse kommen über Methoden mit `@EventMethod`, etwa wenn ein Spieler einen Befehl eingibt. In diesem Workspace registriert sich **nur die Einstiegsklasse** als Rising-World-`Listener`. Ihre Ereignismethoden leiten an passende Handler weiter. So bleiben Einstiegspunkt, Spiellogik, Oberfläche und Datenspeicherung übersichtlich.

## 3. Das tägliche Arbeiten in VS Code

Öffne den **Projektordner mit der `pom.xml`** in VS Code (`Datei` → `Ordner öffnen`). Warte, bis die Java-Erweiterung das Maven-Projekt eingelesen hat. Bearbeite Java-Dateien unter `src/` und prüfe Fehlermeldungen im Tab „Probleme“.

Baue im integrierten Terminal aus dem Projektordner:

```bash
mvn clean package
```

`clean` entfernt alte Build-Ergebnisse; `package` übersetzt den Code und erstellt die Ausgabe. Bei späteren schnellen Durchläufen reicht oft `mvn package`. Im Maven-Bereich von VS Code lassen sich dieselben Ziele anklicken. Ein erfolgreicher Build beweist zunächst nur, dass das Projekt **übersetzt und verpackt** wurde.

Kopiere das **fertige Plugin-Verzeichnis** aus `dist/<PluginName>/` in den `Plugins`-Ordner der lokalen Rising-World-Installation oder des Testservers. Am Ziel sollte beispielsweise `Plugins/<PluginName>/<PluginName>.jar` liegen. Alternativ kannst du das ZIP aus `dist/` dort entpacken; achte darauf, keine zusätzliche Verzeichnisebene zu erzeugen. Starte Spiel oder Testserver und kontrolliere die neuesten Logs auf Ladefehler. Probiere anschließend den Befehl oder die Funktion im Spiel aus. Nach Codeänderungen erneut bauen, Dateien ersetzen und das Plugin bzw. den Testserver neu laden.

**Eine gute erste Übung:** Lass einen Befehl eine kurze Nachricht an den Spieler senden. Danach ändere die Nachricht, baue erneut und prüfe im Spiel, ob wirklich die neue Version läuft.

## 4. Wenn es nicht klappt

| Symptom                                       | Was du prüfst                                                                                                           |
| --------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------- |
| `mvn` oder `javac` wird nicht gefunden        | Maven beziehungsweise **JDK** installieren und ein neues Terminal öffnen.                                               |
| Maven meldet eine falsche Java-Version        | `mvn -version` prüfen; Maven muss mit JDK 25 laufen.                                                                    |
| `net.risingworld.api` wird nicht gefunden     | `libs/PluginAPI.jar`, die Version in `pom.xml` und einen vollständigen Maven-Lauf prüfen.                               |
| OZ-Tools-Abhängigkeit lässt sich nicht laden  | Netzwerkzugang und GitHub-Packages-Berechtigung prüfen; siehe Abschnitt 5.                                              |
| Der Build klappt, aber das Plugin lädt nicht  | `plugin.yml` (`main`/`version`), Plugin-Ordnerstruktur, passende API-Version, benötigte Plugins und Server-Logs prüfen. |
| Eine Codeänderung ist im Spiel nicht sichtbar | Prüfen, ob der **neu gebaute** Ordner kopiert und das Plugin neu geladen wurde.                                         |

Die [offizielle VS-Code-Anleitung für Java](https://code.visualstudio.com/docs/java/java-tutorial), die [Maven-Integration in VS Code](https://code.visualstudio.com/docs/java/java-build) und die [Rising-World-Einführung zur Plugin API](https://forum.rising-world.net/thread/4757-create-a-plugin/) helfen bei den Grundlagen. Achte bei Rising-World-Beispielen darauf, dass sie zur **neuen Plugin API** passen; Anleitungen zur alten Java-Spielversion können abweichen.

## 5. Dieses Maven-Template verwenden

Dieses Repository ist der Startpunkt für neue Plugins in diesem Workspace. Erstelle auf GitHub ein neues Repository aus `Devidian/rw-plugin-maven-template` (Schaltfläche „Use this template“) oder kopiere das Repository ohne seine Git-Historie. Klone dein neues Repository und öffne **dessen** Ordner in VS Code. Erzeuge für dieses Template kein zweites, leeres Maven-Projekt.

Arbeite dann diese Stellen durch:

1. **Identität:** Ändere `groupId`, `artifactId`, `version`, `name` und `description` in `pom.xml`. `artifactId` sollte auch zum Namen der Assembly-Datei `src/assembly/<artifactId>.xml` passen. Halte `pom.xml`-Version und `src/resources/plugin.yml`-Version gleich.
2. **Einstiegsklasse:** Benenne `src/de/omegazirkel/risingworld/MavenTemplate.java` und ihr Paket für dein Plugin um. Passe den `main`-Eintrag in `src/resources/plugin.yml` an. Benenne auch die Klassen und Pakete unter `src/de/omegazirkel/risingworld/template/` passend um; aktualisiere alle Importe.
3. **Ausgabepaket:** Ändere `src/assembly/rw-plugin-maven-template.xml` entsprechend dem `artifactId`. Setze dort `directory` und `outputDirectory` auf den neuen Plugin-Namen. Das Template verwendet `${project.name}` als Namen für `dist/<PluginName>/` und die JAR.
4. **Spielertexte und Assets:** Ersetze Beispielnamen, den Befehl `mt`, Übersetzungen unter `src/i18n/`, Icons unter `src/main/resources/assets/`, den Icon-Schlüssel `maven-template` und die Beispielbeschreibung. Passe `src/settings.default.json` und die vorhandene `PluginSettings`-Klasse an, statt ein zweites Einstellungssystem anzulegen.
5. **Projektangaben:** Passe `README.md`, `HISTORY.md` und die GitHub-Workflow-Dateien an dein neues Repository an. Die mitgelieferten Klassen für Laufzeit, UI und Integration behalten ihre Aufgaben; erweitere sie für deine Funktion.

Für die **erste Übung ohne Umbenennen** kannst du in `TemplatePlayerEventHandler.onPlayerCommand(...)` einen Fall `case "ping" -> player.sendTextMessage("Pong!");` ergänzen. Nach `mvn clean package` und Installation von `dist/MavenTemplate/` sollte `/mt ping` im Spiel „Pong!“ antworten. So prüfst du den vollständigen Weg vom Editor bis zum laufenden Plugin, bevor du die Vorlage auf dein eigenes Projekt anpasst.

### Abhängigkeiten und andere Plugins

Das Template enthält eine `PluginAPI.jar` aus der hier geprüften Rising-World-Installation (`RisingWorld/data/Java/PluginAPI.jar`). Auf dem Windows-Rechner liegt sie unter `G:\SteamLibrary\steamapps\common\RisingWorld\data\Java\PluginAPI.jar`. Wenn dein Spiel aktualisiert wurde, vergleiche diese Datei mit `libs/PluginAPI.jar` und übernimm die aktuelle Version. Prüfe bei einem API-Update auch die Versionsangaben in `pom.xml` und `.github/workflows/ci.yml`.

Das Template verwendet `PluginAPI` **0.9.3.2** aus `libs/PluginAPI.jar` und `rw-plugin-oz-tools` **0.27.3** aus GitHub Packages. Für einen lokalen Build muss die OZ-Tools-Abhängigkeit erreichbar sein. Falls Maven beim Abruf einen Authentifizierungsfehler meldet, richte in deiner persönlichen `~/.m2/settings.xml` einen GitHub-Packages-Zugang für die Repository-ID `github-oz-tools` ein. [GitHub beschreibt die Maven-Anmeldung](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry). Zugangsdaten gehören **nicht** in `pom.xml` oder ins Git-Repository. Die mit diesem Template gebauten Plugins benötigen OZ Tools auch auf dem Testserver.

OZ Tools bietet gemeinsame Bausteine für Menüs, UI, Übersetzungen, Einstellungen und Speicherung. Das Template zeigt ihre Benutzung bereits in `TemplatePluginRuntime`, `PluginGUI` und `PluginSettings`. Für optionale Integrationen liegen drei kleine Brücken im Paket `template/` bereit:

| Schnittstelle   | Wofür sie gedacht ist                         | Einstieg im Template                                         |
| --------------- | --------------------------------------------- | ------------------------------------------------------------ |
| `WalletBridge`  | Währungen, Kontostand, Ein- und Auszahlungen  | `src/de/omegazirkel/risingworld/template/WalletBridge.java`  |
| `MailBridge`    | Nachrichten und Anhänge an Spieler senden     | `src/de/omegazirkel/risingworld/template/MailBridge.java`    |
| `DiscordBridge` | Nachrichten über das Discord-Plugin versenden | `src/de/omegazirkel/risingworld/template/DiscordBridge.java` |

Die Brücken delegieren an OZ Tools und sollen Zugriffe auf die anderen Plugins kapseln. Prüfe vor einer optionalen Funktion, ob die jeweilige Schnittstelle verfügbar ist, und behandle einen fehlgeschlagenen Aufruf. Installiere das Ziel-Plugin nur, wenn du diese Funktion wirklich verwendest. Beginne für dein erstes Plugin am besten mit einem einfachen Befehl; ergänze Integrationen danach einzeln.

Für **jedes OZ-Plugin** gibt es einen eigenen Block mit seinen nutzbaren Schnittstellen und Grenzen im [deutschen Schnittstellenverzeichnis](oz-plugin-schnittstellen.de.md). Dort findest du auch Plugins, die derzeit keine allgemeine Fremd-Plugin-API anbieten.

**Vor dem Weitergeben:** `mvn clean package` ausführen, den erzeugten Ordner in einer lokalen Spiel- oder Testserver-Installation starten und die Logs sowie mindestens eine Spieleraktion prüfen. Für weitere Details stehen [README.md](../README.md) und [RUNTIME_TESTING.md](../RUNTIME_TESTING.md) bereit.
