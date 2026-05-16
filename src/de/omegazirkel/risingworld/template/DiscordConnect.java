package de.omegazirkel.risingworld.template;

import java.lang.reflect.Method;

import de.omegazirkel.risingworld.MavenTemplate;
import net.risingworld.api.Plugin;

// HINT: use this class if you want to support messages to discord
// it is still optional for admins to use OZ - Discord Connect or not 
// if you use this class.

// DESCRIPTION: this class uses OZ - Discord Connect plugin without 
// creating a direct dependency for this plugin.

public class DiscordConnect {

    private static Plugin pluginRef = null;
    // private static final PluginSettings s = PluginSettings.getInstance();

    public static final String botLang() {
        return (String) callPluginMethod("getBotLanguage", null, null);
    }

    public static void init(Plugin plugin) {
        pluginRef = plugin.getPluginByName("OZ - Discord Connect");
        if (pluginRef != null)
            MavenTemplate.logger().info("✅ " + pluginRef.getName() + " found! ID: " + pluginRef.getID());
        else
            MavenTemplate.logger().warn("⚠️ OZ - Discord Connect not available!");
    }

    private static boolean isAvailable() {
        try {
            Class.forName("de.omegazirkel.risingworld.DiscordConnect");
            return pluginRef != null;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private static Object callPluginMethod(String methodName, Class<?>[] paramTypes, Object[] args) {
        if (!isAvailable()) {
            return null;
        }

        try {
            Object plugin = pluginRef;
            Class<?> clazz = plugin.getClass();
            Method method = clazz.getMethod(methodName, paramTypes);
            return method.invoke(plugin, args);
        } catch (Exception e) {
            MavenTemplate.logger().error("Error while calling DiscordConnect Method");
            e.printStackTrace();
            return null;
        }
    }

    public static void sendDiscordMessage(String message, long channelId) {
        sendDiscordMessage(message, channelId, null);
    }

    public static void sendDiscordMessage(String message, long channelId, byte[] image) {
        callPluginMethod("sendDiscordMessageToTextChannel",
                new Class<?>[] { String.class, long.class, byte[].class },
                new Object[] { message, channelId, image });
    }

}
