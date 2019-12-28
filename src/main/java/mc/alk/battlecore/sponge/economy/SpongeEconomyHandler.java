package mc.alk.battlecore.sponge.economy;

import com.google.common.collect.Sets;

import mc.alk.battlecore.economy.EconomyHandler;

import org.battleplugins.api.entity.living.player.OfflinePlayer;
import org.battleplugins.api.plugin.Plugin;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.world.World;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.OptionalDouble;

public class SpongeEconomyHandler implements EconomyHandler {

    private Plugin plugin;
    private EconomyService economyService;

    public SpongeEconomyHandler(Plugin plugin, EconomyService economyService) {
        this.plugin = plugin;
        this.economyService = economyService;
    }

    @Override
    public boolean hasAccount(OfflinePlayer player) {
        return economyService.hasAccount(player.getUniqueId());
    }

    @Override
    public boolean hasAccount(OfflinePlayer player, String world) {
        return hasAccount(player);
    }

    @Override
    public void createAccount(OfflinePlayer player) {
        // Creates an account if one does not exist
        economyService.getOrCreateAccount(player.getUniqueId());
    }

    @Override
    public void createAccount(OfflinePlayer player, String world) {
        createAccount(player);
    }

    @Override
    public OptionalDouble getBalance(OfflinePlayer player) {
        Optional<UniqueAccount> opAccount = getOrCreateAccount(player);
        return opAccount.map(uniqueAccount ->
                OptionalDouble.of(uniqueAccount.getBalance(economyService.getDefaultCurrency()).doubleValue()))
                .orElseGet(OptionalDouble::empty);
    }

    @Override
    public OptionalDouble getBalance(OfflinePlayer player, String world) {
        Optional<World> opWorld = Sponge.getServer().getWorld(world);
        if (!opWorld.isPresent())
            return getBalance(player);

        Optional<UniqueAccount> opAccount = getOrCreateAccount(player);
        return opAccount.map(uniqueAccount ->
                OptionalDouble.of(uniqueAccount.getBalance(economyService.getDefaultCurrency(), Sets.newHashSet(opWorld.get().getContext())).doubleValue()))
                .orElseGet(OptionalDouble::empty);
    }

    @Override
    public void setBalance(OfflinePlayer player, double balance) {
        getOrCreateAccount(player).map(uniqueAccount ->
                uniqueAccount.setBalance(economyService.getDefaultCurrency(), BigDecimal.valueOf(balance), getDefaultCause()));
    }

    @Override
    public void setBalance(OfflinePlayer player, double balance, String world) {
        Optional<World> opWorld = Sponge.getServer().getWorld(world);
        if (!opWorld.isPresent()) {
            setBalance(player, balance);
            return;
        }
        getOrCreateAccount(player).map(uniqueAccount ->
                uniqueAccount.setBalance(economyService.getDefaultCurrency(), BigDecimal.valueOf(balance), getDefaultCause(), Sets.newHashSet(opWorld.get().getContext())));
    }

    public Optional<UniqueAccount> getOrCreateAccount(OfflinePlayer player) {
        return economyService.getOrCreateAccount(player.getUniqueId());
    }

    private Cause getDefaultCause() {
        return Sponge.getPluginManager().fromInstance(plugin.getPlatformPlugin())
                .map(pluginContainer ->
                Cause.of(EventContext
                        .builder()
                        .add(EventContextKeys.PLUGIN, pluginContainer)
                        .build(), pluginContainer)).get();
    }
}
