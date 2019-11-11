package mc.alk.battlecore.bukkit.platform;

import mc.alk.battlecore.bukkit.economy.VaultEconomyHandler;
import mc.alk.battlecore.economy.EconomyController;
import mc.alk.mc.plugin.platform.PlatformCodeHandler;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

public class BattleBukkitCodeHandler extends PlatformCodeHandler {

    @Override
    public void onEnable() {
        if (Bukkit.getServer().getPluginManager().getPlugin("Vault") != null) {
            Economy economy = getEconomy();
            if (economy != null) {
                EconomyController.setEconomyHandler(new VaultEconomyHandler(economy));
            }
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    private Economy getEconomy() {
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return null;
        }
        return rsp.getProvider();
    }
}
