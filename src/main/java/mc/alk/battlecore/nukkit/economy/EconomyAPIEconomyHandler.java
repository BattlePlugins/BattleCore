package mc.alk.battlecore.nukkit.economy;

import me.onebone.economyapi.EconomyAPI;

import mc.alk.battlecore.economy.EconomyHandler;
import mc.alk.mc.MCOfflinePlayer;

import java.util.OptionalDouble;

public class EconomyAPIEconomyHandler implements EconomyHandler {

    private EconomyAPI economyAPI;

    public EconomyAPIEconomyHandler(EconomyAPI economyAPI) {
        this.economyAPI = economyAPI;
    }

    @Override
    public boolean hasAccount(MCOfflinePlayer player) {
        return economyAPI.hasAccount(player.getUniqueId());
    }

    @Override
    public boolean hasAccount(MCOfflinePlayer player, String world) {
        return hasAccount(player);
    }

    @Override
    public void createAccount(MCOfflinePlayer player) {
        economyAPI.createAccount(player.getUniqueId(), 0);
    }

    @Override
    public void createAccount(MCOfflinePlayer player, String world) {
        createAccount(player);
    }

    @Override
    public OptionalDouble getBalance(MCOfflinePlayer player) {
        return OptionalDouble.of(economyAPI.myMoney(player.getUniqueId()));
    }

    @Override
    public OptionalDouble getBalance(MCOfflinePlayer player, String world) {
        return getBalance(player);
    }

    @Override
    public void setBalance(MCOfflinePlayer player, double balance) {
        economyAPI.setMoney(player.getUniqueId(), balance);
    }

    @Override
    public void setBalance(MCOfflinePlayer player, double balance, String world) {
        setBalance(player, balance);
    }
}
