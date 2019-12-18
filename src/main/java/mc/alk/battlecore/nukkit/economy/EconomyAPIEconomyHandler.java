package mc.alk.battlecore.nukkit.economy;

import me.onebone.economyapi.EconomyAPI;

import mc.alk.battlecore.economy.EconomyHandler;

import org.battleplugins.entity.living.player.OfflinePlayer;

import java.util.OptionalDouble;

public class EconomyAPIEconomyHandler implements EconomyHandler {

    private EconomyAPI economyAPI;

    public EconomyAPIEconomyHandler(EconomyAPI economyAPI) {
        this.economyAPI = economyAPI;
    }

    @Override
    public boolean hasAccount(OfflinePlayer player) {
        return economyAPI.hasAccount(player.getUniqueId());
    }

    @Override
    public boolean hasAccount(OfflinePlayer player, String world) {
        return hasAccount(player);
    }

    @Override
    public void createAccount(OfflinePlayer player) {
        economyAPI.createAccount(player.getUniqueId(), 0);
    }

    @Override
    public void createAccount(OfflinePlayer player, String world) {
        createAccount(player);
    }

    @Override
    public OptionalDouble getBalance(OfflinePlayer player) {
        return OptionalDouble.of(economyAPI.myMoney(player.getUniqueId()));
    }

    @Override
    public OptionalDouble getBalance(OfflinePlayer player, String world) {
        return getBalance(player);
    }

    @Override
    public void setBalance(OfflinePlayer player, double balance) {
        economyAPI.setMoney(player.getUniqueId(), balance);
    }

    @Override
    public void setBalance(OfflinePlayer player, double balance, String world) {
        setBalance(player, balance);
    }
}
