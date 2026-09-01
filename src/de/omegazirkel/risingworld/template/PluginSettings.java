package de.omegazirkel.risingworld.template;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import de.omegazirkel.risingworld.MavenTemplate;
import de.omegazirkel.risingworld.tools.OZLogger;
import de.omegazirkel.risingworld.tools.settings.AdminSettingsEntry;
import de.omegazirkel.risingworld.tools.settings.AdminSettingsType;
import de.omegazirkel.risingworld.tools.settings.JsonSettingsFile;
import de.omegazirkel.risingworld.tools.settings.SettingsFileEditor;
import net.risingworld.api.World;

public class PluginSettings {
	private static PluginSettings instance = null;

	private static MavenTemplate plugin;

	private static OZLogger logger() {
		return MavenTemplate.logger();
	}

	// Settings
	public boolean enableWelcomeMessage = false;
	private Path settingsFile;
	private java.util.Map<String, String> currentSettings = new LinkedHashMap<>();
	private java.util.Map<String, String> defaultSettings = new LinkedHashMap<>();

	// END Settings

	public static PluginSettings getInstance(MavenTemplate p) {
		plugin = p;
		return getInstance();
	}

	public static PluginSettings getInstance() {

		if (instance == null) {
			instance = new PluginSettings();
		}
		return instance;
	}

	private PluginSettings() {
	}

	public void initSettings() {
		Path pluginPath = Path.of(plugin.getPath() != null ? plugin.getPath() : ".");
		initSettings(pluginPath.resolve("settings." + safeWorldName() + ".json").toString());
	}

	public void initSettings(String filePath) {
		settingsFile = Path.of(filePath);
		Path defaultSettingsFile = settingsFile.resolveSibling("settings.default.json");
		Path legacySettingsFile = settingsFile.resolveSibling("settings.properties");

		try {
			if (JsonSettingsFile.migrateLegacyProperties(legacySettingsFile, settingsFile))
				logger().info("Migrated legacy settings.properties to " + settingsFile.getFileName());
			if (Files.notExists(settingsFile) && Files.exists(defaultSettingsFile))
				JsonSettingsFile.writeFlatAtomically(settingsFile, JsonSettingsFile.loadFlat(defaultSettingsFile));
			java.util.Map<String, String> settings = JsonSettingsFile.loadFlat(settingsFile);
			java.util.Map<String, String> defaults = JsonSettingsFile.loadFlat(defaultSettingsFile);

			// motd settings
			enableWelcomeMessage = settings.getOrDefault("enableWelcomeMessage",
					defaults.getOrDefault("enableWelcomeMessage", "false")).contentEquals("true");

			logger().info(plugin.getName() + " Plugin settings loaded");
			logger().info("Sending welcome message on login is: " + String.valueOf(enableWelcomeMessage));
			currentSettings = settings;
			defaultSettings = defaults;

		} catch (IOException ex) {
			logger().error("IOException on initSettings: " + ex.getMessage());
			ex.printStackTrace();
		} catch (NumberFormatException ex) {
			logger().error("NumberFormatException on initSettings: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	public List<AdminSettingsEntry> adminSettingsEntries() {
		return Arrays.asList(
				AdminSettingsEntry.group("playerMessages", "Player messages",
						"Messages sent directly to players by this plugin."),
				entry("enableWelcomeMessage", "Welcome message",
						"If true, this plugin sends a welcome message when a player joins.",
						AdminSettingsType.BOOLEAN),
				AdminSettingsEntry.group("examples", "Examples", "Replace or remove template-only examples."),
				new AdminSettingsEntry(
						"exampleSecret",
						"Example secret",
						"Template example for a sensitive value. Replace or remove in real plugins.",
						"",
						"",
						AdminSettingsType.STRING,
						true,
						null));
	}

	private AdminSettingsEntry entry(String key, String label, String description, AdminSettingsType type) {
		return new AdminSettingsEntry(
				key,
				label,
				description,
				currentSettings.getOrDefault(key, defaultSettings.getOrDefault(key, "")),
				defaultSettings.getOrDefault(key, ""),
				type,
				false,
				value -> SettingsFileEditor.writeValue(settingsFile, key, value));
	}

	private static String safeWorldName() {
		String world;
		try {
			world = World.getName();
		} catch (LinkageError ex) {
			world = "default";
		}
		return (world == null || world.isBlank() ? "default" : world).replaceAll("[^A-Za-z0-9._-]", "_");
	}
}
