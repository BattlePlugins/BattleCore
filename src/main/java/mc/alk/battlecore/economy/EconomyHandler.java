package mc.alk.battlecore.economy;

import mc.alk.mc.MCOfflinePlayer;

import java.util.OptionalDouble;

public interface EconomyHandler {

    boolean hasAccount(MCOfflinePlayer player);
    boolean hasAccount(MCOfflinePlayer player, String world);
    void createAccount(MCOfflinePlayer player);
    void createAccount(MCOfflinePlayer player, String world);

    OptionalDouble getBalance(MCOfflinePlayer player);
    OptionalDouble getBalance(MCOfflinePlayer player, String world);

    default void depositBalance(MCOfflinePlayer player, double balance) {
        setBalance(player, getBalance(player).orElse(0) + balance);
    }

    default void depositBalance(MCOfflinePlayer player, double balance, String world) {
        setBalance(player, getBalance(player).orElse(0) + balance, world);
    }

    default void withdrawBalance(MCOfflinePlayer player, double balance) {
        setBalance(player, getBalance(player).orElse(0) - balance);
    }

    default void withdrawBalance(MCOfflinePlayer player, double balance, String world) {
        setBalance(player, getBalance(player).orElse(0) - balance, world);
    }

    void setBalance(MCOfflinePlayer player, double balance);
    void setBalance(MCOfflinePlayer player, double balance, String world);
}
