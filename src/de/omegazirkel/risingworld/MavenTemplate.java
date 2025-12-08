package de.omegazirkel.risingworld;

import java.nio.file.Path;

import de.omegazirkel.risingworld.template.PluginSettings;
import de.omegazirkel.risingworld.tools.Colors;
import de.omegazirkel.risingworld.tools.FileChangeListener;
import de.omegazirkel.risingworld.tools.I18n;
import de.omegazirkel.risingworld.tools.OZLogger;
import net.risingworld.api.Plugin;
import net.risingworld.api.events.EventMethod;
import net.risingworld.api.events.Listener;
import net.risingworld.api.events.player.PlayerCommandEvent;
import net.risingworld.api.objects.Player;
import net.risingworld.api.utils.Vector3f;

public class MavenTemplate extends Plugin implements Listener, FileChangeListener {
	static final String pluginCMD = "mt";
	static final Colors c = Colors.getInstance();
	private static I18n t = null;
	private static PluginSettings s = null;

	public static OZLogger logger() {
		return OZLogger.getInstance("MavenTemplate");
	}

	@Override
	public void onEnable() {
		s = PluginSettings.getInstance(this);
		t = I18n.getInstance(this);
		registerEventListener(this);
		s.initSettings();
	}

	@Override
	public void onDisable() {
	}

	@Override
	public void onSettingsChanged(Path settingsPath) {
		s.initSettings(settingsPath.toString());
	}

	@EventMethod
	public void onPlayerCommand(PlayerCommandEvent event) {
		Player player = event.getPlayer();
		String lang = player.getSystemLanguage();
		String commandLine = event.getCommand();
		Vector3f pos = player.getPosition();

		String[] cmdParts = commandLine.split(" ", 2);
		String command = cmdParts[0];

		if (command.equals("/" + pluginCMD)) {
			// Invalid number of arguments (0)
			if (cmdParts.length < 2) {
				return;
			}
			String option = cmdParts[1];
			switch (option) {
				case "status":
					String statusMessage = t.get("CMD_STATUS", lang)
							.replace("PH_VERSION", c.okay + this.getDescription("version") + c.endTag)
							.replace("PH_LANGUAGE",
									c.info + player.getLanguage() + " / " + player.getSystemLanguage() + c.endTag)
							.replace("PH_USEDLANG", c.okay + t.getLanguageUsed(lang) + c.endTag)
							.replace("PH_LANG_AVAILABLE", c.warning + t.getLanguageAvailable() + c.endTag);
					player.sendTextMessage(c.okay + this.getName() + ":> " + c.text + statusMessage);
					break;
				default:
					break;
			}
		}
	}

}
