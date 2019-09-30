package org.spigotmc.DeathTpPlusRenewed.death.events.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.spigotmc.DeathTpPlusRenewed.DeathTpPlusRenewed;
import org.spigotmc.DeathTpPlusRenewed.commons.ConfigManager;
import org.spigotmc.DeathTpPlusRenewed.death.events.DeathStreakEvent;
import org.spigotmc.DeathTpPlusRenewed.death.events.KillStreakEvent;

/**
 * PluginName: DeathTpPlusRenewed
 * Class: StreakListener
 * User: DonRedhorse
 * Date: 26.11.11
 * Time: 19:57
 */

public class StreakListener implements Listener {
	private DeathTpPlusRenewed plugin;
	private ConfigManager config = ConfigManager.getInstance();

	public StreakListener(DeathTpPlusRenewed plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onDeathStreakEvent(DeathStreakEvent event) {
		String playerName = getPlayerNameForBroadcast(event.getPlayer());
		plugin.getServer().broadcastMessage(event.getMessage().replace("%n", playerName));
	}

	@EventHandler
	public void onKillStreakEvent(final KillStreakEvent event) {
		String playerName = getPlayerNameForBroadcast(event.getPlayer());
		plugin.getServer().broadcastMessage(event.getMessage().replace("%n", playerName));
	}

	private String getPlayerNameForBroadcast(Player player) {
		String playerName = player.getName();
		if (config.isUseDisplayNameforBroadcasts()) {
			playerName = player.getDisplayName();
		}
		if (playerName.contains("*")) {
			playerName = playerName.replace("*", "");
		}
		//Todo Add NPE handling for specific errors here, have no idea atm what that could be
		//ToDo we will need input from users about that

		return playerName;
	}
}

