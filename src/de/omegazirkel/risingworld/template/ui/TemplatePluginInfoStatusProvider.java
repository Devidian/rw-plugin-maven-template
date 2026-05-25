package de.omegazirkel.risingworld.template.ui;

import de.omegazirkel.risingworld.template.PluginSettings;
import de.omegazirkel.risingworld.tools.I18n;
import de.omegazirkel.risingworld.tools.ui.PluginInfoStatusProvider;
import net.risingworld.api.objects.Player;

public class TemplatePluginInfoStatusProvider implements PluginInfoStatusProvider {
    private final String pluginName;
    private final String version;
    private final String command;

    public TemplatePluginInfoStatusProvider(String pluginName, String version, String command) {
        this.pluginName = pluginName;
        this.version = version;
        this.command = command;
    }

    @Override
    public String getPluginName() {
        return pluginName;
    }

    @Override
    public String getInfo(Player player) {
        return t().get("TC_TEMPLATE_INFO_PANEL_INFO", player)
                .replace("PH_PLUGIN_NAME", pluginName)
                .replace("PH_PLUGIN_VERSION", version)
                .replace("PH_PLUGIN_CMD", command);
    }

    @Override
    public String getStatus(Player player) {
        PluginSettings settings = PluginSettings.getInstance();
        return t().get("TC_TEMPLATE_INFO_PANEL_STATUS", player)
                .replace("PH_PLUGIN_NAME", pluginName)
                .replace("PH_LOG_LEVEL", settings.logLevel)
                .replace("PH_RELOAD_ON_CHANGE", String.valueOf(settings.reloadOnChange))
                .replace("PH_WELCOME_MESSAGE", String.valueOf(settings.enableWelcomeMessage));
    }

    private I18n t() {
        return I18n.getInstance(pluginName);
    }
}
