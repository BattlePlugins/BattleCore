package mc.alk.battlecore.message;

import mc.alk.battlecore.util.Log;

import org.battleplugins.ChatColor;
import org.battleplugins.command.CommandSender;
import org.battleplugins.entity.living.player.OfflinePlayer;
import org.battleplugins.entity.living.player.Player;

/**
 * MessageController
 *
 * @author alkarin
 */
// TODO: Split into handlers and add support for other chat plugins (e.g. herochat)
public class MessageController {

    public static final char COLOR_MC_CHAR = ChatColor.COLOR_CHAR;

    public static String colorChat(String msg) {
        return msg.replaceAll("&", Character.toString(COLOR_MC_CHAR));
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
}
