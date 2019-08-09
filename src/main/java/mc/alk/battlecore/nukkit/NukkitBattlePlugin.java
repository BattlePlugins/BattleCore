package mc.alk.battlecore.nukkit;

import cn.nukkit.plugin.PluginDescription;
import mc.alk.battlecore.util.Log;
import mc.alk.mc.MCServer;
import mc.alk.nukkit.NukkitServer;
import mc.alk.nukkit.plugin.NukkitPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class NukkitBattlePlugin extends NukkitPlugin {

    protected String pluginname;
    protected String version;

    @Override
    public void onEnable() {
        MCServer.setInstance(new NukkitServer());

        PluginDescription pdfFile = getDescription();
        pluginname = pdfFile.getName();
        version = pdfFile.getVersion();

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
