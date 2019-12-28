package mc.alk.battlecore.util;

import mc.alk.battlecore.message.MessageController;

import org.battleplugins.api.message.MessageStyle;
import org.battleplugins.api.plugin.Plugin;

public class Log {

    private static Plugin plugin;

    public static void info(String msg){
        plugin.getLogger().info(colorChat(msg));
    }

    public static void warn(String msg){
        plugin.getLogger().warning(colorChat(msg));
    }

    public static void err(String msg){
        plugin.getLogger().error(colorChat(msg));
    }

    public static String colorChat(String msg) {
        return msg.replace("&", String.valueOf(MessageStyle.COLOR_CHAR));
    }

    public static void debug(String string) {
        plugin.getLogger().debug(string);
    }

    public static void setDebug(boolean debug) {
        plugin.getLogger().setDebug(debug);
    }

    public static void setPlugin(Plugin plugin) {
        Log.plugin = plugin;
    }
}
