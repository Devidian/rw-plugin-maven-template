package de.omegazirkel.risingworld.template;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import net.risingworld.api.Plugin;

public final class WalletBridge {
    private final Plugin owner;
    private Plugin walletPlugin;

    public WalletBridge(Plugin owner) {
        this.owner = owner;
        refresh();
    }

    public void refresh() {
        walletPlugin = owner == null ? null : owner.getPluginByName("OZ - Wallet");
    }

    public boolean isAvailable() {
        try {
            Class.forName("de.omegazirkel.risingworld.Wallet");
            if (walletPlugin == null) {
                refresh();
            }
            return walletPlugin != null && databaseAvailable();
        } catch (ClassNotFoundException ex) {
            return false;
        }
    }

    public boolean databaseAvailable() {
        Object value = callWalletMethod("databaseAvailable", new Class<?>[] {}, new Object[] {});
        return value instanceof Boolean && (Boolean) value;
    }

    public String defaultCurrencyIdentifier() {
        Object value = callWalletMethod("defaultCurrencyIdentifier", new Class<?>[] {}, new Object[] {});
        return value instanceof String ? (String) value : "";
    }

    public WalletCallResult registerCurrency(String identifier, String name, String icon, String pluginIdentifier) {
        return WalletCallResult.from(callWalletMethod(
                "registerCurrency",
                new Class<?>[] { String.class, String.class, String.class, String.class },
                new Object[] { identifier, name, icon, pluginIdentifier }));
    }

    public WalletCallResult deposit(
            int playerDbId,
            long value,
            String reason,
            String currencyIdentifier,
            String pluginIdentifier) {
        if (currencyIdentifier == null || currencyIdentifier.isBlank()) {
            return depositDefault(playerDbId, value, reason, pluginIdentifier);
        }
        return WalletCallResult.from(callWalletMethod(
                "deposit",
                new Class<?>[] { int.class, long.class, String.class, String.class, String.class },
                new Object[] { playerDbId, value, reason, currencyIdentifier, pluginIdentifier }));
    }

    public WalletCallResult withdraw(
            int playerDbId,
            long value,
            String reason,
            String currencyIdentifier,
            String pluginIdentifier) {
        if (currencyIdentifier == null || currencyIdentifier.isBlank()) {
            return withdrawDefault(playerDbId, value, reason, pluginIdentifier);
        }
        return WalletCallResult.from(callWalletMethod(
                "withdraw",
                new Class<?>[] { int.class, long.class, String.class, String.class, String.class },
                new Object[] { playerDbId, value, reason, currencyIdentifier, pluginIdentifier }));
    }

    /** Wallet v1 atomic, idempotent transfer for cross-plugin sagas. */
    public WalletTransferCallResult transferIdempotent(int payerDbId, int payeeDbId, long value, String reason,
            String currencyIdentifier, String pluginIdentifier, String correlationId) {
        return WalletTransferCallResult.from(callWalletMethod("transferIdempotent",
                new Class<?>[] { int.class, int.class, long.class, String.class, String.class, String.class, String.class },
                new Object[] { payerDbId, payeeDbId, value, reason, currencyIdentifier, pluginIdentifier, correlationId }));
    }

    public WalletCallResult balance(int playerDbId, String currencyIdentifier) {
        if (currencyIdentifier == null || currencyIdentifier.isBlank()) {
            return balanceDefault(playerDbId);
        }
        return WalletCallResult.from(callWalletMethod(
                "balance",
                new Class<?>[] { int.class, String.class },
                new Object[] { playerDbId, currencyIdentifier }));
    }

    public WalletCallResult depositDefault(int playerDbId, long value, String reason, String pluginIdentifier) {
        return WalletCallResult.from(callWalletMethod(
                "depositDefault",
                new Class<?>[] { int.class, long.class, String.class, String.class },
                new Object[] { playerDbId, value, reason, pluginIdentifier }));
    }

    public WalletCallResult withdrawDefault(int playerDbId, long value, String reason, String pluginIdentifier) {
        return WalletCallResult.from(callWalletMethod(
                "withdrawDefault",
                new Class<?>[] { int.class, long.class, String.class, String.class },
                new Object[] { playerDbId, value, reason, pluginIdentifier }));
    }

    public WalletCallResult balanceDefault(int playerDbId) {
        return WalletCallResult.from(callWalletMethod(
                "balanceDefault",
                new Class<?>[] { int.class },
                new Object[] { playerDbId }));
    }

    public WalletCallResult defaultCurrency() {
        return WalletCallResult.from(callWalletMethod("defaultCurrency", new Class<?>[] {}, new Object[] {}));
    }

    public List<CurrencyInfo> listCurrencies() {
        Object result = callWalletMethod("listCurrencies", new Class<?>[] {}, new Object[] {});
        if (!WalletCallResult.from(result).success()) {
            return List.of();
        }
        Object currencies = getResultField(result, "currencies");
        if (!(currencies instanceof Iterable<?> iterable)) {
            return List.of();
        }

        List<CurrencyInfo> infos = new ArrayList<>();
        for (Object currency : iterable) {
            CurrencyInfo info = CurrencyInfo.from(currency);
            if (info != null) {
                infos.add(info);
            }
        }
        return List.copyOf(infos);
    }

    private Object callWalletMethod(String methodName, Class<?>[] paramTypes, Object[] args) {
        if (walletPlugin == null) {
            refresh();
        }
        if (walletPlugin == null) {
            return null;
        }

        try {
            Method method = walletPlugin.getClass().getMethod(methodName, paramTypes);
            return method.invoke(walletPlugin, args);
        } catch (Exception ex) {
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
        } catch (Exception ex) {
            return null;
        }
    }

    private static Object callGetter(Object target, String getter) {
        if (target == null) {
            return null;
        }
        try {
            Method method = target.getClass().getMethod(getter);
            return method.invoke(target);
        } catch (Exception ex) {
            return null;
        }
    }

    public record WalletCallResult(boolean success, String message) {
        static WalletCallResult from(Object result) {
            Object success = getResultField(result, "success");
            Object message = getResultField(result, "message");
            return new WalletCallResult(success instanceof Boolean && (Boolean) success,
                    message instanceof String ? (String) message : "");
        }
    }

    public record WalletTransferCallResult(boolean success, String errorCode, String message) {
        static WalletTransferCallResult from(Object result) {
            Object success = getResultField(result, "success");
            Object errorCode = getResultField(result, "errorCode");
            Object message = getResultField(result, "message");
            return new WalletTransferCallResult(Boolean.TRUE.equals(success),
                    errorCode == null ? "" : String.valueOf(errorCode), message instanceof String ? (String) message : "");
        }
    }

    public record CurrencyInfo(
            String identifier,
            String name,
            String iconKey,
            String pluginIdentifier,
            long registeredAt,
            boolean defaultCurrency) {
        static CurrencyInfo from(Object currency) {
            Object identifier = callGetter(currency, "getIdentifier");
            if (!(identifier instanceof String)) {
                return null;
            }
            Object name = callGetter(currency, "getName");
            Object iconKey = callGetter(currency, "getIconKey");
            Object pluginIdentifier = callGetter(currency, "getPluginIdentifier");
            Object registeredAt = callGetter(currency, "getRegisteredAt");
            Object defaultCurrency = callGetter(currency, "isDefaultCurrency");
            return new CurrencyInfo(
                    (String) identifier,
                    name instanceof String ? (String) name : "",
                    iconKey instanceof String ? (String) iconKey : "",
                    pluginIdentifier instanceof String ? (String) pluginIdentifier : "",
                    registeredAt instanceof Long ? (Long) registeredAt : 0L,
                    defaultCurrency instanceof Boolean && (Boolean) defaultCurrency);
        }
    }
}
