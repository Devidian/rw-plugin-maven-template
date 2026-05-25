package de.omegazirkel.risingworld.template;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.logging.log4j.Level;

import de.omegazirkel.risingworld.MavenTemplate;
import de.omegazirkel.risingworld.tools.OZLogger;
import de.omegazirkel.risingworld.tools.settings.AdminSettingsEntry;
import de.omegazirkel.risingworld.tools.settings.AdminSettingsType;
import de.omegazirkel.risingworld.tools.settings.SettingsFileEditor;

public class PluginSettings {
	private static PluginSettings instance = null;

	private static MavenTemplate plugin;

	private static OZLogger logger() {
		return MavenTemplate.logger();
	}

	// Settings
	public String logLevel = Level.DEBUG.name();
	public boolean reloadOnChange = false;
	public boolean enableWelcomeMessage = false;
	private Path settingsFile;
	private Properties currentSettings = new Properties();
	private Properties defaultSettings = new Properties();

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
		initSettings((plugin.getPath() != null ? plugin.getPath() : ".") + "/settings.properties");
	}

	public void initSettings(String filePath) {
		settingsFile = Paths.get(filePath);
		Path defaultSettingsFile = settingsFile.resolveSibling("settings.default.properties");

		try {
			if (Files.notExists(settingsFile) && Files.exists(defaultSettingsFile)) {
				logger().info("settings.properties not found, copying from settings.default.properties...");
				Files.copy(defaultSettingsFile, settingsFile);
			}

			Properties settings = new Properties();
			Properties defaults = new Properties();
			if (Files.exists(defaultSettingsFile)) {
				try (FileInputStream in = new FileInputStream(defaultSettingsFile.toFile())) {
					defaults.load(new InputStreamReader(in, "UTF8"));
				}
			}
			if (Files.exists(settingsFile)) {
				try (FileInputStream in = new FileInputStream(settingsFile.toFile())) {
					settings.load(new InputStreamReader(in, "UTF8"));
				}
			} else {
				logger().warn(
						"⚠️ Neither settings.properties nor settings.default.properties found. Using default values.");
			}
			// fill global values
			logLevel = settings.getProperty("logLevel", defaults.getProperty("logLevel", "ALL"));
			reloadOnChange = settings.getProperty("reloadOnChange", defaults.getProperty("reloadOnChange", "true"))
					.contentEquals("true");

			// motd settings
			enableWelcomeMessage = settings
					.getProperty("enableWelcomeMessage", defaults.getProperty("enableWelcomeMessage", "false"))
					.contentEquals("true");

			logger().info(plugin.getName() + " Plugin settings loaded");
			logger().info("Sending welcome message on login is: " + String.valueOf(enableWelcomeMessage));
			logger().info("Loglevel is set to " + logLevel);
			logger().setLevel(logLevel);
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
				AdminSettingsEntry.group("logging", "Logging", "Logging output and verbosity."),
				entry("logLevel", "Log level", "Controls plugin logging verbosity.", AdminSettingsType.STRING),
				AdminSettingsEntry.group("runtime", "Runtime", "Runtime reload and maintenance behavior."),
				entry("reloadOnChange", "Reload on change",
						"If true, this plugin reloads settings when settings.properties changes.",
						AdminSettingsType.BOOLEAN),
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
				currentSettings.getProperty(key, defaultSettings.getProperty(key, "")),
				defaultSettings.getProperty(key, ""),
				type,
				false,
				value -> SettingsFileEditor.writeValue(settingsFile, key, value));
	}
}
