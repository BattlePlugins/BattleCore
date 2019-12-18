package mc.alk.battlecore.message;

import mc.alk.battlecore.configuration.Configuration;
import mc.alk.battlecore.configuration.ConfigurationSection;
import mc.alk.battlecore.util.Log;

import org.battleplugins.ChatColor;
import org.battleplugins.command.CommandSender;
import org.battleplugins.entity.living.player.OfflinePlayer;
import org.battleplugins.entity.living.player.Player;

import java.io.File;
import java.util.Formatter;

/**
 * MessageController
 *
 * @author alkarin
 */
// TODO: Split into handlers and add support for other chat plugins (e.g. herochat)
public class MessageController {

    public static final char COLOR_MC_CHAR = ChatColor.COLOR_CHAR;

    private File file;
    private Configuration config = new Configuration();

    public String getMessage(String prefix,String node, Object... varArgs) {
        try{
            ConfigurationSection n = config.getSection(prefix);

            StringBuilder buf = new StringBuilder(n.getString("prefix", "[Arena]"));
            String msg = n.getString(node, "No translation for " + node);
            Formatter form = new Formatter(buf);

            form.format(msg, varArgs);
            form.close();
            return colorChat(buf.toString());
        } catch(Exception e){
            Log.err("Error getting message " + prefix + "." + node);
            for (Object o: varArgs){
                Log.err("argument=" + o);
            }
            e.printStackTrace();
            return "Error getting message " + prefix + "." + node;
        }
    }
    public String getMessageNP(String prefix,String node, Object... varArgs) {
        ConfigurationSection n = config.getSection(prefix);
        StringBuilder buf = new StringBuilder();
        String msg = n.getString(node, "No translation for " + node);
        Formatter form = new Formatter(buf);
        try{
            form.format(msg, varArgs);
            form.close();
        } catch(Exception e){
            Log.err("Error getting message " + prefix + "." + node);
            for (Object o: varArgs){
                Log.err("argument=" + o);
            }
            e.printStackTrace();
        }
        return colorChat(buf.toString());
    }

    public String getMessageAddPrefix(String pprefix, String prefix,String node, Object... varArgs) {
        ConfigurationSection n = config.getSection(prefix);
        StringBuilder buf = new StringBuilder(pprefix);
        String msg = n.getString(node, "No translation for " + node);
        Formatter form = new Formatter(buf);
        try{
            form.format(msg, varArgs);
            form.close();
        } catch(Exception e){
            Log.err("Error getting message " + prefix + "." + node);
            for (Object o: varArgs) {
                Log.err("argument=" + o);
            }
            e.printStackTrace();
        }
        return colorChat(buf.toString());
    }

    public static String colorChat(String msg) {
        return msg.replaceAll("&", Character.toString(COLOR_MC_CHAR));
    }

    public boolean setConfig(File file){
        this.file = file;
        return load();
    }

    public static boolean sendMessage(Player p, String message){
        if (message == null)
            return true;

        String[] msgs = message.split("\n");
        for (String msg: msgs){
            if (p == null){
                Log.info(MessageController.colorChat(msg));
            } else {
                p.sendMessage(MessageController.colorChat(msg));
            }
        }
        return true;
    }

    public static boolean sendMessage(OfflinePlayer p, String message){
        if (message == null)
            return true;
        String[] msgs = message.split("\n");
        for (String msg: msgs){
            if (p == null){
                Log.info(MessageController.colorChat(msg));
            } else {
                p.getPlayer().ifPresent(player -> player.sendMessage(MessageController.colorChat(msg)));
            }
        }

        return true;
    }

    public static boolean sendMessage(CommandSender sender, String message){
        if (message == null)
            return true;

        sender.sendMessage(MessageController.colorChat(message));
        return true;
    }

    public boolean sendMultilineMessage(CommandSender sender, String message){
        if (message == null)
            return true;

        String[] messages = message.split("\n");
        for (String msg : messages){
            sendMessage(sender, msg);
        }
        return true;
    }

    public boolean load() {
        try {
            config.load(file.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }
}
