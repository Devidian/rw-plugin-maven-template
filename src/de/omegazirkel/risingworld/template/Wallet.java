package de.omegazirkel.risingworld.template;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import net.risingworld.api.Plugin;

// HINT: use this class if you want to support any kind of currency
// in your plugin.

// DESCRIPTION: this class uses OZ - Wallet plugin without 
// creating a direct dependency for this plugin. 
// This way use of currency is optional.

public final class Wallet {

    private static Plugin walletPlugin;

    private Wallet() {
    }

    public static void init(Plugin plugin) {
        walletPlugin = plugin.getPluginByName("OZ - Wallet");
    }

    public static boolean isAvailable() {
        try {
            Class.forName("de.omegazirkel.risingworld.Wallet");
            return walletPlugin != null;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static Object registerCurrency(
            String currencyIdentifier,
            String name,
            String icon,
            String pluginIdentifier) {
        return callWalletMethod(
                "registerCurrency",
                new Class<?>[] { String.class, String.class, String.class, String.class },
                new Object[] { currencyIdentifier, name, icon, pluginIdentifier });
    }

    public static Object deposit(
            int playerDbId,
            long value,
            String reason,
            String currencyIdentifier,
            String pluginIdentifier) {
        return callWalletMethod(
                "deposit",
                new Class<?>[] { int.class, long.class, String.class, String.class, String.class },
                new Object[] { playerDbId, value, reason, currencyIdentifier, pluginIdentifier });
    }

    public static Object withdraw(
            int playerDbId,
            long value,
            String reason,
            String currencyIdentifier,
            String pluginIdentifier) {
        return callWalletMethod(
                "withdraw",
                new Class<?>[] { int.class, long.class, String.class, String.class, String.class },
                new Object[] { playerDbId, value, reason, currencyIdentifier, pluginIdentifier });
    }

    public static Object balance(int playerDbId, String currencyIdentifier) {
        return callWalletMethod(
                "balance",
                new Class<?>[] { int.class, String.class },
                new Object[] { playerDbId, currencyIdentifier });
    }

    public static Object depositDefault(
            int playerDbId,
            long value,
            String reason,
            String pluginIdentifier) {
        return callWalletMethod(
                "depositDefault",
                new Class<?>[] { int.class, long.class, String.class, String.class },
                new Object[] { playerDbId, value, reason, pluginIdentifier });
    }

    public static boolean isSuccess(Object result) {
        Object success = getResultField(result, "success");
        return success instanceof Boolean && (Boolean) success;
    }

    public static String message(Object result) {
        Object message = getResultField(result, "message");
        return message instanceof String ? (String) message : "";
    }

    private static Object callWalletMethod(String methodName, Class<?>[] paramTypes, Object[] args) {
        if (!isAvailable()) {
            return null;
        }

        try {
            Method method = walletPlugin.getClass().getMethod(methodName, paramTypes);
            return method.invoke(walletPlugin, args);
        } catch (Exception e) {
            return null;
        }
    }

    private static Object getResultField(Object result, String fieldName) {
        if (result == null) {
            return null;
        }

        try {
            Field field = result.getClass().getField(fieldName);
            return field.get(result);
        } catch (Exception e) {
            return null;
        }
    }
}