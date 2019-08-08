package mc.alk.battlecore.sponge;

import mc.alk.mc.MCServer;
import mc.alk.sponge.SpongeServer;
import mc.alk.sponge.plugin.SpongePlugin;

public abstract class SpongeBattlePlugin extends SpongePlugin {

    @Override
    public void onEnable() {
        MCServer.setInstance(new SpongeServer());
    }
}
