# OZ plugins: interfaces for new plugins

This directory describes the **integration points intended for other plugins** in the OZ plugin workspace, as of October 6, 2026. A `public` method in an internal Java class is not necessarily a supported plugin interface. Use the bridges and entry classes described here instead of changing another plugin's database, runtime classes, or settings directly.

**General rule:** OZ Tools is the Maven template's shared required dependency. The other plugins are needed only when you use their features. For optional integrations, check availability first and handle errors or `null` results. Names such as `OZ - Wallet` refer to the `name` value in the relevant `plugin.yml`, not the repository name. Versions and method signatures may change; before releasing, check that they match the plugin versions actually installed.

## OZ Tools (`rw-plugin-oz-tools`)

- **Purpose:** Shared translations, logging, settings, SQLite connections, menus, inventory buttons, indicators, and info/status displays.
- **Entry point:** Already a Maven dependency in the template. `TemplatePluginRuntime`, `PluginGUI`, and `PluginSettings` demonstrate its use. Common contracts include `I18n.getInstance(plugin)`, `SQLiteConnectionFactory.open(plugin)`, `PluginMenuManager.registerPluginMenu(...)`, `InventoryOverlayButtons.registerButton(...)`, `SharedIndicators.registerProvider(...)`, and `PluginInfoStatusProviders.registerProvider(...)`.
- **Optional bridges:** `de.omegazirkel.risingworld.tools.bridge.WalletBridge`, `MailBridge`, `DiscordBridge`, and `FactionBridge` wrap calls to the corresponding plugins. The template includes short adapters of its own for Wallet, Mail, and Discord.
- **Another interface:** `OZTools.registerGameConnectorFeature(eventName, onReady)` and `publishGameConnectorEvent(eventName, data)` support registered Game Connector events. Close the returned `AutoCloseable` when disabling the plugin.

## OZ Wallet (`rw-plugin-oz-wallet`)

- **Purpose:** Currencies, player accounts, transactions, and system accounts. Other plugins should not access Wallet through its database.
- **Entry point:** `new WalletBridge(ownerPlugin)` from OZ Tools or the template adapter of the same name. `isAvailable()` checks whether `OZ - Wallet` is loaded. `defaultCurrencyIdentifier()`, `balanceDefault(playerDbId)`, `depositDefault(...)`, and `withdrawDefault(...)` cover simple cases.
- **Other contracts:** `registerCurrency(...)`, `listCurrencies()`, `transferIdempotent(...)`, `hasSystemAccountApi()`, and the system account methods. Use a stable `correlationId` when retrying transfers. Check `success()` on results; an unavailable Wallet must not make a paid feature free.

## OZ Shop (`rw-plugin-oz-shop`)

- **Purpose:** System offers, plugin offers, NPC traders, and Wallet-backed purchases.
- **Entry point:** The loaded plugin is named `OZ - Shop`. Its entry class, `de.omegazirkel.risingworld.Shop`, offers `registerOffer(...)`, `registerContextOffer(...)`, `unregisterOffer(id, pluginIdentifier)`, and `unregisterOffers(pluginIdentifier)` for other plugins' offers. Related types include `ShopOffer`, `ShopPurchaseCallback`, `ShopPurchaseResult`, and `ShopOfferRegistrationResult`.
- **Important:** Register offers on enable and unregister them on disable; Shop does not persist plugin offers. The callback delivers the feature owned by your plugin. Other public methods include `purchase(...)`, `findOffer(...)`, `listOffers()`, and `systemOfferBaseUnitPrice(itemName, itemVariant)`. For an optional connection like GPS or Land Claim, access Shop classes at runtime through your own bridge; for a required connection, add the Shop API as a build dependency.

## OZ GPS (`rw-plugin-oz-gps`)

- **Purpose:** Teleport destinations, markers, group markers, fees, and cooldowns.
- **Interface for other plugins:** There is currently **no general published API** for creating GPS markers or teleporting through them. The existing `WalletBridge`, `ShopBridge`, and `DiscordConnect` are **outgoing** integrations used by GPS, not a GPS API for other plugins.
- **For new features:** Add a focused public GPS contract before another plugin calls such a feature. Do not use GPS's database or private marker classes directly.

## OZ Land Claim (`rw-plugin-oz-land-claim`)

- **Purpose:** Ownership, permissions, claim sales, leases, and renewal zones.
- **Entry point for existing integrations:** The entry class `de.omegazirkel.risingworld.LandClaim` exposes, among others, `extraClaimCapacityService()`, `claimSaleListingService()`, `renewZoneConfigService()`, `renewZoneResetService()`, `playerLeaseService()`, `propertyClearanceService()`, and `landPriceService()`. These are specialized contracts; check the specific behavior and handle `null` around plugin startup or shutdown.
- **Boundary:** Wallet, Shop, and Discord bridges in Land Claim are outgoing integrations. There is not yet a simple bridge contract for general claim queries from other plugins.

## OZ Marketplace (`rw-plugin-oz-marketplace`)

- **Purpose:** Player listings, market areas, fees, seller payouts, and purchase handling.
- **Interface for other plugins:** There is currently **no general published API** for creating or buying Marketplace listings. Marketplace itself uses Wallet and Mail, and Shop offers for capacity upgrades.
- **Boundary:** Player trading and delivery belong to Marketplace. Other plugins should not call its database or internal transaction steps.

## OZ Mail (`rw-plugin-oz-mail`)

- **Purpose:** Mailboxes, messages, attachments, and trusted delivery from other plugins.
- **Entry point:** `new MailBridge(ownerPlugin)` from OZ Tools or the template adapter. `sendTextMail(new PluginMailRequest(senderPlugin, recipientDbId, recipientName, subject, body, correlationId))` sends text; `sendAttachmentMail(new PluginAttachmentMailRequest(...))` sends attachments. `sendSystemReport(...)`, `canReceiveMail(recipientDbId)`, and `maxSubjectLength()` cover reports and preliminary checks.
- **Important:** `senderPlugin` must match the calling plugin's `name`. Check `BridgeResult.success()`, `code()`, and, for critical failures, `reconciliationRequired()`. Use a stable `correlationId` for repeatable sends; treat attachments as delivered only after custody is confirmed. Server settings may restrict approved senders and subject lengths.

## OZ Rewards (`rw-plugin-oz-rewards`)

- **Purpose:** Login, combat, milestone, and discovery rewards.
- **Interface for other plugins:** There is currently **no general published API** for triggering or configuring rewards. The plugin optionally pays through Wallet and sends Discord messages.
- **For new features:** Define a focused contract in Rewards; do not change reward state or streak data directly.

## OZ Bosses (`rw-plugin-oz-bosses`)

- **Purpose:** Sector threat, boss groups, combat rankings, and loot.
- **Interface for other plugins:** There is currently **no general published API** for spawning or controlling bosses. Public methods in `BossViewService` serve Bosses' own admin UI and are not a promised contract for other plugins.
- **Boundary:** Wallet rewards and Discord messages are outgoing integrations of Bosses.

## OZ Admin Utils (`rw-plugin-oz-admin-utils`)

- **Purpose:** Moderation, prison workflows, protection rules, and server restarts.
- **Entry point:** The entry class `de.omegazirkel.risingworld.AdminUtils` offers `requestRestartFromDiscord()` for the Discord integration and `requestRestartFromPlayer(player)` for authorized player actions. Both return status text; neither grants unrestricted permission to restart the server.
- **Specialized services:** `prisonService()`, `prisonerService()`, `prisonIncarcerationService()`, and `prisonReleaseService()` exist, but a new plugin should use them only after checking their specific permission and workflow rules. Do not change prison data directly.

## OZ Discord Connect (`rw-plugin-oz-discord-connect`)

- **Purpose:** Discord bot, webhooks, chat, event, and status messages.
- **Entry point:** `new DiscordBridge(ownerPlugin)` from OZ Tools or the template adapter. `isAvailable()` checks for the plugin; `sendTextMessage(message, channelId)` or `sendTextMessage(message, channelId, imageBytes)` sends a message; `getBotLanguage()` returns the bot language.
- **Other methods:** The entry class also exposes `sendDiscordMessageToTextChannel(...)`, `sendDiscordEventMessage(text)`, and `sendDiscordStatusMessage(text)`, among others. The bridge is simpler for optional use. A successful bridge result confirms the method call, **not** eventual delivery by Discord.

## OZ Global Intercom (`rw-plugin-oz-global-intercom`)

- **Purpose:** Cross-server chat channels through a relay service.
- **Interface for other plugins:** `GlobalIntercom.isRelayConnected()` reports connection status. A general public API for sending to Intercom channels is currently **not documented**.
- **Boundary:** The relay protocol and channel state belong to Global Intercom; the external relay service is a separate component.

## OZ Factions (`rw-plugin-oz-player-factions`)

- **Purpose:** Faction membership, roles, faction accounts, and licenses.
- **Entry point:** `new FactionBridge(ownerPlugin)` from OZ Tools. `isAvailable()`, `factionIdForPlayer(playerDbId)`, `factionRoleForPlayer(playerDbId)`, `isFactionLeader(playerDbId)`, and `factionAccountIdForPlayer(playerDbId)` cover common queries.
- **Other queries:** `factionDefaultCurrencyBalance(factionId)`, `claimLicenses(factionId)`, `traderLicenses(factionId)`, `crierLicenses(factionId)`, `serviceNpcLicenses(factionId)`, and `canManageClaim/Trader/Crier/ServiceNpc(playerDbId)`. `null` can mean that the plugin, player, or value is missing; it does not automatically mean `0`.

## OZ Service NPC (`rw-plugin-oz-service-npc`)

- **Purpose:** Interactive service NPCs and their configuration.
- **Interface for other plugins:** There is currently **no general published API** for creating or controlling service NPCs. The plugin uses OZ Tools for UI, settings, and status.
- **For new features:** Define a small, explicit Service NPC contract before other plugins use its internal runtime classes.

## OZ Stargate (`rw-plugin-oz-stargate`)

- **Purpose:** Gate controls, local gate data, transfers, and player UI.
- **Interface for other plugins:** There is currently **no general published API** for dialing gates or transferring through them. The existing Wallet, Mail, and Discord bridges are outgoing integrations.
- **Boundary:** The connection to `rw-stargate-network` is a separate relay contract. Other plugins should not alter its transfer state directly.

Return to the [getting started guide](plugins-with-vscode-and-maven.en.md).
