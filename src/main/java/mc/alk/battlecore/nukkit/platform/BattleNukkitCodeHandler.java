package mc.alk.battlecore.nukkit.platform;

import me.onebone.economyapi.EconomyAPI;

import cn.nukkit.Server;

import mc.alk.battlecore.economy.EconomyController;
import mc.alk.battlecore.nukkit.economy.EconomyAPIEconomyHandler;

import org.battleplugins.api.plugin.platform.PlatformCodeHandler;

public class BattleNukkitCodeHandler extends PlatformCodeHandler {

    @Override
    public void onEnable() {
        if (Server.getInstance().getPluginManager().getPlugin("EconomyAPI") != null) {
            EconomyController.setEconomyHandler(new EconomyAPIEconomyHandler(EconomyAPI.getInstance()));
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}
