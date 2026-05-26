package de.omegazirkel.risingworld;

import java.nio.file.Path;

import de.omegazirkel.risingworld.template.PluginGUI;
import de.omegazirkel.risingworld.template.PluginSettings;
import de.omegazirkel.risingworld.template.ui.TemplatePluginInfoStatusProvider;
import de.omegazirkel.risingworld.template.ui.TemplatePlayerPluginData;
import de.omegazirkel.risingworld.template.ui.TemplatePlayerPluginSettings;
import de.omegazirkel.risingworld.tools.Colors;
import de.omegazirkel.risingworld.tools.FileChangeListener;
import de.omegazirkel.risingworld.tools.I18n;
import de.omegazirkel.risingworld.tools.OZLogger;
import de.omegazirkel.risingworld.tools.settings.PlayerPluginAdminSettings;
import de.omegazirkel.risingworld.tools.ui.AssetManager;
import de.omegazirkel.risingworld.tools.ui.InventoryOverlayButtons;
import de.omegazirkel.risingworld.tools.ui.MenuItem;
import de.omegazirkel.risingworld.tools.ui.PlayerPluginSettingsOverlay;
import de.omegazirkel.risingworld.tools.ui.PluginInfoStatusProviders;
import de.omegazirkel.risingworld.tools.ui.PluginMenuManager;
import de.omegazirkel.risingworld.tools.ui.SharedIndicatorProvider;
import de.omegazirkel.risingworld.tools.ui.SharedIndicators;
import net.risingworld.api.Plugin;
import net.risingworld.api.events.EventMethod;
import net.risingworld.api.events.Listener;
import net.risingworld.api.events.player.PlayerCommandEvent;
import net.risingworld.api.events.player.PlayerSpawnEvent;
import net.risingworld.api.objects.Player;

public class MavenTemplate extends Plugin implements Listener, FileChangeListener {
	static final String pluginCMD = "mt";
	private static final String LOGGER_NAME = "MavenTemplate";
	static final Colors c = Colors.getInstance();
	private static I18n t = null;
	private static PluginSettings s = null;
	private static PluginGUI gui;
	public static String name;

	public static OZLogger logger() {
		return OZLogger.getInstance(LOGGER_NAME);
	}

	@Override
	public void onEnable() {
		name = this.getDescription("name");
		s = PluginSettings.getInstance(this);
		t = I18n.getInstance(this);
		registerEventListener(this);
		s.initSettings();
		gui = PluginGUI.getInstance(this);
		// Load Plugin Menu into Main Plugin Menu
		PluginMenuManager
				// FIXME rename template stuff
				.registerPluginMenu(
						new MenuItem(AssetManager.getIcon("template-icon"), "Template Plugin", (Player p) -> {
							gui.openMainMenu(p);
						}));
		InventoryOverlayButtons.registerButton(name, "Open", "template-icon",
				event -> gui.openMainMenu(event.getPlayer()));
		SharedIndicators.registerProvider(name, new SharedIndicatorProvider() {
			@Override
			public boolean showIndicator(Player player) {
				return false;
			}

			@Override
			public String getIcon(Player player) {
				return "template-icon";
			}
		});
		PluginInfoStatusProviders
				.registerProvider(new TemplatePluginInfoStatusProvider(name, getDescription("version"), pluginCMD));
		// register plugin settings
		PlayerPluginSettingsOverlay.registerPlayerPluginSettings(new TemplatePlayerPluginSettings(getDescription("version")));
		PlayerPluginSettingsOverlay.registerPlayerPluginData(new TemplatePlayerPluginData(getDescription("version")));
		PlayerPluginSettingsOverlay.registerPlayerPluginAdminSettings(
				new PlayerPluginAdminSettings(name, getDescription("version"), () -> s.adminSettingsEntries(),
						s::initSettings));
		logger().info("✅ " + this.getName() + " Plugin is enabled version:" + this.getDescription("version"));
	}

	@Override
	public void onDisable() {
		if (name != null) {
			InventoryOverlayButtons.unregisterButtons(name);
			SharedIndicators.unregisterProvider(name);
			PluginInfoStatusProviders.unregisterProvider(name);
		}
	}

	@Override
	public void onSettingsChanged(Path settingsPath) {
		s.initSettings(settingsPath.toString());
		logger().setLevel(s.logLevel);
	}

	@EventMethod
	public void onPlayerCommand(PlayerCommandEvent event) {
		Player player = event.getPlayer();
		String lang = player.getSystemLanguage();
		String commandLine = event.getCommand();

		String[] cmdParts = commandLine.split(" ", 2);
		String command = cmdParts[0];

		if (command.equals("/" + pluginCMD)) {
			// Invalid number of arguments (0)
			if (cmdParts.length < 2) {
				gui.openMainMenu(player);
				return;
			}
			String option = cmdParts[1];
			switch (option) {
				case "info":
				case "status":
					PluginInfoStatusProviders.show(player, name);
					break;
                case "help":
                    String helpMessage = t.get("TC_CMD_HELP", player).replaceAll("PH_PLUGIN_CMD", pluginCMD);
                    player.sendTextMessage(c.okay + this.getName() + ":> " + c.endTag + helpMessage);
                    break;
				case "open":
					gui.openMainMenu(player);
					break;
				default:
					player.sendTextMessage(t.get("TC_ERR_CMD_UNKNOWN").replace("PH_PLUGIN_CMD", pluginCMD));
					break;
			}
		}
	}

	@EventMethod
    public void onPlayerSpawnEvent(PlayerSpawnEvent event) {
        Player player = event.getPlayer();

        if (s.enableWelcomeMessage) {
            // Player player = event.getPlayer();
            String lang = player.getSystemLanguage();
            player.sendTextMessage(t.get("TC_MSG_PLUGIN_WELCOME", lang)
                    .replace("PH_PLUGIN_NAME", getDescription("name"))
                    .replace("PH_PLUGIN_CMD", pluginCMD)
                    .replace("PH_PLUGIN_VERSION", getDescription("version")));
        }
    }

}
