package mc.alk.battlecore.message;

import mc.alk.battlecore.util.Log;

import org.battleplugins.api.command.CommandSender;
import org.battleplugins.api.entity.living.player.OfflinePlayer;
import org.battleplugins.api.entity.living.player.Player;
import org.battleplugins.api.message.MessageStyle;

/**
 * MessageController
 *
 * @author alkarin
 */
// TODO: Split into handlers and add support for other chat plugins (e.g. herochat)
public class MessageController {

    public static String colorChat(String msg) {
        return msg.replaceAll("&", Character.toString(MessageStyle.COLOR_CHAR));
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
