package de.omegazirkel.risingworld.template.runtime;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import de.omegazirkel.risingworld.MavenTemplate;
import de.omegazirkel.risingworld.template.PluginGUI;
import de.omegazirkel.risingworld.template.PluginSettings;
import de.omegazirkel.risingworld.template.ui.TemplatePlayerPluginData;
import de.omegazirkel.risingworld.template.ui.TemplatePlayerPluginSettings;
import de.omegazirkel.risingworld.template.ui.TemplatePluginInfoStatusProvider;
import de.omegazirkel.risingworld.tools.I18n;
import de.omegazirkel.risingworld.tools.settings.PlayerPluginAdminSettings;
import de.omegazirkel.risingworld.tools.ui.InventoryOverlayButtons;
import de.omegazirkel.risingworld.tools.ui.MenuItem;
import de.omegazirkel.risingworld.tools.ui.PlayerPluginSettingsOverlay;
import de.omegazirkel.risingworld.tools.ui.PluginInfoStatusProviders;
import de.omegazirkel.risingworld.tools.ui.PluginMenuManager;
import de.omegazirkel.risingworld.tools.ui.PluginShortcutVisibility;
import de.omegazirkel.risingworld.tools.ui.SharedIndicatorProvider;
import de.omegazirkel.risingworld.tools.ui.SharedIndicators;
import net.risingworld.api.objects.Player;

public final class TemplatePluginRuntime {
    public static final String COMMAND = "mt";

    private final MavenTemplate plugin;
    private final String pluginName;
    private final PluginSettings settings;
    private final I18n i18n;
    private final PluginGUI gui;
    private final TemplatePlayerEventHandler events;
    private final List<Runnable> uiRegistrations = new ArrayList<>();

    public TemplatePluginRuntime(MavenTemplate plugin) {
        this.plugin = plugin;
        pluginName = plugin.getDescription("name");
        settings = PluginSettings.getInstance(plugin);
        i18n = I18n.getInstance(plugin);
        gui = PluginGUI.getInstance(plugin);
        events = new TemplatePlayerEventHandler(plugin, pluginName, settings, i18n, gui);
    }

    public void enable() {
        settings.initSettings();
        uiRegistrations.add(PluginMenuManager.registerScopedPluginMenu(
                new MenuItem(pluginName, "maven-template", "Template Plugin", gui::openMainMenu)));
        PluginShortcutVisibility.register(pluginName, player -> true);
        InventoryOverlayButtons.registerButton(pluginName, "Open", "maven-template",
                event -> gui.openMainMenu(event.getPlayer()));
        SharedIndicators.registerProvider(pluginName, new SharedIndicatorProvider() {
            @Override
            public boolean showIndicator(Player player) {
                return false;
            }

            @Override
            public String getIcon(Player player) {
                return "maven-template";
            }
        });
        String version = plugin.getDescription("version");
        PluginInfoStatusProviders.registerProvider(new TemplatePluginInfoStatusProvider(pluginName, version, COMMAND));
        uiRegistrations.add(PlayerPluginSettingsOverlay.registerScopedPlayerPluginSettings(
                new TemplatePlayerPluginSettings(pluginName, version)));
        uiRegistrations.add(PlayerPluginSettingsOverlay.registerScopedPlayerPluginData(
                new TemplatePlayerPluginData(pluginName, version)));
        uiRegistrations.add(PlayerPluginSettingsOverlay.registerScopedPlayerPluginAdminSettings(
                new PlayerPluginAdminSettings(pluginName, version, settings::adminSettingsEntries, settings::initSettings)));
        MavenTemplate.logger().info("✅ " + plugin.getName() + " Plugin is enabled version:" + version);
    }

    public void disable() {
        uiRegistrations.forEach(Runnable::run);
        uiRegistrations.clear();
        InventoryOverlayButtons.unregisterButtons(pluginName);
        PluginShortcutVisibility.unregister(pluginName);
        SharedIndicators.unregisterProvider(pluginName);
        PluginInfoStatusProviders.unregisterProvider(pluginName);
    }

    public void reloadSettings(Path settingsPath) {
        settings.initSettings(settingsPath.toString());
    }

    public TemplatePlayerEventHandler events() {
        return events;
    }
}
