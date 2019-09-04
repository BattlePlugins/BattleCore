package mc.alk.battlecore.sponge;

import mc.alk.battlecore.util.Log;
import mc.alk.mc.MCPlatform;
import mc.alk.sponge.SpongePlatform;
import mc.alk.sponge.plugin.SpongePlugin;

public abstract class SpongeBattlePlugin extends SpongePlugin {

    @Override
    public void onEnable() {
        MCPlatform.setInstance(new SpongePlatform());
        Log.setPlugin(this);
    }
}
