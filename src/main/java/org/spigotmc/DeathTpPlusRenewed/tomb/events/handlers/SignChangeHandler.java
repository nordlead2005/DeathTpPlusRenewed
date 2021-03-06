package org.spigotmc.DeathTpPlusRenewed.tomb.events.handlers;

import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;
import org.spigotmc.DeathTpPlusRenewed.commons.ConfigManager;
import org.spigotmc.DeathTpPlusRenewed.commons.DefaultLogger;
import org.spigotmc.DeathTpPlusRenewed.tomb.models.Tomb;
import org.spigotmc.DeathTpPlusRenewed.tomb.workers.TombWorker;

/**
 * PluginName: DeathTpPlusRenewed
 * Class: SignChangeHandler
 * User: DonRedhorse
 * Date: 19.11.11
 * Time: 20:33
 */

public class SignChangeHandler {

	private DefaultLogger log;
	private ConfigManager config;
	private TombWorker tombWorker;

	public SignChangeHandler() {
		this.log = DefaultLogger.getLogger();
		this.config = ConfigManager.getInstance();
		this.tombWorker = TombWorker.getInstance();
	}

	public void oSCTomb(SignChangeEvent event) {
		String line0 = event.getLine(0);
		Player p = event.getPlayer();
		boolean admin = false;
		if (line0.indexOf(config.getTombKeyWord()) == 0) {
			if (!event.getLine(1).isEmpty() && p.hasPermission("deathtpplusrenewed.admin.tomb")) {
				admin = true;
			}
// Sign check
			Tomb tomb = null;
			String deadName = event.getLine(1);
			if (admin) {
				if ((tomb = tombWorker.getTomb(deadName)) == null) {
					deadName = event.getLine(1);
				} else {
					deadName = tomb.getPlayer();
				}
			} else {
				deadName = event.getPlayer().getName();
			}
			log.debug("deadName", deadName);
			if (tomb != null) {
				tomb.checkSigns();
			} else if (tombWorker.hasTomb(deadName)) {
				tomb = tombWorker.getTomb(deadName);
				tomb.checkSigns();
			}
			int nbSign = 0;
			if (tomb != null) {
				nbSign = tomb.getNbSign();
			}
// max check
			int maxTombs = config.getMaxTomb();
			if (!admin && maxTombs != 0 && (nbSign + 1) > maxTombs) {
				p.sendMessage(tombWorker.graveDigger + "You have reached your Tomb limit.");
				event.setCancelled(true);
				return;
			}
// perm and economy check
			if ((!admin && !p.hasPermission("deathtpplusrenewed.tomb.create"))
					|| !tombWorker.economyCheck(p, "creation-price")) {
				event.setCancelled(true);
				return;
			}
			try {

				if (tomb != null) {
					tomb.setPlayer(deadName);
				} else {
					tomb = new Tomb();
					tomb.setPlayer(deadName);
					tombWorker.setTomb(deadName, tomb);
				}
				tomb.addSignBlock(event);
				if (config.isUseTombAsRespawnPoint()) {
					tomb.setRespawn(p.getLocation(), p.getWorld().getName());
					if (admin) {
						p.sendMessage(tombWorker.graveDigger + " When " + deadName
								+ " die, he/she will respawn here.");
					} else {
						p.sendMessage(tombWorker.graveDigger + " When you die you'll respawn here.");
					}
				}
			} catch (IllegalArgumentException e2) {
				p.sendMessage(tombWorker.graveDigger
						+ "It's not a good place for a Tomb. Try somewhere else.");
			}

		}
	}

}
