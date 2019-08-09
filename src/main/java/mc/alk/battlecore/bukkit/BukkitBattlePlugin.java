package mc.alk.battlecore.bukkit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import mc.alk.battlecore.util.Log;
import mc.alk.bukkit.BukkitServer;
import mc.alk.bukkit.plugin.BukkitPlugin;

import mc.alk.mc.MCServer;
import org.bukkit.plugin.PluginDescriptionFile;

public class BukkitBattlePlugin extends BukkitPlugin {

    private String pluginname;
    private String version;

    @Override
    public void onEnable() {
        MCServer.setInstance(new BukkitServer());
        PluginDescriptionFile pdfFile = getDescription();
        pluginname = pdfFile.getName();
        version = pdfFile.getVersion();

        Log.info(getVersion() + " enabled!");
    }

    @Override
    public void onDisable() {

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
