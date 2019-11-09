package mc.alk.battlecore.util;

import mc.alk.mc.MCOfflinePlayer;
import mc.alk.mc.MCPlatform;
import mc.alk.mc.MCPlayer;

public class PlayerUtil {

    public static MCPlayer findPlayer(String name) {
        if (name == null)
            return null;

        MCPlayer foundPlayer = MCPlatform.getPlatform().getPlayer(name);
        if (foundPlayer != null)
            return foundPlayer;

        for (MCPlayer player : MCPlatform.getPlatform().getOnlinePlayers()) {
            String playerName = player.getName();

            if (playerName.equalsIgnoreCase(name)) {
                foundPlayer = player;
                break;
            }

            if (playerName.toLowerCase().contains(name.toLowerCase())) {
                if (foundPlayer != null) {
                    return null;}

                foundPlayer = player;
            }
        }

        return foundPlayer;
    }

    public static MCOfflinePlayer findOfflinePlayer(String name) {
        MCPlayer player = findPlayer(name);
        if (player != null){
            return MCPlatform.getPlatform().getOfflinePlayer(name);
        } else{
            MCOfflinePlayer offlinePlayer = MCPlatform.getPlatform().getOfflinePlayer(name);
            if (offlinePlayer != null)
                return offlinePlayer;

            return null;
        }
    }
}
