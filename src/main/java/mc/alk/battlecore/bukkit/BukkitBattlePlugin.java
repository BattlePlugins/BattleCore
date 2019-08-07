package mc.alk.battlecore.bukkit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import mc.alk.battlecore.util.Log;
import mc.alk.bukkit.plugin.BukkitPlugin;

import org.bukkit.plugin.PluginDescriptionFile;

public class BukkitBattlePlugin extends BukkitPlugin {

    private String pluginname;
    private String version;

    @Override
    public void onEnable() {
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

    public File load(String default_file, String config_file) {
        File file = new File(config_file);
        if (!file.exists()){ /// Create a new file from our default example
            try{
                InputStream inputStream = getClass().getResourceAsStream(default_file);
                OutputStream out=new FileOutputStream(config_file);
                byte buf[]=new byte[1024];
                int len;
                while((len=inputStream.read(buf))>0){
                    out.write(buf,0,len);}
                out.close();
                inputStream.close();
            } catch (Exception e){
            }
        }
        return file;
    }
}
