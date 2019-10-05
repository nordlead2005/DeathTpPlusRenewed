package org.spigotmc.DeathTpPlusRenewed.tomb.events.handlers;

import org.spigotmc.DeathTpPlusRenewed.DeathTpPlusRenewed;
import org.spigotmc.DeathTpPlusRenewed.commons.ConfigManager;
import org.spigotmc.DeathTpPlusRenewed.commons.DefaultLogger;
import org.spigotmc.DeathTpPlusRenewed.teleport.TeleportHelper;
import org.spigotmc.DeathTpPlusRenewed.teleport.persistence.DeathLocation;
import org.spigotmc.DeathTpPlusRenewed.teleport.persistence.DeathLocationDao;
import org.spigotmc.DeathTpPlusRenewed.tomb.TombStoneHelper;
import org.spigotmc.DeathTpPlusRenewed.tomb.models.Tomb;
import org.spigotmc.DeathTpPlusRenewed.tomb.models.TombStoneBlock;
import org.spigotmc.DeathTpPlusRenewed.tomb.workers.TombWorker;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.data.type.Sign;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * PluginName: DeathTpPlusRenewed
 * Class: PlayerInteractHandler
 * User: DonRedhorse
 * Date: 19.11.11
 * Time: 20:29
 */
public class PlayerInteractHandler {
   private DefaultLogger log;
   private ConfigManager config;
   private DeathTpPlusRenewed plugin;
   private TombWorker tombWorker = TombWorker.getInstance();
   private TombStoneHelper tombStoneHelper;
   private DeathLocationDao deathLocationLog;

   public PlayerInteractHandler(DeathTpPlusRenewed plugin) {
      log = DefaultLogger.getLogger();
      config = ConfigManager.getInstance();
      tombWorker = TombWorker.getInstance();
      this.plugin = plugin;
      tombStoneHelper = TombStoneHelper.getInstance();
      deathLocationLog = DeathTpPlusRenewed.getDeathLocationLog();
   }

   public void playerInteractTombStone(PlayerInteractEvent event) {
      Block b = event.getClickedBlock();

      if (b.getType() != Material.OAK_SIGN && b.getType() != Material.CHEST) {
         return;
      }
      // We'll do quickloot on rightclick of chest if we're going to destroy
      // it anyways

      if (b.getType() == Material.CHEST
            && (!config.isDestroyOnQuickLoot() || config.isAllowTombStoneDestroy())) {
         return;
      }
      if (!plugin.hasPerm(event.getPlayer(), "tombstone.quickloot", false)) {
         return;
      }

      TombStoneBlock tStoneBlock = tombStoneHelper.getTombStoneBlockList(b.getLocation());
      if (tStoneBlock == null || !(tStoneBlock.getBlock().getState() instanceof Chest)) {
         log.debug("Not a Tombstone!");
         return;
      }

      if (!tStoneBlock.getOwner().equals(event.getPlayer().getName())) {
         log.debug("Owner of tombstone", tStoneBlock.getOwner());
         return;
      }

      Player player = event.getPlayer();
      Chest sChest = (Chest) tStoneBlock.getBlock().getState();
      Chest lChest = (tStoneBlock.getLBlock() != null) ? (Chest) tStoneBlock
            .getLBlock().getState() : null;

      ItemStack[] items = sChest.getInventory().getContents();
      boolean overflow = false;
      for (int cSlot = 0; cSlot < items.length; cSlot++) {
         ItemStack item = items[cSlot];
         if (item == null) {
            continue;
         }
         if (item.getType() == Material.AIR) {
            continue;
         }
         int slot = player.getInventory().firstEmpty();
         if (slot == -1) {
            overflow = true;
            break;
         }
         player.getInventory().setItem(slot, item);
         sChest.getInventory().clear(cSlot);
      }
      if (lChest != null) {
         items = lChest.getInventory().getContents();
         for (int cSlot = 0; cSlot < items.length; cSlot++) {
            ItemStack item = items[cSlot];
            if (item == null) {
               continue;
            }
            if (item.getType() == Material.AIR) {
               continue;
            }
            int slot = player.getInventory().firstEmpty();
            if (slot == -1) {
               overflow = true;
               break;
            }
            player.getInventory().setItem(slot, item);
            lChest.getInventory().clear(cSlot);
         }
      }

      int storedDroppedExperience = tStoneBlock.getDroppedExperience();
      int playerTotalExperience = player.getTotalExperience();
      log.debug("Player TotalExperience", playerTotalExperience);
      log.debug("Stored droppedExperience", storedDroppedExperience);
      /*for (int i = 0; i < storedDroppedExperience; ++i) {
               player.giveExp(1);
               log.debug("Player New TotalExperience", player.getTotalExperience());
            }*/
      player.setTotalExperience(storedDroppedExperience + playerTotalExperience);
      log.debug("Player New TotalExperience", player.getTotalExperience());
      tStoneBlock.clearExperience();
      if (!overflow) {
         // We're quicklooting, so no need to resume this interaction
         event.setUseInteractedBlock(Result.DENY);
         event.setUseItemInHand(Result.DENY);
         // TODO: Minor bug here - if
         // you're holding a sign,
         // it'll still pop up
         event.setCancelled(true);

         if (config.isDestroyOnQuickLoot()) {
            tombStoneHelper.destroyTombStone(tStoneBlock);
         }
      }

      // Manually update inventory for the time being.
      updatePlayerInventory(event);
      plugin.sendMessage(event.getPlayer(), "Tombstone quicklooted!");
      Location location = tStoneBlock.getBlock().getLocation();
      String loc = location.getWorld().getName();
      loc = loc + ", x=" + location.getBlock().getX();
      loc = loc + ", y=" + location.getBlock().getY();
      loc = loc + ", z=" + location.getBlock().getZ();
      log.debug(event.getPlayer().getName() + " quicklooted tombstone at "
            + loc);
   }

   private void updatePlayerInventory(PlayerInteractEvent event) {
      event.getPlayer().updateInventory();
   }

   public void playerInteractTomb(PlayerInteractEvent event) {
      Block b = event.getClickedBlock();

      //is it a sign?
      if (!(b.getState() instanceof org.bukkit.block.Sign)) {
         return;
      }

      //is it a tomb?
      org.bukkit.block.Sign sign = (org.bukkit.block.Sign)b.getState();
      if(!sign.getLine(0).contentEquals(config.getTombKeyWord()))
      {
         return;
      }

      //is it the player's tomb?
      Player player = event.getPlayer();
      if(!sign.getLine(1).contentEquals(player.getName()))
      {
         return;
      }

      //make sure it is a valid tomb
      if (!tombWorker.hasTomb(player.getName())) {
         return;
      }
      Tomb tomb = tombWorker.getTomb(b);
      if (tomb != null && tomb.hasSign(b)) {
         TeleportHelper teleportHelper = new TeleportHelper(plugin);
         String thisWorld = player.getWorld().getName();
         DeathLocation locationRecord = deathLocationLog.getRecord(player.getName());
         if (locationRecord != null) {
            World deathWorld = player.getServer().getWorld(locationRecord.getWorldName());
            if (deathWorld == null) {
               log.debug("World: " + locationRecord.getWorldName() + " doesn't exist anymore");
               player.sendMessage("The deathlocation is in a world which is no more! RIP: " + locationRecord.getWorldName());
               return;
            }
            if (!teleportHelper.canGoBetween(thisWorld, deathWorld, player)) {
               player.sendMessage("You do not have the right to travel between worlds via your tomb!");
               return;
            }

            Location deathLocation = teleportHelper.findTeleportLocation(locationRecord, player);

            if (deathLocation == null) {
               return;
            }

            if(!teleportHelper.canTp(player, false))
            {
               return;
            }

            deathLocation.setWorld(deathWorld);
            player.teleport(deathLocation);
            TombWorker tombWorker = TombWorker.getInstance();

            player.sendMessage(tombWorker.graveDigger + "I teleported you to your place of death.");
            teleportHelper.registerTp(player, false);
         }
      }
   }
}

