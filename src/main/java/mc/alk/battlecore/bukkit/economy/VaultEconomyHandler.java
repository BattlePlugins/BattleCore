package mc.alk.battlecore.bukkit.economy;

import mc.alk.battlecore.economy.EconomyHandler;
import mc.alk.bukkit.BukkitOfflinePlayer;
import mc.alk.mc.MCOfflinePlayer;

import net.milkbowl.vault.economy.Economy;

import java.util.OptionalDouble;

public class VaultEconomyHandler implements EconomyHandler {

    private Economy economy;

    public VaultEconomyHandler(Economy economy) {
        this.economy = economy;
    }

    @Override
    public boolean hasAccount(MCOfflinePlayer player) {
        return economy.hasAccount(((BukkitOfflinePlayer) player).getHandle());
    }

    @Override
    public boolean hasAccount(MCOfflinePlayer player, String world) {
        return economy.hasAccount(((BukkitOfflinePlayer) player).getHandle(), world);
    }

    @Override
    public void createAccount(MCOfflinePlayer player) {
        economy.createPlayerAccount(((BukkitOfflinePlayer) player).getHandle());
    }

    @Override
    public void createAccount(MCOfflinePlayer player, String world) {
        economy.createPlayerAccount(((BukkitOfflinePlayer) player).getHandle(), world);
    }

    @Override
    public OptionalDouble getBalance(MCOfflinePlayer player) {
        if (!hasAccount(player))
            return OptionalDouble.empty();

        return OptionalDouble.of(economy.getBalance(((BukkitOfflinePlayer) player).getHandle()));
    }

    @Override
    public OptionalDouble getBalance(MCOfflinePlayer player, String world) {
        if (!hasAccount(player, world))
            return OptionalDouble.empty();

        return OptionalDouble.of(economy.getBalance(((BukkitOfflinePlayer) player).getHandle(), world));
    }

    @Override
    public void setBalance(MCOfflinePlayer player, double balance) {
        // Withdraw current amount so account is empty
        economy.withdrawPlayer(((BukkitOfflinePlayer) player).getHandle(), getBalance(player).orElse(0));
        economy.depositPlayer(((BukkitOfflinePlayer) player).getHandle(), balance);
    }

    @Override
    public void setBalance(MCOfflinePlayer player, double balance, String world) {
        // Withdraw current amount so account is empty
        economy.withdrawPlayer(((BukkitOfflinePlayer) player).getHandle(), world, getBalance(player).orElse(0));
        economy.depositPlayer(((BukkitOfflinePlayer) player).getHandle(), world, balance);
    }
}
