package mc.alk.battlecore.util;

import mc.alk.battlecore.controllers.MessageController;
import mc.alk.mc.plugin.MCPlugin;

public class Log {

    private static MCPlugin plugin;

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
        return msg.replace("&", String.valueOf(MessageController.COLOR_MC_CHAR));
    }

    public static void debug(String string) {
        plugin.getLogger().debug(string);
    }

    public static void setDebug(boolean debug) {
        plugin.getLogger().setDebug(debug);
    }

    public static void setPlugin(MCPlugin plugin) {
        Log.plugin = plugin;
    }
}
