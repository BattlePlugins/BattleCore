package mc.alk.battlecore.core;

import mc.alk.battlecore.bukkit.BukkitBattlePlugin;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

public class MCListener extends BukkitBattlePlugin implements Listener{

	@Override
	public void onEnable() {
		super.onEnable();
		Bukkit.getServer().getPluginManager().registerEvents(this, this);
	}
}
