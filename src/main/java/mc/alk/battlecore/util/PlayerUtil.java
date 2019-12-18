package mc.alk.battlecore.util;

import org.battleplugins.Platform;
import org.battleplugins.entity.living.player.OfflinePlayer;
import org.battleplugins.entity.living.player.Player;

import java.util.Optional;

public class PlayerUtil {

    public static Optional<? extends Player> findPlayer(String name) {
        if (name == null)
            return Optional.empty();

        return Platform.getPlatform().getPlayer(name);
    }

    public static Optional<? extends OfflinePlayer> findOfflinePlayer(String name) {
        if (findPlayer(name).isPresent())
            return findPlayer(name);

        return Platform.getPlatform().getOfflinePlayer(name);
    }
}
