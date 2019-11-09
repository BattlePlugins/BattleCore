package mc.alk.battlecore.sponge.economy;

import com.google.common.collect.Sets;

import mc.alk.battlecore.economy.EconomyHandler;
import mc.alk.mc.MCOfflinePlayer;
import mc.alk.mc.MCPlatform;

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

    private EconomyService economyService;

    public SpongeEconomyHandler(EconomyService economyService) {
        this.economyService = economyService;
    }

    @Override
    public boolean hasAccount(MCOfflinePlayer player) {
        return economyService.hasAccount(player.getUniqueId());
    }

    @Override
    public boolean hasAccount(MCOfflinePlayer player, String world) {
        return hasAccount(player);
    }

    @Override
    public void createAccount(MCOfflinePlayer player) {
        // Creates an account if one does not exist
        economyService.getOrCreateAccount(player.getUniqueId());
    }

    @Override
    public void createAccount(MCOfflinePlayer player, String world) {
        createAccount(player);
    }

    @Override
    public OptionalDouble getBalance(MCOfflinePlayer player) {
        Optional<UniqueAccount> opAccount = getOrCreateAccount(player);
        return opAccount.map(uniqueAccount ->
                OptionalDouble.of(uniqueAccount.getBalance(economyService.getDefaultCurrency()).doubleValue()))
                .orElseGet(OptionalDouble::empty);
    }

    @Override
    public OptionalDouble getBalance(MCOfflinePlayer player, String world) {
        Optional<World> opWorld = Sponge.getServer().getWorld(world);
        if (!opWorld.isPresent())
            return getBalance(player);

        Optional<UniqueAccount> opAccount = getOrCreateAccount(player);
        return opAccount.map(uniqueAccount ->
                OptionalDouble.of(uniqueAccount.getBalance(economyService.getDefaultCurrency(), Sets.newHashSet(opWorld.get().getContext())).doubleValue()))
                .orElseGet(OptionalDouble::empty);
    }

    @Override
    public void setBalance(MCOfflinePlayer player, double balance) {
        getOrCreateAccount(player).map(uniqueAccount ->
                uniqueAccount.setBalance(economyService.getDefaultCurrency(), BigDecimal.valueOf(balance), getDefaultCause()));
    }

    @Override
    public void setBalance(MCOfflinePlayer player, double balance, String world) {
        Optional<World> opWorld = Sponge.getServer().getWorld(world);
        if (!opWorld.isPresent()) {
            setBalance(player, balance);
            return;
        }
        getOrCreateAccount(player).map(uniqueAccount ->
                uniqueAccount.setBalance(economyService.getDefaultCurrency(), BigDecimal.valueOf(balance), getDefaultCause(), Sets.newHashSet(opWorld.get().getContext())));
    }

    public Optional<UniqueAccount> getOrCreateAccount(MCOfflinePlayer player) {
        return economyService.getOrCreateAccount(player.getUniqueId());
    }

    private Cause getDefaultCause() {
        return Sponge.getPluginManager().fromInstance(MCPlatform.getPluginManager().getPlugin())
                .map(pluginContainer ->
                Cause.of(EventContext
                        .builder()
                        .add(EventContextKeys.PLUGIN, pluginContainer)
                        .build(), pluginContainer)).get();
    }
}
