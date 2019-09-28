package org.spigotmc.DeathTpPlusRenewed.death.events.listeners;

//java imports

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.spigotmc.DeathTpPlusRenewed.DeathTpPlusRenewed;
import org.spigotmc.DeathTpPlusRenewed.commons.ConfigManager;
import org.spigotmc.DeathTpPlusRenewed.commons.DefaultLogger;
import org.spigotmc.DeathTpPlusRenewed.death.events.handlers.EntityDeathHandler;
import org.spigotmc.DeathTpPlusRenewed.tomb.TombStoneHelper;
import org.spigotmc.DeathTpPlusRenewed.tomb.models.TombStoneBlock;

//bukkit imports

public class EntityListener implements Listener {
	private DeathTpPlusRenewed plugin;

	private ConfigManager config;
	private DefaultLogger log;
	private EntityDeathHandler oedea;
	private EntityListener instance;
	private TombStoneHelper tombStoneHelper;

	public EntityListener(DeathTpPlusRenewed plugin) {
		this.plugin = plugin;
		log = DefaultLogger.getLogger();
		config = ConfigManager.getInstance();
		tombStoneHelper = TombStoneHelper.getInstance();
		log.debug("EntityListener active");
		instance = this;
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onEntityDeath(EntityDeathEvent event) {

		if (event.getEntity() instanceof Player) {

			if (plugin.isMobArenaEnabled()) {
				if (plugin.getMaHandler().inRegion(event.getEntity().getLocation())) {
					log.debug("Player in MobArena Region");
					return;
				}


			}
			if (config.isEnableDeathtp()) {
				oedea = new EntityDeathHandler(plugin);
				oedea.oEDeaDeathTp(plugin, instance, event);
			}

			if (config.isShowDeathNotify() || config.isShowStreaks() || config.isAllowDeathLog() || config.isEnableTombStone() || config.isEnableTomb()) {
				oedea = new EntityDeathHandler(plugin);
				oedea.oEDeaGeneralDeath(plugin, instance, event);
			}
		}

	}

	@EventHandler
	public void onEntityExplode(EntityExplodeEvent event) {
		if (event.isCancelled()) {
			return;
		}
		if (!config.isCreeperProtection()) {
			return;
		}
		for (Block block : event.blockList()) {
			TombStoneBlock tStoneBlock = tombStoneHelper.getTombStoneBlockList(block.getLocation());
			if (tStoneBlock != null) {
				event.setCancelled(true);
			}
		}
	}

}
