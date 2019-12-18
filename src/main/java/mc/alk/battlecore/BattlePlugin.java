package mc.alk.battlecore;

import mc.alk.battlecore.bukkit.platform.BattleBukkitCodeHandler;
import mc.alk.battlecore.nukkit.platform.BattleNukkitCodeHandler;
import mc.alk.battlecore.sponge.platform.BattleSpongeCodeHandler;
import mc.alk.battlecore.util.Log;
import org.battleplugins.PlatformType;
import org.battleplugins.plugin.Plugin;

public class BattlePlugin extends Plugin {

    @Override
    public void onEnable() {
        PlatformType platformType = this.getPlatform().getType();
        if (platformType == PlatformType.BUKKIT)
            platformCode.put(PlatformType.BUKKIT, new BattleBukkitCodeHandler());

        if (platformType == PlatformType.NUKKIT)
            platformCode.put(PlatformType.NUKKIT, new BattleNukkitCodeHandler());

        if (platformType == PlatformType.SPONGE)
            platformCode.put(PlatformType.SPONGE, new BattleSpongeCodeHandler(this));

        Log.setPlugin(this);
    }

    @Override
    public void onDisable() {

    }
}
