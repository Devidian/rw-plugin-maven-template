# OZ-Plugins: Schnittstellen für neue Plugins

Dieses Verzeichnis beschreibt die **nach außen nutzbaren** Integrationspunkte der OZ-Plugins im Workspace, Stand 6. Oktober 2026. Ein `public` in einer internen Java-Klasse macht eine Methode noch nicht zu einer unterstützten Plugin-Schnittstelle. Verwende die hier genannten Brücken und Hauptklassen statt Datenbanken, Laufzeitklassen oder Einstellungen anderer Plugins direkt zu verändern.

**Grundregel:** OZ Tools ist die gemeinsame Pflichtabhängigkeit des Maven-Templates. Die übrigen Plugins sind nur nötig, wenn du ihre Funktionen nutzt. Bei optionalen Integrationen zuerst Verfügbarkeit prüfen und Fehler beziehungsweise `null`-Ergebnisse behandeln. Namen wie `OZ - Wallet` beziehen sich auf den `name`-Wert der jeweiligen `plugin.yml`, nicht auf den Repository-Namen. Versionen und Methodensignaturen können sich ändern; prüfe vor einer Veröffentlichung, ob sie zur tatsächlich installierten Plugin-Version passen.

## OZ Tools (`rw-plugin-oz-tools`)

- **Aufgabe:** Gemeinsame Übersetzungen, Logger, Einstellungen, SQLite-Verbindungen, Menüs, Inventar-Schaltflächen, Indikatoren und Info-/Statusanzeige.
- **Einstieg:** Bereits als Maven-Abhängigkeit im Template. `TemplatePluginRuntime`, `PluginGUI` und `PluginSettings` zeigen die Verwendung. Häufige Verträge sind `I18n.getInstance(plugin)`, `SQLiteConnectionFactory.open(plugin)`, `PluginMenuManager.registerPluginMenu(...)`, `InventoryOverlayButtons.registerButton(...)`, `SharedIndicators.registerProvider(...)` und `PluginInfoStatusProviders.registerProvider(...)`.
- **Optionale Brücken:** `de.omegazirkel.risingworld.tools.bridge.WalletBridge`, `MailBridge`, `DiscordBridge` und `FactionBridge` kapseln Zugriffe auf die entsprechenden Plugins. Das Template hat eigene kurze Adapter für Wallet, Mail und Discord.
- **Weitere Schnittstelle:** `OZTools.registerGameConnectorFeature(eventName, onReady)` und `publishGameConnectorEvent(eventName, data)` für registrierte Game-Connector-Ereignisse. Das zurückgegebene `AutoCloseable` beim Deaktivieren schließen.

## OZ Wallet (`rw-plugin-oz-wallet`)

- **Aufgabe:** Währungen, Spielerkonten, Transaktionen und Systemkonten. Andere Plugins sollten Wallet nicht über seine Datenbank ansprechen.
- **Einstieg:** `new WalletBridge(ownerPlugin)` aus OZ Tools oder der gleichnamige Template-Adapter. `isAvailable()` prüft, ob `OZ - Wallet` geladen ist. `defaultCurrencyIdentifier()`, `balanceDefault(playerDbId)`, `depositDefault(...)` und `withdrawDefault(...)` decken einfache Fälle ab.
- **Weitere Verträge:** `registerCurrency(...)`, `listCurrencies()`, `transferIdempotent(...)` sowie `hasSystemAccountApi()` und die Methoden für Systemkonten. Bei Transfers eine stabile `correlationId` für Wiederholungen verwenden. Ergebnisse auf `success()` prüfen; ein fehlendes Wallet darf keine kostenlose Leistung auslösen.

## OZ Shop (`rw-plugin-oz-shop`)

- **Aufgabe:** Systemangebote, Plugin-Angebote, NPC-Händler und Wallet-gestützte Käufe.
- **Einstieg:** Das geladene Plugin heißt `OZ - Shop`; seine Hauptklasse `de.omegazirkel.risingworld.Shop` bietet `registerOffer(...)`, `registerContextOffer(...)`, `unregisterOffer(id, pluginIdentifier)` und `unregisterOffers(pluginIdentifier)` für fremde Angebote. `ShopOffer`, `ShopPurchaseCallback`, `ShopPurchaseResult` und `ShopOfferRegistrationResult` sind die zugehörigen Typen.
- **Wichtig:** Angebote beim Aktivieren registrieren und beim Deaktivieren abmelden; Shop speichert Plugin-Angebote nicht dauerhaft. Der Callback liefert die vom eigenen Plugin verantwortete Leistung. `purchase(...)`, `findOffer(...)`, `listOffers()` und `systemOfferBaseUnitPrice(itemName, itemVariant)` sind weitere öffentliche Methoden. Für eine optionale Anbindung wie GPS und Land Claim die Shop-Klassen nur zur Laufzeit über einen eigenen Bridge-Adapter verwenden; für eine feste Anbindung die Shop-API als Build-Abhängigkeit aufnehmen.

## OZ GPS (`rw-plugin-oz-gps`)

- **Aufgabe:** Teleportpunkte, Marker, Gruppenmarker, Gebühren und Abklingzeiten.
- **Schnittstelle für andere Plugins:** Derzeit **keine allgemeine veröffentlichte API** zum Erstellen oder Teleportieren über GPS-Marker. Die vorhandenen `WalletBridge`, `ShopBridge` und `DiscordConnect` sind von GPS **ausgehende** Integrationen, keine fremd nutzbare GPS-API.
- **Für neue Funktionen:** Eine GPS-Funktion über eine gezielte öffentliche Schnittstelle ergänzen, bevor ein anderes Plugin sie aufruft. Keine GPS-Datenbank oder privaten Markerklassen direkt verwenden.

## OZ Land Claim (`rw-plugin-oz-land-claim`)

- **Aufgabe:** Besitz, Berechtigungen, Claim-Verkauf, Pacht und Erneuerungszonen.
- **Einstieg für bestehende Integrationen:** Die Hauptklasse `de.omegazirkel.risingworld.LandClaim` stellt unter anderem `extraClaimCapacityService()`, `claimSaleListingService()`, `renewZoneConfigService()`, `renewZoneResetService()`, `playerLeaseService()`, `propertyClearanceService()` und `landPriceService()` bereit. Diese Zugriffe sind fachlich spezialisiert; prüfe bei Nutzung den konkreten Vertrag und `null` nach Plugin-Start beziehungsweise -Stop.
- **Grenze:** Wallet-, Shop- und Discord-Bridges im Land-Claim-Code sind ausgehende Integrationen. Für allgemeine fremde Claim-Abfragen gibt es hier noch keinen einfachen Bridge-Vertrag.

## OZ Marketplace (`rw-plugin-oz-marketplace`)

- **Aufgabe:** Spielerangebote, Marktflächen, Gebühren, Verkäuferauszahlungen und Kaufabwicklung.
- **Schnittstelle für andere Plugins:** Derzeit **keine allgemeine veröffentlichte API** zum Anlegen oder Kaufen von Marketplace-Angeboten. Marketplace nutzt selbst Wallet und Mail sowie Shop-Angebote für Kapazitätserweiterungen.
- **Grenze:** Spielerhandel und Zustellung gehören zum Marketplace; fremde Plugins sollten weder dessen Datenbank noch seine internen Transaktionsschritte aufrufen.

## OZ Mail (`rw-plugin-oz-mail`)

- **Aufgabe:** Postfächer, Nachrichten, Anhänge und vertrauenswürdige Zustellung durch andere Plugins.
- **Einstieg:** `new MailBridge(ownerPlugin)` aus OZ Tools oder der Template-Adapter. `sendTextMail(new PluginMailRequest(senderPlugin, recipientDbId, recipientName, subject, body, correlationId))` sendet Text; `sendAttachmentMail(new PluginAttachmentMailRequest(...))` sendet Anhänge. `sendSystemReport(...)`, `canReceiveMail(recipientDbId)` und `maxSubjectLength()` decken Berichte und Vorabprüfungen ab.
- **Wichtig:** `senderPlugin` muss dem `name` des aufrufenden Plugins entsprechen. Prüfe `BridgeResult.success()`, `code()` und bei kritischen Fehlern `reconciliationRequired()`. Verwende für wiederholbare Sendungen eine stabile `correlationId`; Anhänge erst nach bestätigter Übernahme als zugestellt behandeln. Die Serverkonfiguration kann erlaubte Absender und Betreffgrenzen einschränken.

## OZ Rewards (`rw-plugin-oz-rewards`)

- **Aufgabe:** Login-, Kampf-, Meilenstein- und Entdeckungsbelohnungen.
- **Schnittstelle für andere Plugins:** Derzeit **keine allgemeine veröffentlichte API** zum Auslösen oder Konfigurieren von Belohnungen. Das Plugin zahlt optional über Wallet aus und verschickt optional Discord-Meldungen.
- **Für neue Funktionen:** Einen gezielten Reward-Vertrag im Rewards-Plugin definieren; keine Belohnungszustände oder Streak-Daten direkt verändern.

## OZ Bosses (`rw-plugin-oz-bosses`)

- **Aufgabe:** Sektorgefahr, Bossgruppen, Kampfwertung und Beute.
- **Schnittstelle für andere Plugins:** Derzeit **keine allgemeine veröffentlichte API** zum Spawnen oder Steuern von Bossen. Öffentliche Methoden in `BossViewService` dienen der eigenen Admin-Oberfläche und sind kein zugesagter Fremd-Plugin-Vertrag.
- **Grenze:** Wallet-Belohnungen und Discord-Meldungen sind ausgehende Integrationen des Boss-Plugins.

## OZ Admin Utils (`rw-plugin-oz-admin-utils`)

- **Aufgabe:** Moderation, Gefängnisabläufe, Schutzregeln und Serverneustarts.
- **Einstieg:** Die Hauptklasse `de.omegazirkel.risingworld.AdminUtils` bietet `requestRestartFromDiscord()` für die Discord-Integration und `requestRestartFromPlayer(player)` für autorisierte Spieleraktionen. Beide liefern einen Status-Text zurück; das ist keine frei verfügbare Neustartberechtigung.
- **Fachliche Dienste:** `prisonService()`, `prisonerService()`, `prisonIncarcerationService()` und `prisonReleaseService()` sind vorhanden, aber für ein neues Plugin nur nach Prüfung ihrer konkreten Berechtigungs- und Ablaufregeln geeignet. Keine Gefängnisdaten direkt ändern.

## OZ Discord Connect (`rw-plugin-oz-discord-connect`)

- **Aufgabe:** Discord-Bot, Webhooks, Chat-, Ereignis- und Statusmeldungen.
- **Einstieg:** `new DiscordBridge(ownerPlugin)` aus OZ Tools oder der Template-Adapter. `isAvailable()` prüft das Plugin, `sendTextMessage(message, channelId)` oder `sendTextMessage(message, channelId, imageBytes)` verschickt eine Nachricht; `getBotLanguage()` liefert die Bot-Sprache.
- **Weitere Methoden:** Die Hauptklasse stellt unter anderem `sendDiscordMessageToTextChannel(...)`, `sendDiscordEventMessage(text)` und `sendDiscordStatusMessage(text)` bereit. Die Bridge ist für optionale Nutzung einfacher. Eine erfolgreiche Bridge-Rückgabe bestätigt den Methodenaufruf, **nicht** die spätere Zustellung durch Discord.

## OZ Global Intercom (`rw-plugin-oz-global-intercom`)

- **Aufgabe:** Serverübergreifende Chat-Kanäle über einen Relay-Dienst.
- **Schnittstelle für andere Plugins:** `GlobalIntercom.isRelayConnected()` liefert einen Verbindungsstatus. Eine allgemeine öffentliche API zum Senden in Intercom-Kanäle ist derzeit **nicht dokumentiert**.
- **Grenze:** Relay-Protokoll und Kanalzustand gehören dem Intercom-Plugin; der externe Relay-Dienst ist eine separate Komponente.

## OZ Factions (`rw-plugin-oz-player-factions`)

- **Aufgabe:** Fraktionszugehörigkeit, Rollen, Fraktionskonten und Lizenzen.
- **Einstieg:** `new FactionBridge(ownerPlugin)` aus OZ Tools. `isAvailable()`, `factionIdForPlayer(playerDbId)`, `factionRoleForPlayer(playerDbId)`, `isFactionLeader(playerDbId)` und `factionAccountIdForPlayer(playerDbId)` liefern die üblichen Abfragen.
- **Weitere Abfragen:** `factionDefaultCurrencyBalance(factionId)`, `claimLicenses(factionId)`, `traderLicenses(factionId)`, `crierLicenses(factionId)`, `serviceNpcLicenses(factionId)` sowie `canManageClaim/Trader/Crier/ServiceNpc(playerDbId)`. `null` bedeutet, dass Plugin, Spieler oder Wert fehlen kann; es ist nicht automatisch die Zahl `0`.

## OZ Service NPC (`rw-plugin-oz-service-npc`)

- **Aufgabe:** Interaktive Service-NPCs und deren eigene Konfiguration.
- **Schnittstelle für andere Plugins:** Derzeit **keine allgemeine veröffentlichte API** zum Anlegen oder Steuern von Service-NPCs. Das Plugin nutzt OZ-Tools-Bausteine für UI, Einstellungen und Status.
- **Für neue Funktionen:** Einen kleinen, expliziten Service-NPC-Vertrag definieren, bevor andere Plugins seine internen Runtime-Klassen verwenden.

## OZ Stargate (`rw-plugin-oz-stargate`)

- **Aufgabe:** Gate-Bedienung, lokale Gate-Daten, Transfers und Spieleroberfläche.
- **Schnittstelle für andere Plugins:** Derzeit **keine allgemeine veröffentlichte API** zum Anwählen oder Transferieren über Gates. Die vorhandenen Wallet-, Mail- und Discord-Bridges sind ausgehende Integrationen.
- **Grenze:** Die Verbindung zu `rw-stargate-network` ist ein eigener Relay-Vertrag. Andere Plugins sollten dessen Transferzustand nicht direkt verändern.

Zurück zur [Einstiegsanleitung](plugins-mit-vscode-und-maven.de.md).
