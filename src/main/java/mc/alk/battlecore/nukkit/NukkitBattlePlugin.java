package mc.alk.battlecore.nukkit;

import cn.nukkit.plugin.PluginDescription;
import mc.alk.battlecore.util.Log;
import mc.alk.mc.MCPlatform;
import mc.alk.nukkit.NukkitPlatform;
import mc.alk.nukkit.plugin.NukkitPlugin;

import java.io.File;

public class NukkitBattlePlugin extends NukkitPlugin {

    protected String pluginname;
    protected String version;

    @Override
    public void onEnable() {
        MCPlatform.setInstance(new NukkitPlatform());

        PluginDescription pdfFile = getDescription();
        pluginname = pdfFile.getName();
        version = pdfFile.getVersion();

        Log.setPlugin(this);
        Log.info(getVersion()+ " enabled!");
    }

    protected void createPluginFolder() {
        /// Create our plugin folder if its not there
        File dir = this.getDataFolder();
        if (!dir.exists()){
            dir.mkdirs();}
    }

    public String getVersion() {
        return "[" + pluginname + " v" + version +"]";
    }
}
