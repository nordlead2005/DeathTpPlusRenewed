package org.spigotmc.DeathTpPlusRenewed.teleport.commands;

import org.spigotmc.DeathTpPlusRenewed.DeathTpPlusRenewed;
import org.spigotmc.DeathTpPlusRenewed.commons.ConfigManager;
import org.spigotmc.DeathTpPlusRenewed.commons.DefaultLogger;
import org.spigotmc.DeathTpPlusRenewed.teleport.TeleportHelper;
import org.spigotmc.DeathTpPlusRenewed.teleport.persistence.DeathLocation;
import org.spigotmc.DeathTpPlusRenewed.teleport.persistence.DeathLocationDao;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * PluginName: DeathTpPlusRenewed
 * Class: DeathtpCommand
 * User: DonRedhorse
 * Date: 19.10.11
 * Time: 22:09
 */
public class DeathTpCommand implements CommandExecutor {
	private DeathTpPlusRenewed plugin;
	private DefaultLogger log;
	private ConfigManager config;
	private DeathLocationDao deathLocationLog;

	/**
	 * List of blocks which are normally save to teleport into
	 */
	public DeathTpCommand(DeathTpPlusRenewed instance) {
		this.plugin = instance;
		log = DefaultLogger.getLogger();
		config = ConfigManager.getInstance();
		deathLocationLog = DeathTpPlusRenewed.getDeathLocationLog();
		log.informational("deathtp command registered");
	}

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		boolean canUseCommand = false;
		log.debug("deathtp command executing");

		if (sender instanceof Player) {
			Player player = (Player) sender;

			canUseCommand = (player.hasPermission("deathtpplusrenewed.deathtp.deathtp") || config.isAllowDeathtp());

			if (canUseCommand) {
				TeleportHelper teleportHelper = new TeleportHelper(plugin);
				log.debug("canUseCommand", canUseCommand);
				String thisWorld = player.getWorld().getName();

				if (!teleportHelper.canTp(player, true)) {
					log.debug("canTp", "nope");
					return true;
				}

				DeathLocation locationRecord = deathLocationLog.getRecord(player.getName());

				if (locationRecord != null) {
					World deathWorld = player.getServer().getWorld(locationRecord.getWorldName());
					// check if world still exists otherwise display a message and exit
					if (deathWorld == null) {
						log.debug("World: " + locationRecord.getWorldName() + " doesn't exist anymore");
						player.sendMessage("The deathlocation is in a world which is no more! RIP: " + locationRecord.getWorldName());
						return true;
					}
					if (!teleportHelper.canGoBetween(thisWorld, deathWorld, player)) {
						player.sendMessage("You do not have the right to travel between worlds via deathtp!");
						return true;
					}

					Location deathLocation = teleportHelper.findTeleportLocation(locationRecord, player);

					if (deathLocation == null) {
						return true;
					}

					deathLocation.setWorld(deathWorld);
					player.teleport(deathLocation);
					teleportHelper.registerTp(player);
				}
			} else {
				player.sendMessage("That command is not available");
			}

			return true;
		} else {
			log.warning("This is only a player command.");
			return true;
		}
	}
}

