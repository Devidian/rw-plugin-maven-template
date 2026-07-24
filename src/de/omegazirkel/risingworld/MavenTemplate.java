package de.omegazirkel.risingworld;

import java.nio.file.Path;

import de.omegazirkel.risingworld.template.runtime.TemplatePluginRuntime;
import de.omegazirkel.risingworld.tools.FileChangeListener;
import de.omegazirkel.risingworld.tools.OZLogger;
import net.risingworld.api.Plugin;
import net.risingworld.api.events.EventMethod;
import net.risingworld.api.events.Listener;
import net.risingworld.api.events.player.PlayerCommandEvent;
import net.risingworld.api.events.player.PlayerSpawnEvent;

/**
 * Rising World entry point. This is intentionally the plugin's only event
 * listener; all runtime and feature logic is delegated below {@code template}.
 */
public class MavenTemplate extends Plugin implements Listener, FileChangeListener {
    private static final String LOGGER_NAME = "MavenTemplate";

    private TemplatePluginRuntime runtime;

    public static OZLogger logger() {
        return OZLogger.getInstance(LOGGER_NAME);
    }

    @Override
    public void onEnable() {
        runtime = new TemplatePluginRuntime(this);
        runtime.enable();
        registerEventListener(this);
    }

    @Override
    public void onDisable() {
        if (runtime != null) {
            runtime.disable();
        }
    }

    @Override
    public void onSettingsChanged(Path settingsPath) {
        runtime.reloadSettings(settingsPath);
    }

    @EventMethod
    public void onPlayerCommand(PlayerCommandEvent event) {
        runtime.events().onPlayerCommand(event);
    }

    @EventMethod
    public void onPlayerSpawnEvent(PlayerSpawnEvent event) {
        runtime.events().onPlayerSpawn(event);
    }
}
