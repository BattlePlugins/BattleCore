package mc.alk.battlecore.util;

import org.battleplugins.api.Platform;
import org.battleplugins.api.entity.living.player.OfflinePlayer;
import org.battleplugins.api.entity.living.player.Player;

import java.util.Optional;

public class PlayerUtil {

    public static Optional<? extends Player> findPlayer(String name) {
        if (name == null)
            return Optional.empty();

        return Platform.getServer().getPlayer(name);
    }

    public static Optional<? extends OfflinePlayer> findOfflinePlayer(String name) {
        if (findPlayer(name).isPresent())
            return findPlayer(name);

        return Platform.getServer().getOfflinePlayer(name);
    }
}
