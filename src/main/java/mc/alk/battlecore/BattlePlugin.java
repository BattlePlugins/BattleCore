package mc.alk.battlecore;

import mc.alk.battlecore.bukkit.platform.BattleBukkitCodeHandler;
import mc.alk.battlecore.nukkit.platform.BattleNukkitCodeHandler;
import mc.alk.battlecore.sponge.platform.BattleSpongeCodeHandler;
import mc.alk.battlecore.util.Log;

import org.battleplugins.api.PlatformType;
import org.battleplugins.api.PlatformTypes;
import org.battleplugins.api.plugin.Plugin;

public class BattlePlugin extends Plugin {

    @Override
    public void onEnable() {
        PlatformType platformType = this.getPlatform().getType();
        if (platformType.equals(PlatformTypes.BUKKIT))
            platformCode.put(PlatformTypes.BUKKIT, new BattleBukkitCodeHandler());

        if (platformType.equals(PlatformTypes.NUKKIT))
            platformCode.put(PlatformTypes.NUKKIT, new BattleNukkitCodeHandler());

        if (platformType.equals(PlatformTypes.SPONGE))
            platformCode.put(PlatformTypes.SPONGE, new BattleSpongeCodeHandler(this));

        Log.setPlugin(this);
    }

    @Override
    public void onDisable() {

    }
}
