package mc.alk.battlecore.message;

import net.kyori.adventure.text.Component;
import org.battleplugins.api.command.CommandSender;

/**
 * MessageController
 *
 * @author alkarin
 */
// TODO: Split into handlers and add support for other chat plugins (e.g. herochat)
public class MessageController {

    public static boolean sendMessage(CommandSender sender, Component... messages) {
        if (messages == null)
            return true;

        for (Component message : messages) {
            sender.sendMessage(message);
        }
        return true;
    }
}
