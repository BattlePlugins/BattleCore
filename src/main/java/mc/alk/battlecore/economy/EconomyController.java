package mc.alk.battlecore.economy;

import org.battleplugins.api.entity.living.player.OfflinePlayer;

import java.util.OptionalDouble;

public class EconomyController {

    private static EconomyHandler handler;

    public static boolean hasAccount(OfflinePlayer player) {
        return handler.hasAccount(player);
    }

    public static boolean hasAccount(OfflinePlayer player, String world) {
        return handler.hasAccount(player, world);
    }

    public static void createAccount(OfflinePlayer player) {
        handler.createAccount(player);
    }

    public static void createAccount(OfflinePlayer player, String world) {
        handler.createAccount(player, world);
    }

    public static OptionalDouble getBalance(OfflinePlayer player) {
        return handler.getBalance(player);
    }

    public static OptionalDouble getBalance(OfflinePlayer player, String world) {
        return handler.getBalance(player, world);
    }

    public static void depositBalance(OfflinePlayer player, double balance) {
        handler.depositBalance(player, balance);
    }

    public static void depositBalance(OfflinePlayer player, double balance, String world) {
        handler.depositBalance(player, balance, world);
    }

    public static void withdrawBalance(OfflinePlayer player, double balance) {
        handler.withdrawBalance(player, balance);
    }

    public static void withdrawBalance(OfflinePlayer player, double balance, String world) {
        handler.withdrawBalance(player, balance, world);
    }

    public static void setBalance(OfflinePlayer player, double balance) {
        handler.setBalance(player, balance);
    }

    public static void setBalance(OfflinePlayer player, double balance, String world) {
        handler.setBalance(player, balance, world);
    }

    public static void setEconomyHandler(EconomyHandler economyHandler) {
        handler = economyHandler;
    }
}
