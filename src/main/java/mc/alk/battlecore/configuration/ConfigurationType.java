package mc.alk.battlecore.configuration;

import java.util.Arrays;
import java.util.List;

/**
 * Configuration API adapted from Bukkit & Nukkit
 */
public enum ConfigurationType {

    DETECT,
    PROPERTIES("properties"),
    CNF("con", "conf", "config"),
    JSON("json"),
    YAML("yml", "yaml"),
    ENUM("enum", "list", "txt");

    private List<String> extensions;

    ConfigurationType(String... extensions) {
        this.extensions = Arrays.asList(extensions);
    }

    public List<String> getExtensions() {
        return extensions;
    }
}
