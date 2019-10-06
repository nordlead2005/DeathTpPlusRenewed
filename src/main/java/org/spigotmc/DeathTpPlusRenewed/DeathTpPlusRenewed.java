package org.spigotmc.DeathTpPlusRenewed;

//~--- non-JDK imports --------------------------------------------------------

/**
 * PluginName: DeathTpPlusRenewed
 * Class: DeathTpPlusRenewed
 * User: DonRedhorse
 * Date: 18.10.11
 * Time: 22:33
 * based on:
 * DeathTpPlusRenewed from lonelydime
 * and material from
 * an updated fork of Furt https://github.com/Furt of
 * Cenopath - A Dead Man's Chest plugin for Bukkit
 * By Jim Drey (Southpaw018) <moof@moofit.com>
 * and material from
 * Tomb a plugin from Belphemur https://github.com/Belphemur/Tomb
 * Original Copyright (C) of DeathTp 2011 Steven "Drakia" Scott <Drakia@Gmail.com>
 */

import java.util.HashMap;

import com.garbagemule.MobArena.MobArenaHandler;
import com.griefcraft.lwc.LWCPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import me.ryanhamshire.GriefPrevention.GriefPrevention;

import net.milkbowl.vault.economy.Economy;
import nl.rutgerkok.blocklocker.BlockLockerPlugin;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.dynmap.DynmapAPI;
import org.spigotmc.DeathTpPlusRenewed.commons.ConfigManager;
import org.spigotmc.DeathTpPlusRenewed.commons.DefaultLogger;
import org.spigotmc.DeathTpPlusRenewed.commons.DynMapHelper;
import org.spigotmc.DeathTpPlusRenewed.commons.listeners.ServerListener;
import org.spigotmc.DeathTpPlusRenewed.death.DeathMessages;
import org.spigotmc.DeathTpPlusRenewed.death.commands.DeathsCommand;
import org.spigotmc.DeathTpPlusRenewed.death.commands.KillsCommand;
import org.spigotmc.DeathTpPlusRenewed.death.commands.ReportCommand;
import org.spigotmc.DeathTpPlusRenewed.death.commands.StreakCommand;
import org.spigotmc.DeathTpPlusRenewed.death.commands.TopCommand;
import org.spigotmc.DeathTpPlusRenewed.death.events.listeners.EntityListener;
import org.spigotmc.DeathTpPlusRenewed.death.events.listeners.StreakListener;
import org.spigotmc.DeathTpPlusRenewed.death.persistence.DeathRecordDao;
import org.spigotmc.DeathTpPlusRenewed.death.persistence.StreakRecordDao;
import org.spigotmc.DeathTpPlusRenewed.teleport.commands.DeathTpCommand;
import org.spigotmc.DeathTpPlusRenewed.teleport.persistence.DeathLocationDao;
import org.spigotmc.DeathTpPlusRenewed.tomb.TombMessages;
import org.spigotmc.DeathTpPlusRenewed.tomb.TombStoneHelper;
import org.spigotmc.DeathTpPlusRenewed.tomb.commands.AdminCommand;
import org.spigotmc.DeathTpPlusRenewed.tomb.commands.FindCommand;
import org.spigotmc.DeathTpPlusRenewed.tomb.commands.ListCommand;
import org.spigotmc.DeathTpPlusRenewed.tomb.commands.ResetCommand;
import org.spigotmc.DeathTpPlusRenewed.tomb.commands.TimeCommand;
import org.spigotmc.DeathTpPlusRenewed.tomb.events.listeners.BlockListener;
import org.spigotmc.DeathTpPlusRenewed.tomb.events.listeners.PlayerListener;
import org.spigotmc.DeathTpPlusRenewed.tomb.workers.TombStoneWorker;
import org.spigotmc.DeathTpPlusRenewed.tomb.workers.TombWorker;

//~--- JDK imports ------------------------------------------------------------

//~--- classes ----------------------------------------------------------------

//Register
// importing commands and listeners
//importing BlockLocker
// importing LWC

/**
 * Class description
 * <p/>
 * todo   Make sure to change the version
 * @author DonRedhorse
 * @version 3.8.0.1818, 25.01.2012
 */
public class DeathTpPlusRenewed extends JavaPlugin {
	/**
	 * Field description
	 */
	private static Server server = null;
	/**
	 * Field description
	 */
	private static DeathLocationDao deathLocationLog;
	/**
	 * Field description
	 */
	private static DeathRecordDao deathLog;
	/**
	 * Field description
	 */
	private static DeathTpPlusRenewed instance;
	/**
	 * Field description
	 */
	protected static String pluginPath;
	/**
	 * Field description
	 */
	protected static PluginManager pm;
	/**
	 * Field description
	 */
	private static StreakRecordDao streakLog;

	//~--- fields -------------------------------------------------------------
	/**
	 * Field description
	 */
	private BlockLockerPlugin blocklockerPlugin = null;
	/**
	 * Field description
	 */
	private Economy economy = null;
	/**
	 * Field description
	 */
	private LWCPlugin lwcPlugin = null;

	// plugin variables
	/**
	 * Field description
	 */
	private DeathTpPlusRenewed plugin = this;
	/**
	 * Field description
	 */
	private boolean worldTravel = false;

	// Vault
	/**
	 * Field description
	 */
	private boolean useVault = false;
	/**
	 * Field description
	 */
	private boolean mobArenaEnabled = false;
	/**
	 * Field description
	 */
	private boolean economyActive = false;

	// DynMap
	/**
	 * Field description
	 */
	private boolean dynmapEnabled = false;
	/**
	 * Field description
	 */
	private boolean dynmapActive = false;
	/**
	 * Field description
	 */
	protected HashMap<String, EntityDamageEvent> deathCause = new HashMap<String, EntityDamageEvent>();
	/**
	 * Field description
	 */
	private ConfigManager config;
	/**
	 * Field description
	 */
	private FileConfiguration configuration;
	/**
	 * Field description
	 */
	private DeathMessages deathMessages;
	/**
	 * Field description
	 */
	private DynMapHelper dynMapHelper;
	/**
	 * Field description
	 */
	private Plugin dynmap;
	/**
	 * Field description
	 */
	private DynmapAPI dynmapAPI;
	/**
	 * Field description
	 */
	private DefaultLogger log;
	/**
	 * Field description
	 */
	private String lwcPluginVersion;
	/**
	 * Field description
	 */
	private MobArenaHandler maHandler;
	/**
	 * Field description
	 */
	private TombMessages tombMessages;
	/**
	 * Field description
	 */
	private TombStoneHelper tombStoneHelper;
	private WorldGuardPlugin worldGuardPlugin;
	private boolean worldGuardEnabled = false;

	private GriefPrevention griefPreventionPlugin;
	private boolean griefPreventionEnabled = false;

	//~--- methods ------------------------------------------------------------

	/**
	 * Method description
	 */
	public void onDisable() {
		for (World w : getServer().getWorlds()) {
			tombStoneHelper.saveTombStoneList(w.getName());
		}

		if (config.isEnableTomb()) {
			TombWorker.getInstance().save();
			server.getScheduler().cancelTasks(this);
		}

		deathLocationLog.save();
		deathLog.save();
		streakLog.save();

		log.disableMsg();
	}

	/**
	 * Method description
	 */
	public void onEnable() {
		instance = this;
		log = DefaultLogger.getInstance(this);
		config = ConfigManager.getInstance();
		config.setupConfig(configuration, plugin);
		deathMessages = DeathMessages.getInstance();
		deathMessages.setupDeathMessages(plugin);
		tombMessages = TombMessages.getInstance();
		tombMessages.setupTombMessages(plugin);
		pluginPath = getDataFolder() + System.getProperty("file.separator");
		deathLocationLog = new DeathLocationDao(this);
		deathLog = new DeathRecordDao(this);
		streakLog = new StreakRecordDao(this);
		tombStoneHelper = TombStoneHelper.getInstance();
		pm = this.getServer().getPluginManager();

//      register entityListener
		Bukkit.getPluginManager().registerEvents(new EntityListener(this), this);
		Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);
		Bukkit.getPluginManager().registerEvents(new BlockListener(this), this);
		Bukkit.getPluginManager().registerEvents(new StreakListener(this), this);

		// register entityListener for Enable Tombstone or Tomb
		if (config.isEnableTombStone() || config.isEnableTomb()) {
			lwcPlugin = (LWCPlugin) checkPlugin("LWC");
			blocklockerPlugin = (BlockLockerPlugin) checkPlugin("BlockLocker");
		}

		// register entityListener for Enable Tomb
		if (config.isEnableTomb()) {

			// ToDo check if this is really needed
			// pm.registerEvent(Event.Type.WORLD_SAVE, worldSaveListener, Priority.Normal, this);
			server = getServer();
			TombWorker.getInstance().setPluginInstance(this);
			TombWorker.getInstance().load();
		}

		// Register Server Listener
		Bukkit.getPluginManager().registerEvents(new ServerListener(this), this);

		// reading in Tomblist
		for (World w : getServer().getWorlds()) {
			tombStoneHelper.loadTombStoneList(w.getName());
		}

		// starting Removal Thread
		if (config.isRemoveTombStoneSecurity() || config.isRemoveTombStone()) {
			getServer().getScheduler().scheduleSyncRepeatingTask(this, new TombStoneWorker(plugin), 0L,
					100L);
		}

		// registering commands
		this.addCommands();

		// print success
		log.enableMsg();
	}

	//~--- get methods --------------------------------------------------------

	/**
	 * Method description
	 * @return
	 */
	public static Server getBukkitServer() {
		return server;
	}

	public boolean isWorldGuardEnabled() {
		return worldGuardEnabled;
	}

	public WorldGuardPlugin getWorldGuardPlugin() {
		return worldGuardPlugin;
	}

	public boolean isGriefPreventionEnabled()
	{
		return griefPreventionEnabled;
	}

	public GriefPrevention getGriefPrevention()
	{
		return griefPreventionPlugin;
	}

	//~--- methods ------------------------------------------------------------

	/*
     *    Check if a plugin is loaded/enabled already. Returns the plugin if so,
     *    null otherwise
     */

	/**
	 * Method description
	 * @param p
	 * @return
	 */
	private Plugin checkPlugin(String p) {
		Plugin plugin = pm.getPlugin(p);

		return checkPlugin(plugin);
	}

	/**
	 * Method description
	 * @param plugin
	 * @return
	 */
	public Plugin checkPlugin(Plugin plugin) {
		if ((plugin != null) && plugin.isEnabled()) {
			String pluginName = plugin.getDescription().getName();
			String pluginVersion = plugin.getDescription().getVersion();

			log.info("Found " + pluginName + " (v" + pluginVersion + ")");

			if (pluginName.equalsIgnoreCase("LWC")) {
				setLwcPlugin((LWCPlugin) plugin);
				setLwcPluginVersion(pluginVersion);
				log.debug("lwcVersion ", pluginVersion);
			}

			if (pluginName.equalsIgnoreCase("BlockLocker")) {
				setBlockLockerPlugin((BlockLockerPlugin) plugin);
			}

			if (config.isEnableBlockLocker() && (getBlockLockerPlugin() == null)) {
				log.warning("is configured to use BlockLocker, but BlockLocker wasn't found yet!");
				log.warning("Still waiting for BlockLocker to become active!");
			}

			if (config.isEnableLWC() && (getLwcPlugin() == null)) {
				log.warning("is configured to use LWC, but LWC wasn't found yet!");
				log.warning("Still waiting for LWC to become active!");
			}

			return plugin;
		}

		return null;
	}

	//~--- get methods --------------------------------------------------------

	/**
	 * Method description
	 * @return
	 */
	public boolean isDynmapActive() {
		return dynmapActive;
	}

	//~--- set methods --------------------------------------------------------

	/**
	 * Method description
	 * @param dynmapActive
	 */
	public void setDynmapActive(boolean dynmapActive) {
		this.dynmapActive = dynmapActive;
	}

	//~--- get methods --------------------------------------------------------

	/**
	 * Method description
	 * @return
	 */
	public DynmapAPI getDynmapAPI() {
		return dynmapAPI;
	}

	//~--- set methods --------------------------------------------------------

	/**
	 * Method description
	 * @param dynmapAPI
	 */
	public void setDynmapAPI(DynmapAPI dynmapAPI) {
		this.dynmapAPI = dynmapAPI;
	}

	//~--- get methods --------------------------------------------------------

	/**
	 * Method description
	 * @return
	 */
	public DynMapHelper getDynMapHelper() {
		return dynMapHelper;
	}

	//~--- set methods --------------------------------------------------------

	/**
	 * Method description
	 * @param dynMapHelper
	 */
	public void setDynMapHelper(DynMapHelper dynMapHelper) {
		this.dynMapHelper = dynMapHelper;
	}

	/**
	 * Method description
	 * @param dynMap
	 */
	public void setDynMap(Plugin dynMap) {
		this.dynmap = dynMap;
	}

	//~--- get methods --------------------------------------------------------

	/**
	 * Method description
	 * @return
	 */
	public Plugin getDynmap() {
		return dynmap;
	}

	/**
	 * Method description
	 * @return
	 */
	public boolean isDynmapEnabled() {
		return dynmapEnabled;
	}

	//~--- set methods --------------------------------------------------------

	/**
	 * Method description
	 * @param dynmapEnabled
	 */
	public void setDynmapEnabled(boolean dynmapEnabled) {
		this.dynmapEnabled = dynmapEnabled;
	}

	//~--- get methods --------------------------------------------------------

	/**
	 * Method description
	 * @return
	 */
	public MobArenaHandler getMaHandler() {
		return maHandler;
	}

	//~--- set methods --------------------------------------------------------

	/**
	 * Method description
	 * @param maHandler
	 */
	public void setMaHandler(MobArenaHandler maHandler) {
		this.maHandler = maHandler;
	}

	//~--- get methods --------------------------------------------------------

	/**
	 * Method description
	 * @return
	 */
	public boolean isMobArenaEnabled() {
		return mobArenaEnabled;
	}

	//~--- set methods --------------------------------------------------------

	/**
	 * Method description
	 * @param mobArenaEnabled
	 */
	public void setMobArenaEnabled(boolean mobArenaEnabled) {
		this.mobArenaEnabled = mobArenaEnabled;
	}

	//~--- get methods --------------------------------------------------------

	/**
	 * Method description
	 * @return
	 */
	public Economy getEconomy() {
		return economy;
	}

	//~--- set methods --------------------------------------------------------

	/**
	 * Method description
	 * @param worldTravel
	 */
	public void setWorldTravel(boolean worldTravel) {
		this.worldTravel = worldTravel;
	}

	/**
	 * Method description
	 * @param economyActive
	 */
	public void setEconomyActive(boolean economyActive) {
		this.economyActive = economyActive;
	}

	/**
	 * Method description
	 * @param useVault
	 */
	public void setUseVault(boolean useVault) {
		this.useVault = useVault;
	}

	/**
	 * Method description
	 * @param economy
	 */
	public void setEconomy(Economy economy) {
		this.economy = economy;
	}

	//~--- get methods --------------------------------------------------------

	/**
	 * Method description
	 * @return
	 */
	public boolean isWorldTravel() {
		return worldTravel;
	}

	/**
	 * Method description
	 * @return
	 */
	public static DeathTpPlusRenewed getPlugin() {
		return instance;
	}

	/**
	 * Method description
	 * @return
	 */
	public HashMap<String, EntityDamageEvent> getDeathCause() {
		return deathCause;
	}

	/**
	 * Method description
	 * @return
	 */
	public boolean isEconomyActive() {
		return economyActive;
	}

	/**
	 * Method description
	 * @return
	 */
	public static DeathLocationDao getDeathLocationLog() {
		return deathLocationLog;
	}

	/**
	 * Method description
	 * @return
	 */
	public static DeathRecordDao getDeathLog() {
		return deathLog;
	}

	/**
	 * Method description
	 * @return
	 */
	public static StreakRecordDao getStreakLog() {
		return streakLog;
	}

	/**
	 * Method description
	 * @return
	 */
	public LWCPlugin getLwcPlugin() {
		return lwcPlugin;
	}

	/**
	 * Method description
	 * @return
	 */
	public BlockLockerPlugin getBlockLockerPlugin() {
		return blocklockerPlugin;
	}

	/**
	 * Method description
	 * @return
	 */
	public boolean isUseVault() {
		return useVault;
	}

	//~--- set methods --------------------------------------------------------

	/**
	 * Method description
	 * @param lwcPlugin
	 */
	public void setLwcPlugin(LWCPlugin lwcPlugin) {
		this.lwcPlugin = lwcPlugin;
	}

	/**
	 * Method description
	 * @param BlockLockerPlugin
	 */
	public void setBlockLockerPlugin(BlockLockerPlugin plugin) {
		blocklockerPlugin = plugin;
	}

	//~--- methods ------------------------------------------------------------

	//  Register commands

	/**
	 * Method description
	 */
	private void addCommands() {
		getCommand("dtplist").setExecutor(new ListCommand(this));
		getCommand("dtpfind").setExecutor(new FindCommand(this));
		getCommand("dtptime").setExecutor(new TimeCommand(this));
		getCommand("dtpreset").setExecutor(new ResetCommand(this));
		getCommand("dtpadmin").setExecutor(new AdminCommand(this));
		getCommand("deathtp").setExecutor(new DeathTpCommand(this));
		getCommand("deaths").setExecutor(new DeathsCommand(this));
		getCommand("kills").setExecutor(new KillsCommand(this));
		getCommand("streak").setExecutor(new StreakCommand(this));
		getCommand("dtpreport").setExecutor(new ReportCommand(this));
		getCommand("dtptop").setExecutor(new TopCommand(this));
	}

	//~--- get methods --------------------------------------------------------

	/*
			  *    Check whether the player has the given permissions.
			  */

	/**
	 * Method description
	 * @param sender
	 * @param label
	 * @param consoleUse
	 * @return
	 */
	public boolean hasPerm(CommandSender sender, String label, boolean consoleUse) {
		boolean perm = sender.hasPermission("deathtpplusrenewed." + label);

		if (this.console(sender)) {
			if (consoleUse) {
				return true;
			}

			log.warning("This command cannot be used in console.");

			return false;
		} else {
			if (sender.isOp()) {
				return true;
			}

			return perm;
		}
	}

	//~--- methods ------------------------------------------------------------

	/**
	 * Method description
	 * @param sender
	 * @return
	 */
	boolean console(CommandSender sender) {
		return !(sender instanceof Player);
	}

	/**
	 * Method description
	 * @param p
	 * @param msg
	 */
	public void sendMessage(Player p, String msg) {
		if (!config.isShowTombStoneStatusMessage()) {
			return;
		}

		p.sendMessage(msg);
	}

	/**
	 * Method description
	 * @param p
	 * @param msg
	 */
	public void sendMessage(CommandSender p, String msg) {
		if (!config.isShowTombStoneStatusMessage()) {
			return;
		}

		p.sendMessage(msg);
	}

	//~--- set methods --------------------------------------------------------

	/**
	 * Method description
	 * @param lwcPluginVersion
	 */
	public void setLwcPluginVersion(String lwcPluginVersion) {
		this.lwcPluginVersion = lwcPluginVersion;
	}

	//~--- get methods --------------------------------------------------------

	/**
	 * Method description
	 * @return
	 */
	public boolean isLWC4() {
		if (lwcPluginVersion.startsWith("3")) {
			return false;
		}

		return true;
	}

	public void setWorldGuardEnabled(boolean b) {
		worldGuardEnabled = b;
	}

	public void setWorldGuardPlugin(WorldGuardPlugin worldGuardPlugin) {
		this.worldGuardPlugin = worldGuardPlugin;
	}

	public void setGriefPreventionEnabled(boolean b)
	{
		griefPreventionEnabled = b;
	}

	public void setGriefPreventionPlugin(GriefPrevention plugin)
	{
		this.griefPreventionPlugin = plugin;
	}
}
