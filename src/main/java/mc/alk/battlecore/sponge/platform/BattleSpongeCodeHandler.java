package mc.alk.battlecore.sponge.platform;

import mc.alk.battlecore.economy.EconomyController;
import mc.alk.battlecore.sponge.economy.SpongeEconomyHandler;

import org.battleplugins.plugin.Plugin;
import org.battleplugins.plugin.platform.PlatformCodeHandler;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.ProviderRegistration;
import org.spongepowered.api.service.economy.EconomyService;

public class BattleSpongeCodeHandler extends PlatformCodeHandler {

    private Plugin plugin;

    public BattleSpongeCodeHandler(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onEnable() {
        if (Sponge.getServiceManager().getRegistration(EconomyService.class).isPresent()) {
            ProviderRegistration<EconomyService> economyService = Sponge.getServiceManager().getRegistration(EconomyService.class).get();
            EconomyController.setEconomyHandler(new SpongeEconomyHandler(plugin, economyService.getProvider()));
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}
