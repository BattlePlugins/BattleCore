package mc.alk.battlecore.configuration;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import mc.alk.battlecore.util.FileUtil;
import mc.alk.battlecore.util.Log;
import mc.alk.mc.Scheduler;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Pattern;

/**
 * Configuration API adapted from Bukkit & Nukkit
 */
public class Configuration {

    private ConfigurationType type;

    private ConfigurationSection section;
    private File file;

    private boolean valid;

    public static final Map<String, Integer> FORMAT = new TreeMap<>();

    public Configuration() {
        this(ConfigurationType.YAML);
    }

    public Configuration(ConfigurationType type) {
        this.type = type;
        this.section = new ConfigurationSection();
    }

    public Configuration(String file) {
        this(file, ConfigurationType.DETECT);
    }

    public Configuration(File file) {
        this(file.toString(), ConfigurationType.DETECT);
    }

    public Configuration(String file, ConfigurationType type) {
        this(file, type, new ConfigurationSection());
    }

    public Configuration(File file, ConfigurationType type,  LinkedHashMap<String, Object> defaultMap) {
        this.load(file.toString(), type, new ConfigurationSection(defaultMap));
    }

    public Configuration(String file, ConfigurationType type,  LinkedHashMap<String, Object> defaultMap) {
        this.load(file, type, new ConfigurationSection(defaultMap));
    }

    public Configuration(File file, ConfigurationType type,  ConfigurationSection section) {
        this.load(file.toString(), type, section);
    }

    public Configuration(String file, ConfigurationType type,  ConfigurationSection section) {
        this.load(file, type, section);
    }

    public void reload() {
        section.clear();
        valid = false;
        if (file == null) {
            throw new IllegalStateException("Failed to reload config. File is undefined!");
        }

        this.load(file.toString(), type);
    }

    public boolean load(String file) {
        return this.load(file, ConfigurationType.DETECT);
    }

    public boolean load(String file, ConfigurationType type) {
        return this.load(file, type, new ConfigurationSection());
    }

    public boolean load(String file, ConfigurationType type, ConfigurationSection section) {
        this.valid = true;
        this.type = type;
        this.file = new File(file);
        if (!this.file.exists()) {
            try {
                this.file.getParentFile().mkdirs();
                this.file.createNewFile();
            } catch (IOException ex) {
                Log.err("Could not create configuration file " + file);
                ex.printStackTrace();
            }

            this.section = section;
            save();
        } else {
            if (type == ConfigurationType.DETECT) {
                String extension = "";
                if (this.file.getName().lastIndexOf(".") != -1 && this.file.getName().lastIndexOf(".") != 0) {
                    extension = this.file.getName().substring(this.file.getName().lastIndexOf(".") + 1);
                }

                for (ConfigurationType configType : ConfigurationType.values()) {
                    if (configType == ConfigurationType.DETECT)
                        continue;

                    if (configType.getExtensions().contains(extension)) {
                        this.type = configType;
                        break;
                    }
                }

                if (this.type == null || this.type == ConfigurationType.DETECT)
                    valid = false;
            }

            if (valid) {
                String content = "";
                try {
                    content = FileUtil.readFile(this.file);
                } catch (IOException ex) {
                    Log.err("Could not load file " + this.file.getName());
                    ex.printStackTrace();
                }

                parseContent(content);
                if (!valid)
                    return false;

                if (setDefault(section) > 0)
                    save();
            } else {
                return false;
            }
        }

        return true;
    }

    public boolean load(InputStream stream) {
        if (stream == null)
            return false;

        if (valid) {
            String content;
            try {
                content = FileUtil.readFile(stream);
            } catch (IOException ex) {
                Log.err("Could not load file " + this.file.getName());
                ex.printStackTrace();
                return false;
            }

            parseContent(content);
        }

        return true;
    }

    public boolean isValid() {
        return valid;
    }

    public boolean save(File file, boolean async) {
        this.file = file;
        return save(async);
    }

    public boolean save(File file) {
        this.file = file;
        return save();
    }

    public boolean save() {
        return save(false);
    }

    public boolean save(boolean async) {
        if (this.file == null)
            throw new IllegalStateException("Failed to reload config. File is undefined!");

        if (!valid)
            return false;

        String content = "";
        switch (type) {
            case PROPERTIES:
                content = writeProperties();
                break;
            case JSON:
                content = new GsonBuilder().setPrettyPrinting().create().toJson(section);
                break;
            case YAML:
                DumperOptions dumperOptions = new DumperOptions();
                dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
                Yaml yaml = new Yaml(dumperOptions);
                content = yaml.dump(section);
                break;
            case ENUM:
            case CNF:
                for (Object object : section.entrySet()) {
                    Map.Entry entry = (Map.Entry) object;
                    content += entry.getKey() + "\r\n";
                }
                break;
        }

        if (async) {
            final String finalContent = content;
            Scheduler.scheduleAsynchrounousTask(() -> {
                try {
                    FileUtil.writeFile(this.file, finalContent);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
        } else {
            try {
                FileUtil.writeFile(this.file, content);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        return true;
    }

    public void set(String key, Object value) {
        section.set(key, value);
    }

    public Object get(String key) {
        return get(key, null);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key, T defaultValue) {
        return valid ? section.get(key, defaultValue) : defaultValue;
    }

    public ConfigurationSection getSection(String key) {
        return valid ? section.getSection(key) : new ConfigurationSection();
    }

    public boolean isSection(String key) {
        return section.isSection(key);
    }

    public ConfigurationSection getSections() {
        return valid ? section.getSections() : new ConfigurationSection();
    }

    public ConfigurationSection getSections(String key) {
        return valid ? section.getSections(key) : new ConfigurationSection();
    }

    public int getInt(String key) {
        return getInt(key, 0);
    }

    public int getInt(String key, int defaultValue) {
        return valid ? section.getInt(key, defaultValue) : defaultValue;
    }

    public boolean isInt(String key) {
        return section.isInt(key);
    }

    public long getLong(String key) {
        return getLong(key, 0);
    }

    public long getLong(String key, long defaultValue) {
        return valid ? section.getLong(key, defaultValue) : defaultValue;
    }

    public boolean isLong(String key) {
        return section.isLong(key);
    }

    public double getDouble(String key) {
        return getDouble(key, 0);
    }

    public double getDouble(String key, double defaultValue) {
        return valid ? section.getDouble(key, defaultValue) : defaultValue;
    }

    public boolean isDouble(String key) {
        return section.isDouble(key);
    }

    public float getFloat(String key) {
        return getFloat(key, 0);
    }

    public float getFloat(String key, float defaultValue) {
        return valid ? section.get(key, ((Number) defaultValue)).floatValue() : defaultValue;
    }

    public boolean isFloat(String key) {
        return section.get(key) instanceof Float;
    }

    public String getString(String key) {
        return getString(key, "");
    }

    public String getString(String key, String defaultValue) {
        return valid ? section.getString(key, defaultValue) : defaultValue;
    }

    public boolean isString(String key) {
        return section.isString(key);
    }

    public boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return valid ? section.getBoolean(key, defaultValue) : defaultValue;
    }

    public boolean isBoolean(String key) {
        return section.isBoolean(key);
    }

    public List getList(String key) {
        return getList(key, null);
    }

    public List getList(String key, List defaultList) {
        return valid ? section.getList(key, defaultList) : defaultList;
    }

    public boolean isList(String key) {
        return section.isList(key);
    }

    public List<String> getStringList(String key) {
        return section.getStringList(key);
    }

    public List<Integer> getIntegerList(String key) {
        return section.getIntegerList(key);
    }

    public List<Boolean> getBooleanList(String key) {
        return section.getBooleanList(key);
    }

    public List<Double> getDoubleList(String key) {
        return section.getDoubleList(key);
    }

    public List<Float> getFloatList(String key) {
        return section.getFloatList(key);
    }

    public List<Long> getLongList(String key) {
        return section.getLongList(key);
    }

    public List<Byte> getByteList(String key) {
        return section.getByteList(key);
    }

    public List<Character> getCharacterList(String key) {
        return section.getCharacterList(key);
    }

    public List<Short> getShortList(String key) {
        return section.getShortList(key);
    }

    public List<Map> getMapList(String key) {
        return section.getMapList(key);
    }

    public void setAll(LinkedHashMap<String, Object> map) {
        this.section = new ConfigurationSection(map);
    }

    public void setAll(ConfigurationSection section) {
        this.section = section;
    }

    public boolean contains(String key) {
        return section.contains(key);
    }

    public boolean contains(String key, boolean ignoreCase) {
        return section.contains(key, ignoreCase);
    }

    public void remove(String key) {
        section.remove(key);
    }

    public Map<String, Object> getAll() {
        return section.getAll();
    }

    public ConfigurationSection getRootSection() {
        return section;
    }

    public int setDefault(LinkedHashMap<String, Object> map) {
        return setDefault(new ConfigurationSection(map));
    }

    public int setDefault(ConfigurationSection map) {
        int size = section.size();
        section = fillDefaults(map, section);
        return section.size() - size;
    }

    private ConfigurationSection fillDefaults(ConfigurationSection defaultMap, ConfigurationSection data) {
        for (String key : defaultMap.keySet()) {
            if (!data.containsKey(key)) {
                data.put(key, defaultMap.get(key));
            }
        }
        return data;
    }

    private void parseList(String content) {
        content = content.replace("\r\n", "\n");
        for (String v : content.split("\n")) {
            if (v.trim().isEmpty()) {
                continue;
            }
            section.put(v, true);
        }
    }

    private String writeProperties() {
        String content = "# Properties Config file\r\n#" + new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()) + "\r\n";
        for (Object o : section.entrySet()) {
            Map.Entry entry = (Map.Entry) o;
            Object v = entry.getValue();
            Object k = entry.getKey();
            if (v instanceof Boolean) {
                v = (Boolean) v ? "on" : "off";
            }
            content += k + "=" + v + "\r\n";
        }
        return content;
    }

    private void parseProperties(String content) {
        for (String line : content.split("\n")) {
            if (Pattern.compile("[a-zA-Z0-9\\-_.]*+=+[^\\r\\n]*").matcher(line).matches()) {
                String[] b = line.split("=", -1);
                String k = b[0];
                String v = b[1].trim();
                String v_lower = v.toLowerCase();
                if (section.containsKey(k)) {
                    Log.debug("[Config] Repeated property " + k + " on file " + this.file.toString());
                }
                switch (v_lower) {
                    case "on":
                    case "true":
                    case "yes":
                        section.put(k, true);
                        break;
                    case "off":
                    case "false":
                    case "no":
                        section.put(k, false);
                        break;
                    default:
                        section.put(k, v);
                        break;
                }
            }
        }
    }

    private void parseContent(String content) {
        switch (type) {
            case PROPERTIES:
                parseProperties(content);
                break;
            case JSON:
                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();
                section = new ConfigurationSection(gson.fromJson(content, new TypeToken<LinkedHashMap<String, Object>>() {
                }.getType()));
                break;
            case YAML:
                DumperOptions dumperOptions = new DumperOptions();
                dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
                Yaml yaml = new Yaml(dumperOptions);
                section = new ConfigurationSection(yaml.loadAs(content, LinkedHashMap.class));
                break;
            // case Config.SERIALIZED
            case CNF:
            case ENUM:
                parseList(content);
                break;
            default:
                valid = false;
        }
    }

    public Set<String> getKeys() {
        if (valid)
            return section.getKeys();

        return new HashSet<>();
    }

    public Set<String> getKeys(boolean child) {
        if (valid)
            return section.getKeys(child);


        return new HashSet<>();
    }
}
