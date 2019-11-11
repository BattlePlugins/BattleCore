package mc.alk.battlecore.economy;

import mc.alk.mc.MCOfflinePlayer;

import java.util.OptionalDouble;

public class EconomyController {

    private static EconomyHandler handler;

    public static boolean hasAccount(MCOfflinePlayer player) {
        return handler.hasAccount(player);
    }

    public static boolean hasAccount(MCOfflinePlayer player, String world) {
        return handler.hasAccount(player, world);
    }

    public static void createAccount(MCOfflinePlayer player) {
        handler.createAccount(player);
    }

    public static void createAccount(MCOfflinePlayer player, String world) {
        handler.createAccount(player, world);
    }

    public static OptionalDouble getBalance(MCOfflinePlayer player) {
        return handler.getBalance(player);
    }

    public static OptionalDouble getBalance(MCOfflinePlayer player, String world) {
        return handler.getBalance(player, world);
    }

    public static void depositBalance(MCOfflinePlayer player, double balance) {
        handler.depositBalance(player, balance);
    }

    public static void depositBalance(MCOfflinePlayer player, double balance, String world) {
        handler.depositBalance(player, balance, world);
    }

    public static void withdrawBalance(MCOfflinePlayer player, double balance) {
        handler.withdrawBalance(player, balance);
    }

    public static void withdrawBalance(MCOfflinePlayer player, double balance, String world) {
        handler.withdrawBalance(player, balance, world);
    }

    public static void setBalance(MCOfflinePlayer player, double balance) {
        handler.setBalance(player, balance);
    }

    public static void setBalance(MCOfflinePlayer player, double balance, String world) {
        handler.setBalance(player, balance, world);
    }

    public static void setEconomyHandler(EconomyHandler economyHandler) {
        handler = economyHandler;
    }
}
