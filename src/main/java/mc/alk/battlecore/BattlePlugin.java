package mc.alk.battlecore;

import mc.alk.battlecore.bukkit.platform.BattleBukkitCodeHandler;
import mc.alk.battlecore.nukkit.platform.BattleNukkitCodeHandler;
import mc.alk.battlecore.sponge.platform.BattleSpongeCodeHandler;
import mc.alk.battlecore.util.Log;
import mc.alk.mc.APIType;
import mc.alk.mc.plugin.MCPlugin;

public class BattlePlugin extends MCPlugin {

    @Override
    public void onEnable() {
        APIType api = getPlatform().getAPIType();
        if (api == APIType.BUKKIT)
            platformCode.put(APIType.BUKKIT, new BattleBukkitCodeHandler());

        if (api == APIType.NUKKIT)
            platformCode.put(APIType.NUKKIT, new BattleNukkitCodeHandler());

        if (api == APIType.SPONGE)
            platformCode.put(APIType.SPONGE, new BattleSpongeCodeHandler());

        Log.setPlugin(this);
    }

    @Override
    public void onDisable() {

    }
}
