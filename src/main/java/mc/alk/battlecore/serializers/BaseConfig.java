package mc.alk.battlecore.serializers;

import java.io.File;
import java.io.IOException;
import java.util.List;

import mc.alk.battlecore.configuration.Configuration;
import mc.alk.battlecore.configuration.ConfigurationSection;
import mc.alk.battlecore.configuration.ConfigurationType;
import mc.alk.battlecore.util.Log;
import org.yaml.snakeyaml.error.YAMLException;

public class BaseConfig {

    protected Configuration config;
    protected File file = null;

    public BaseConfig(){

    }

    public BaseConfig(File file){
        setConfig(file);
    }

    public int getInt(String node,int defaultValue) {
        return config.getInt(node, defaultValue);
    }

    public boolean getBoolean(String node, boolean defaultValue) {
        return config.getBoolean(node, false);
    }

    public double getDouble(String node, double defaultValue) {
        return config.getDouble(node, defaultValue);
    }

    public String getString(String node,String defaultValue) {
        return config.getString(node,defaultValue);
    }

    public ConfigurationSection getConfigurationSection(String node) {
        return config.getSection(node);
    }

    public Configuration getConfig() {
        return config;
    }

    public File getFile() {
        return file;
    }

    public boolean setConfig(String file){
        return setConfig(new File(file));
    }

    public boolean setConfig(File file){
        this.file = file;
        if (!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.err.println("Couldn't create the config file=" + file);
                e.printStackTrace();
                return false;
            }
        }

        try {
            config = new Configuration(file);
        } catch(YAMLException ex) {
            Log.warn(ex.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void reloadFile(){
        try {
            config.reload();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void save() {
        try {
            config.save();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public List<String> getStringList(String node) {
        return config.getStringList(node);
    }

    public void load(File file) {
        this.file = file;
        reloadFile();
    }
}
