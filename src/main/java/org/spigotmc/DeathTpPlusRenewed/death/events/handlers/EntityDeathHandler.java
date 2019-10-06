package org.spigotmc.DeathTpPlusRenewed.death.events.handlers;

//~--- non-JDK imports --------------------------------------------------------

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.Chest.Type;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.spigotmc.DeathTpPlusRenewed.DeathTpPlusRenewed;
import org.spigotmc.DeathTpPlusRenewed.commons.ConfigManager;
import org.spigotmc.DeathTpPlusRenewed.commons.DefaultLogger;
import org.spigotmc.DeathTpPlusRenewed.commons.utils.MessageUtil;
import org.spigotmc.DeathTpPlusRenewed.death.DeathDetail;
import org.spigotmc.DeathTpPlusRenewed.death.DeathMessages;
import org.spigotmc.DeathTpPlusRenewed.death.events.listeners.EntityListener;
import org.spigotmc.DeathTpPlusRenewed.death.persistence.DeathRecordDao;
import org.spigotmc.DeathTpPlusRenewed.death.persistence.StreakRecordDao;
import org.spigotmc.DeathTpPlusRenewed.teleport.persistence.DeathLocationDao;
import org.spigotmc.DeathTpPlusRenewed.tomb.TombMessages;
import org.spigotmc.DeathTpPlusRenewed.tomb.TombStoneHelper;
import org.spigotmc.DeathTpPlusRenewed.tomb.models.Tomb;
import org.spigotmc.DeathTpPlusRenewed.tomb.models.TombStoneBlock;
import org.spigotmc.DeathTpPlusRenewed.tomb.workers.TombWorker;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;

import java.text.SimpleDateFormat;
import java.util.*;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;

//~--- JDK imports ------------------------------------------------------------

//~--- classes ----------------------------------------------------------------

/**
 * PluginName: DeathTpPlusRenewed
 * Class: EntityDeathHandler
 * User: DonRedhorse
 * Date: 19.11.11
 * Time: 20:27
 */
public class EntityDeathHandler {
	/**
	 * Field description
	 */
	private TombWorker tombWorker = TombWorker.getInstance();

	/**
	 * Field description
	 */
	private ConfigManager config;

	/**
	 * Field description
	 */
	private DeathLocationDao deathLocationsLog;

	/**
	 * Field description
	 */
	private DeathRecordDao deathLog;

	/**
	 * Field description
	 */
	private DefaultLogger log;

	/**
	 * Field description
	 */
	private DeathTpPlusRenewed plugin;

	/**
	 * Field description
	 */
	private StreakRecordDao streakLog;

	/**
	 * Field description
	 */
	private TombMessages tombMessages;

	/**
	 * Field description
	 */
	private TombStoneHelper tombStoneHelper;

	//~--- constructors -------------------------------------------------------

	/**
	 * Constructs ...
	 *
	 * @param plugin
	 */
	public EntityDeathHandler(DeathTpPlusRenewed plugin) {
		log = DefaultLogger.getLogger();
		config = ConfigManager.getInstance();
		tombWorker = TombWorker.getInstance();
		tombMessages = TombMessages.getInstance();
		this.plugin = plugin;
		deathLocationsLog = DeathTpPlusRenewed.getDeathLocationLog();
		streakLog = DeathTpPlusRenewed.getStreakLog();
		deathLog = DeathTpPlusRenewed.getDeathLog();
		tombStoneHelper = TombStoneHelper.getInstance();
	}

	//~--- methods ------------------------------------------------------------

	/**
	 * Method description
	 *
	 * @param plugin
	 * @param entityListener
	 * @param entityDeathEvent
	 */
	public void oEDeaDeathTp(DeathTpPlusRenewed plugin, EntityListener entityListener,
	                         EntityDeathEvent entityDeathEvent) {
		DeathDetail deathDetail = new DeathDetail(entityDeathEvent);

		log.debug("deathDetail", deathDetail);
		deathLocationsLog.setRecord(deathDetail);
	}

	/**
	 * Method description
	 *
	 * @param plugin
	 * @param entityListener
	 * @param entityDeathEvent
	 */
	public void oEDeaGeneralDeath(DeathTpPlusRenewed plugin, EntityListener entityListener,
	                              EntityDeathEvent entityDeathEvent) {
		DeathDetail deathDetail = new DeathDetail(entityDeathEvent);

		if (config.isShowStreaks()) {
			streakLog.setRecord(deathDetail);
		}

		if (config.isShowDeathNotify()) {
			String deathMessage = DeathMessages.getDeathMessage(deathDetail);

			if (entityDeathEvent instanceof PlayerDeathEvent) {
				if (config.isDisableDeathNotifyInSpecifiedWorlds() || config.isShowDeathNotifyInDeathWorldOnly()) {
                    log.debug("deathMessage to selective worlds", deathMessage);
	                Set<String> notifyWorlds = new HashSet<String>();
	                
	                if (config.isShowDeathNotifyInDeathWorldOnly()) {
	                    notifyWorlds.add(deathDetail.getWorld().getName());
	                } else {
	                    notifyWorlds.addAll(getWorldNames());
	                }
	                if (config.isDebugLogEnabled()){
                        log.debug("Notify to following worlds:");
                        for (String temp : notifyWorlds){
                            log.debug(temp);
                        }
                    }
	                if (config.isDisableDeathNotifyInSpecifiedWorlds()) {
	                	Set<String> tmpWorlds = new HashSet<String>(notifyWorlds);
	                    for (String world : tmpWorlds) {
	                        if (config.isDisabledDeathNotifyWorld(world)) {
                                log.debug("Removing from notify worlds:",world);
	                            notifyWorlds.remove(world);
	                        }
	                    }
	                }

	                for (Player player : plugin.getServer().getOnlinePlayers()) {
                        log.debug("player",player.getName());
                        log.debug("playerworld ",player.getWorld());
	                    if (notifyWorlds.contains(player.getWorld().getName())) {
	                        player.sendMessage(deathMessage);
	                    }
	                }
	                ((PlayerDeathEvent) entityDeathEvent).setDeathMessage("");
				} else {
                    log.debug("deathMessage to all worlds", deathMessage);
					((PlayerDeathEvent) entityDeathEvent).setDeathMessage(deathMessage);
				}

				if (config.isShowDeathNotifyOnConsole()) {
					log.info(MessageUtil.removeColorCodes(deathMessage));
				}
			}
		}

        // write kill to deathlog
		if (config.isAllowDeathLog()) {
			deathLog.setRecord(deathDetail);
		}

		// Tombstone part
		if (config.isEnableTombStone()) {
			CreateTombStone(deathDetail);
		}

		// Tomb part
		if (config.isEnableTomb()) {
			UpdateTomb(deathDetail);
		}

		if (config.isShowDeathSign()) {
			ShowDeathSign(deathDetail);
		}
	}

	private static Set<String> getWorldNames() {
		Set<String> worldNames = new HashSet<String>();
		for (World world : Bukkit.getWorlds()) {
			worldNames.add(world.getName());
		}
		return worldNames;
	}
	private boolean isSapling(Material material)
	{
		return material == Material.OAK_SAPLING ||
			   material == Material.ACACIA_SAPLING ||
			   material == Material.BIRCH_SAPLING ||
			   material == Material.DARK_OAK_SAPLING ||
			   material == Material.JUNGLE_SAPLING ||
			   material == Material.SPRUCE_SAPLING ||
			   material == Material.BAMBOO_SAPLING;
	}
	private boolean isSign(Material material)
	{
		return material == Material.ACACIA_WALL_SIGN ||
	           material == Material.BIRCH_WALL_SIGN ||
	           material == Material.DARK_OAK_WALL_SIGN ||
	           material == Material.JUNGLE_WALL_SIGN ||
	           material == Material.OAK_WALL_SIGN ||
	           material == Material.SPRUCE_WALL_SIGN ||
		       material == Material.ACACIA_SIGN ||
	           material == Material.BIRCH_SIGN ||
	           material == Material.DARK_OAK_SIGN ||
	           material == Material.JUNGLE_SIGN ||
	           material == Material.OAK_SIGN ||
	           material == Material.SPRUCE_SIGN;
	}
	private boolean isPlate(Material material)
	{
		return material == Material.SPRUCE_PRESSURE_PLATE ||
	           material == Material.BIRCH_PRESSURE_PLATE ||
	           material == Material.DARK_OAK_PRESSURE_PLATE ||
	           material == Material.JUNGLE_PRESSURE_PLATE ||
	           material == Material.OAK_PRESSURE_PLATE;
	}

	/**
	 * Method description
	 *
	 * @param deathDetail
	 */
	private void CreateTombStone(DeathDetail deathDetail) {
		Player player = deathDetail.getPlayer();

		if (!plugin.hasPerm(player, "tombstone.use", false)) {
			return;
		}

		log.debug(player.getName() + " died.");

		List<ItemStack> deathDrops = deathDetail.getEntityDeathEvent().getDrops();

		if ((deathDrops.size() == 0) && !(config.isKeepExperienceOnQuickLoot())) {
			plugin.sendMessage(player, "Inventory Empty.");
			log.debug(player.getName() + " inventory empty.");

			return;
		}

//      Get the current player location.
		Location loc = player.getLocation();
		Block block = returnGoodPlace(player, loc);

		//WorldGuard 7.0
		if (plugin.isWorldGuardEnabled())
		{
			LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
			RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
			RegionQuery query = container.createQuery();
			if(!query.testState(BukkitAdapter.adapt(block.getLocation()), localPlayer, Flags.BUILD))
			{
				plugin.sendMessage(player, "You died in a protected region. Dropping inventory.");
				log.debug(player.getName() + " died in WorldGuard Region, dropping inventory");
				return;
			}
		}
		
		//TODO: GriefPrevention
		if(plugin.isGriefPreventionEnabled())
		{
			// GriefPrevention.instance.claim
			if(config.isAllowGriefPreventionContainerTrust())
			{
				Claim claim = GriefPrevention.instance.dataStore.getClaimAt(loc, false, null);
				String allowBuild = claim.allowContainers(player);
				if(null != allowBuild)
				{
					plugin.sendMessage(player, "You died in a GriefProtection Region without container permissions. Dropping inventory.");
					log.informational("GriefPrevention.allowBuild = "+allowBuild);
					return;
				}
			}
			else
			{
				String allowBuild = GriefPrevention.instance.allowBuild(player, loc);
				if(allowBuild != null)
				{
					plugin.sendMessage(player, "You died in a GriefProtection Region without build permissions. Dropping inventory.");
					log.informational("GriefPrevention.allowBuild = "+allowBuild);
				}
			}
		}

//      Don't create the chest if it or its sign would be in the void
		if (config.isVoidCheck()
				&& ((config.isShowTombStoneSign() && (block.getY() > block.getWorld().getMaxHeight() - 1))
				|| (!config.isShowTombStoneSign() && (block.getY() > block.getWorld().getMaxHeight()))
				|| (player.getLocation().getY() < 1))) {
			plugin.sendMessage(player, "Your tombstone would be in the Void. Inventory dropped");
			log.debug(player.getName() + " died in the Void.");

			return;
		}

//      Check if the player has a chest.
		int pChestCount = 0;
		int pSignCount = 0;

		for (ItemStack item : deathDrops) {
			if (item == null) {
				continue;
			}

			if (item.getType() == Material.CHEST) {
				pChestCount += item.getAmount();
			}

			if (isSign(item.getType())) {
				pSignCount += item.getAmount();
			}
		}

		if ((pChestCount == 0) && !(plugin.hasPerm(player, "tombstone.freechest", false))) {
			plugin.sendMessage(player, "No chest found in inventory. Inventory dropped");
			log.debug(player.getName() + " No chest in inventory.");

			return;
		}

//      Check if we can replace the block.
		block = tombStoneHelper.findPlace(block, false);

		if (block == null) {
			plugin.sendMessage(player, "Could not find room for chest. Inventory dropped");
			log.debug(player.getName() + " Could not find room for chest.");

			return;
		}

//      Check if there is a nearby chest
		if (!config.isAllowInterfere() && checkChest(block)) {
			plugin.sendMessage(player, "There is a chest interfering with your tombstone. Inventory dropped");
			log.debug(player.getName() + " Chest interfered with tombstone creation.");

			return;
		}

		int removeChestCount = 1;
		int removeSign = 0;

//      Do the check for a large chest block here so we can check for
//      interference
		Block lBlock = findLarge(block);

//      Set the current block to a chest, init some variables for later use.
		block.setType(Material.CHEST);

		BlockState state = block.getState();

		if (!(state instanceof Chest)) {
			plugin.sendMessage(player, "Could not access chest. Inventory dropped.");
			log.debug(player.getName() + " Could not access chest.");

			return;
		}

		Chest sChest = (Chest) state;
		Chest lChest = null;
		int slot = 0;
		int maxSlot = sChest.getInventory().getSize();

//      Check if they need a large chest.
		if (deathDrops.size() > maxSlot) {

//          If they are allowed spawn a large chest to catch their entire
//          inventory.
			if ((lBlock != null) && plugin.hasPerm(player, "tombstone.large", false)) {
				removeChestCount = 2;

//              Check if the player has enough chests
				if ((pChestCount >= removeChestCount) || plugin.hasPerm(player, "tombstone.freechest", false)) {
					lBlock.setType(Material.CHEST);
					lChest = (Chest) lBlock.getState();
					maxSlot = maxSlot * 2;
				} else {
					removeChestCount = 1;
				}
			}
		}

		//combine the chests and orrient them correctly
		if(lChest != null && sChest != null)
		{
			org.bukkit.block.data.type.Chest chestData = null;
			BlockFace facing = BlockFace.EAST;
			Location sLocation = sChest.getLocation();
			Location lLocation = lChest.getLocation();
			if(sLocation.getX() < lLocation.getX())
			{
				facing = BlockFace.SOUTH;
			}
			else if(sLocation.getX() > lLocation.getX())
			{
				facing = BlockFace.NORTH;
			}
			else if(sLocation.getZ() < lLocation.getZ())
			{
				facing = BlockFace.WEST;
			}
			// else // is default, no need to set it again
			// {
			// 	facing = BlockFace.EAST;
			// }
			log.debug("Combining Chests into Large Chest and facing "+facing.toString());
			chestData = (org.bukkit.block.data.type.Chest)sChest.getBlockData();
			chestData.setType(Type.RIGHT);
			chestData.setFacing(facing);
			sChest.setBlockData(chestData);
			chestData = (org.bukkit.block.data.type.Chest)lChest.getBlockData();
			chestData.setType(Type.LEFT);
			chestData.setFacing(facing);
			lChest.setBlockData(chestData);

			//update chests so they are drawn properly
			sChest.update();
			lChest.update();
		}

//      Don't remove any chests if they get a free one.
		if (plugin.hasPerm(player, "tombstone.freechest", false)) {
			removeChestCount = 0;
		}

//      Check if we have signs enabled, if the player can use signs, and if
//      the player has a sign or gets a free sign
		Block sBlock = null;

		if (config.isShowTombStoneSign() && plugin.hasPerm(player, "tombstone.sign", false)
				&& ((pSignCount > 0) || plugin.hasPerm(player, "tombstone.freesign", false))) {

//          Find a place to put the sign, then place the sign.
			sBlock = sChest.getWorld().getBlockAt(sChest.getX(), sChest.getY() + 1, sChest.getZ());

			if (tombStoneHelper.canReplace(sBlock.getType())) {
				createSign(sBlock, deathDetail);
				removeSign = 1;
			} else if (lChest != null) {
				sBlock = lChest.getWorld().getBlockAt(lChest.getX(), lChest.getY() + 1, lChest.getZ());

				if (tombStoneHelper.canReplace(sBlock.getType())) {
					createSign(sBlock, deathDetail);
					removeSign = 1;
				}
			}
		}

//      Don't remove a sign if they get a free one
		if (plugin.hasPerm(player, "tombstone.freesign", false)) {
			removeSign = 0;
		}

		int experience;

		if (config.isKeepExperienceOnQuickLoot()) {
			if (config.isKeepFullExperience()) {
				// experience = (int) Math.round(deathDetail.getPlayer().getExp());
				experience = deathDetail.getPlayer().getTotalExperience();
				log.debug("Get Experience", deathDetail.getPlayer().getExp());
				log.debug("GetTotal Experience", deathDetail.getPlayer().getTotalExperience());
			} else {
				experience = deathDetail.getEntityDeathEvent().getDroppedExp();
			}

			log.debug("experience", experience);
			deathDetail.getEntityDeathEvent().setDroppedExp(0);
		} else {
			experience = 0;
		}

//      Create a TombBlock for this tombstone
		TombStoneBlock tStoneBlock = new TombStoneBlock(sChest.getBlock(), (lChest != null)
				? lChest.getBlock()
				: null, sBlock, player.getName(), (System.currentTimeMillis() / 1000), experience);

//      Protect the chest/sign if LWC is installed.
		Boolean prot = false;
		Boolean protLWC = false;

		if (plugin.hasPerm(player, "tombstone.lwc", false)) {
			prot = tombStoneHelper.activateLWC(player, tStoneBlock);
		}

		tStoneBlock.setLwcEnabled(prot);

		if (prot) {
			protLWC = true;
		}

//      Protect the chest with BlockLocker if installed, enabled, and
//      unprotected.
		if (plugin.hasPerm(player, "tombstone.blocklocker", false)) {
			prot = tombStoneHelper.protectWithBlockLocker(player, tStoneBlock);
		}

//      Add tombstone to list
		tombStoneHelper.offerTombStoneList(tStoneBlock);

//      Add tombstone blocks to tombStoneBlockList
		tombStoneHelper.putTombStoneBlockList(tStoneBlock.getBlock().getLocation(), tStoneBlock);

		if (tStoneBlock.getLBlock() != null) {
			tombStoneHelper.putTombStoneBlockList(tStoneBlock.getLBlock().getLocation(), tStoneBlock);
		}

		if (tStoneBlock.getSign() != null) {
			tombStoneHelper.putTombStoneBlockList(tStoneBlock.getSign().getLocation(), tStoneBlock);
		}

//      Add tombstone to player lookup list
		ArrayList<TombStoneBlock> pList = tombStoneHelper.getPlayerTombStoneList(player.getName());

		if (pList == null) {
			pList = new ArrayList<TombStoneBlock>();
			tombStoneHelper.putPlayerTombStoneList(player.getName(), pList);
		}

		pList.add(tStoneBlock);
		tombStoneHelper.saveTombStoneList(player.getWorld().getName());

//      Next get the players inventory using the getDrops() method.
		for (Iterator<ItemStack> iter = deathDrops.listIterator(); iter.hasNext(); ) {
			ItemStack item = iter.next();

			if (item == null) {
				continue;
			}

//          Take the chest(s)
			if ((removeChestCount > 0) && (item.getType() == Material.CHEST)) {
				if (item.getAmount() >= removeChestCount) {
					item.setAmount(item.getAmount() - removeChestCount);
					removeChestCount = 0;
				} else {
					removeChestCount -= item.getAmount();
					item.setAmount(0);
				}

				if (item.getAmount() == 0) {
					iter.remove();

					continue;
				}
			}

//          Take a sign
			if ((removeSign > 0) && (isSign(item.getType()))) {
				item.setAmount(item.getAmount() - 1);
				removeSign = 0;

				if (item.getAmount() == 0) {
					iter.remove();

					continue;
				}
			}

//          Add items to chest if not full.
			if (slot < maxSlot) {
				if (slot >= sChest.getInventory().getSize()) {
					if (lChest == null) {
						continue;
					}

					lChest.getInventory().setItem(slot % sChest.getInventory().getSize(), item);
				} else {
					sChest.getInventory().setItem(slot, item);
				}

				iter.remove();
				slot++;
			} else if (removeChestCount == 0) {
				break;
			}
		}

//      Tell the player how many items went into chest.
		String msg = "Inventory stored in chest. ";

		if (deathDrops.size() > 0) {
			msg += deathDrops.size() + " items wouldn't fit in chest.";
		}

		plugin.sendMessage(player, msg);
		log.debug(player.getName() + " " + msg);

		if (prot && protLWC) {
			plugin.sendMessage(player,
					"Chest protected with LWC. " + config.getRemoveTombStoneSecurityTimeOut()
							+ "s before chest is unprotected.");
			log.debug(player.getName() + " Chest protected with LWC. " + config.getRemoveTombStoneSecurityTimeOut()
					+ "s before chest is unprotected.");
		}

		if (prot && !protLWC) {
			plugin.sendMessage(player,
					"Chest protected with BlockLocker. " + config.getRemoveTombStoneSecurityTimeOut()
							+ "s before chest is unprotected.");
			log.debug(player.getName() + " Chest protected with BlockLocker.");
		}

		if (config.isRemoveTombStone()) {
			plugin.sendMessage(player,
					"Chest will break in " + config.getRemoveTombStoneTime()
							+ "s unless an override is specified.");
			log.debug(player.getName() + " Chest will break in " + config.getRemoveTombStoneTime() + "s");
		}

		if (config.isRemoveTombStoneWhenEmpty() && config.isKeepTombStoneUntilEmpty()) {
			plugin.sendMessage(
					player,
					"Break override: Your tombstone will break when it is emptied, but will not break until then.");
		} else {
			if (config.isRemoveTombStoneWhenEmpty()) {
				plugin.sendMessage(player, "Break override: Your tombstone will break when it is emptied.");
			}

			if (config.isKeepTombStoneUntilEmpty()) {
				plugin.sendMessage(player, "Break override: Your tombstone will not break until it is empty.");
			}
		}
	}

	/**
	 * Method description
	 *
	 * @param player
	 * @param loc
	 *
	 * @return
	 */
	private Block returnGoodPlace(Player player, Location loc) {
		Block block = player.getWorld().getBlockAt(loc);

		// If we run into something we don't want to destroy, go one up.
		if (isSign(block.getType())
				|| (block.getType() == Material.TORCH) || (block.getType() == Material.DETECTOR_RAIL)
				|| (block.getType() == Material.REDSTONE_WIRE) || (block.getType() == Material.ACTIVATOR_RAIL)
				|| isPlate(block.getType())
				|| (block.getType() == Material.REDSTONE_TORCH) || (block.getType() == Material.CAKE)
				|| (block.getType() == Material.RAIL) || (block.getType() == Material.POWERED_RAIL)) {
			block = player.getWorld().getBlockAt(loc.getBlockX(), loc.getBlockY() + 1, loc.getBlockZ());
		}

		if (config.isShouldOnlyUseAirToCreate()) {
			if (isNotAir(block)) {
				World world = player.getWorld();
				int x = loc.getBlockX();
				int y = loc.getBlockY();
				int z = loc.getBlockZ();
				Block otherBlock = world.getBlockAt(x + 1, y, z);

				if (isNotAir(otherBlock)) {
					otherBlock = world.getBlockAt(x - 1, y, z);
				}

				if (isNotAir(otherBlock)) {
					otherBlock = world.getBlockAt(x, y, z - 1);
				}

				if (isNotAir(otherBlock)) {
					otherBlock = world.getBlockAt(x, y, z + 1);
				}

				if (!isNotAir(otherBlock)) {
					block = otherBlock;
				}
			}
		}

		return block;
	}

	//~--- get methods --------------------------------------------------------

	/**
	 * Method description
	 *
	 * @param block
	 *
	 * @return
	 */
	private boolean isNotAir(Block block) {
		return (block.getType() != Material.AIR);
	}

	//~--- methods ------------------------------------------------------------

	/**
	 * Method description
	 *
	 * @param deathDetail
	 */
	private void UpdateTomb(DeathDetail deathDetail) {
		Player player = deathDetail.getPlayer();

		if (tombWorker.hasTomb(player.getName())) {
			log.debug("UpdateTomb");

			Tomb tomb = tombWorker.getTomb(player.getName());

			log.debug("tomb", tomb);

			String signtext;

			if (deathDetail.isPVPDeath()) {
				signtext = "By " + deathDetail.getKiller().getName();
			} else {
				signtext = tombMessages.getMessage(deathDetail.getCauseOfDeath());
			}

			int deathLimit = config.getMaxDeaths();

			if(!config.getUseServerDeathStatistics())
			{
				tomb.addDeath();
			}
			else
			{
				try
				{
					tomb.setDeaths(deathDetail.getPlayer().getStatistic(Statistic.DEATHS));
				}
				catch(IllegalArgumentException e)
				{
					log.warning("Failure to get death statistics for "+deathDetail.getPlayer().getName());
				}
			}

			if ((deathLimit != 0) && (tomb.getDeaths() % deathLimit) == 0) {
				tomb.resetTombBlocks();
				player.sendMessage(tombWorker.graveDigger + "You've reached the number of deaths before Tomb reset.("
						+ ChatColor.DARK_RED + deathLimit + ChatColor.WHITE
						+ ") All your tombs are now destroyed.");
			} else {
				tomb.setReason(signtext);
				tomb.setDeathLoc(player.getLocation(), player.getWorld().getName());
				tomb.updateDeath();
			}
		}
	}

	/**
	 * Method description
	 *
	 * @param deathDetail
	 */
	private void ShowDeathSign(DeathDetail deathDetail) {

		// place sign
		Block signBlock = returnGoodPlace(deathDetail.getPlayer(), deathDetail.getPlayer().getLocation());

		signBlock = tombStoneHelper.findPlace(signBlock, false);
		if (signBlock != null) {
			log.debug("SignBlock at location: ", signBlock.getLocation());
		}

		//WorldGuard 7.0
		if (signBlock != null && plugin.isWorldGuardEnabled())
		{
			LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(deathDetail.getPlayer());
			RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
			RegionQuery query = container.createQuery();
			if(!query.testState(BukkitAdapter.adapt(signBlock.getLocation()), localPlayer, Flags.BUILD))
			{
				log.debug(deathDetail.getPlayer().getName() + " died in WorldGuard Region, not creating DeathSign");
				signBlock = null;
			}
		}

		if (signBlock == null) {
			deathDetail.getPlayer().sendMessage("We will remember you, even without a Deathsign!");
			log.informational("Couldn't create a deathsign for player " + deathDetail.getPlayer().getName()
					+ " at location " + deathDetail.getPlayer().getLocation().toString());

			return;
		}

		signBlock.setType(Material.OAK_SIGN);

		BlockState state = signBlock.getState();

		if (state instanceof Sign) {
			log.debug("Creating DeathSign at: ", signBlock.getLocation());
			final Sign sign = (Sign) state;
			String date = new SimpleDateFormat(config.getDateFormat()).format(new Date());
			String time = new SimpleDateFormat(config.getTimeFormat()).format(new Date());
			String name = deathDetail.getPlayer().getName();
			String reason;

			if (deathDetail.isPVPDeath()) {
				reason = tombMessages.getPvpMessage(deathDetail.getKiller().getName());
			} else {
				reason = tombMessages.getMessage(deathDetail.getCauseOfDeath());
			}

			String[] signMessage = config.getTombStoneSign();

			for (int x = 0; x < 4; x++) {
				String line = signMessage[x];

				line = line.replace("{name}", name);
				line = line.replace("{date}", date);
				line = line.replace("{time}", time);
				line = line.replace("{reason}", reason);

				if (line.length() > 15) {
					line = line.substring(0, 15);
				}
				log.debug("Writing line " + x + ": " + line);
				sign.setLine(x, line);
			}
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				public void run() {
					sign.update();
				}
			});
		}
	}

//  Helper Methods

	/**
	 * Method description
	 *
	 * @param signBlock
	 * @param deathDetail
	 */
	private void createSign(Block signBlock, DeathDetail deathDetail) {
		String date = new SimpleDateFormat(config.getDateFormat()).format(new Date());
		String time = new SimpleDateFormat(config.getTimeFormat()).format(new Date());
		String name = deathDetail.getPlayer().getName();
		String reason;

		if (deathDetail.isPVPDeath()) {
			reason = tombMessages.getPvpMessage(deathDetail.getKiller().getName());
		} else {
			reason = tombMessages.getMessage(deathDetail.getCauseOfDeath());
		}

		signBlock.setType(Material.OAK_SIGN);

		final Sign sign = (Sign) signBlock.getState();
		String[] signMessage = config.getTombStoneSign();

		for (int x = 0; x < 4; x++) {
			String line = signMessage[x];

			line = line.replace("{name}", name);
			line = line.replace("{date}", date);
			line = line.replace("{time}", time);
			line = line.replace("{reason}", reason);

			if (line.length() > 15) {
				line = line.substring(0, 15);
			}

			sign.setLine(x, line);
		}

		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				sign.update();
			}
		});
	}

	/**
	 * Method description
	 *
	 * @param base
	 *
	 * @return
	 */
	Block findLarge(Block base) {

//      Check all 4 sides for air.
		Block exp;

		exp = base.getWorld().getBlockAt(base.getX() - 1, base.getY(), base.getZ());
		if (tombStoneHelper.canReplace(exp.getType()) && (config.isAllowInterfere() || !checkChest(exp))) {
			return exp;
		}

		exp = base.getWorld().getBlockAt(base.getX(), base.getY(), base.getZ() - 1);
		if (tombStoneHelper.canReplace(exp.getType()) && (config.isAllowInterfere() || !checkChest(exp))) {
			return exp;
		}

		exp = base.getWorld().getBlockAt(base.getX() + 1, base.getY(), base.getZ());
		if (tombStoneHelper.canReplace(exp.getType()) && (config.isAllowInterfere() || !checkChest(exp))) {
			return exp;
		}

		exp = base.getWorld().getBlockAt(base.getX(), base.getY(), base.getZ() + 1);
		if (tombStoneHelper.canReplace(exp.getType()) && (config.isAllowInterfere() || !checkChest(exp))) {
			return exp;
		}

		return null;
	}

	/**
	 * Method description
	 *
	 * @param base
	 *
	 * @return
	 */
	boolean checkChest(Block base) {

//      Check all 4 sides for a chest.
		Block exp;

		exp = base.getWorld().getBlockAt(base.getX() - 1, base.getY(), base.getZ());

		if (exp.getType() == Material.CHEST) {
			return true;
		}

		exp = base.getWorld().getBlockAt(base.getX(), base.getY(), base.getZ() - 1);

		if (exp.getType() == Material.CHEST) {
			return true;
		}

		exp = base.getWorld().getBlockAt(base.getX() + 1, base.getY(), base.getZ());

		if (exp.getType() == Material.CHEST) {
			return true;
		}

		exp = base.getWorld().getBlockAt(base.getX(), base.getY(), base.getZ() + 1);

		if (exp.getType() == Material.CHEST) {
			return true;
		}

		return false;
	}
}
