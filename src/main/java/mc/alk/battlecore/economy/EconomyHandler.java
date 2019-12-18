package mc.alk.battlecore.economy;

import org.battleplugins.entity.living.player.OfflinePlayer;

import java.util.OptionalDouble;

public interface EconomyHandler {

    boolean hasAccount(OfflinePlayer player);
    boolean hasAccount(OfflinePlayer player, String world);
    void createAccount(OfflinePlayer player);
    void createAccount(OfflinePlayer player, String world);

    OptionalDouble getBalance(OfflinePlayer player);
    OptionalDouble getBalance(OfflinePlayer player, String world);

    default void depositBalance(OfflinePlayer player, double balance) {
        setBalance(player, getBalance(player).orElse(0) + balance);
    }

    default void depositBalance(OfflinePlayer player, double balance, String world) {
        setBalance(player, getBalance(player).orElse(0) + balance, world);
    }

    default void withdrawBalance(OfflinePlayer player, double balance) {
        setBalance(player, getBalance(player).orElse(0) - balance);
    }

    default void withdrawBalance(OfflinePlayer player, double balance, String world) {
        setBalance(player, getBalance(player).orElse(0) - balance, world);
    }

    void setBalance(OfflinePlayer player, double balance);
    void setBalance(OfflinePlayer player, double balance, String world);
}
