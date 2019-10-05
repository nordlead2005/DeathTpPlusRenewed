package org.spigotmc.DeathTpPlusRenewed.teleport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.spigotmc.DeathTpPlusRenewed.DeathTpPlusRenewed;
import org.spigotmc.DeathTpPlusRenewed.commons.ConfigManager;
import org.spigotmc.DeathTpPlusRenewed.commons.DefaultLogger;
import org.spigotmc.DeathTpPlusRenewed.teleport.persistence.DeathLocation;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * PluginName: DeathTpPlusRenewed
 * Class: TeleportHelper
 * User: DonRedhorse
 * Date: 04.01.12
 * Time: 15:11
 */
public class TeleportHelper {
    private DeathTpPlusRenewed plugin;
    private DefaultLogger log;
    private ConfigManager config;
    // private List<Integer> saveBlocks = new ArrayList<Integer>(Arrays.asList(new Integer[]{
    //         0, 6, 8, 9, 10, 11, 37, 38, 39, 40, 50, 51, 55, 59, 69, 76
    // }));
    private List<Material> saveBlocks = new ArrayList<Material>(Arrays.asList(new Material[]{
        Material.AIR, Material.SPRUCE_SAPLING, Material.BAMBOO_SAPLING, Material.BIRCH_SAPLING,
        Material.DARK_OAK_SAPLING, Material.JUNGLE_SAPLING, Material.ACACIA_SAPLING, Material.OAK_SAPLING,
        Material.WATER, Material.LAVA, Material.DANDELION, Material.POPPY, Material.BLUE_ORCHID,
        Material.ALLIUM, Material.AZURE_BLUET, Material.RED_TULIP, Material.ORANGE_TULIP, Material.WHITE_TULIP,
        Material.PINK_TULIP, Material.OXEYE_DAISY, Material.BROWN_MUSHROOM, Material.RED_MUSHROOM,
        Material.TORCH, Material.FIRE, Material.REDSTONE_WIRE, Material.WHEAT, Material.RAIL,
        Material.POWERED_RAIL, Material.DETECTOR_RAIL, Material.ACTIVATOR_RAIL, Material.LEVER,
        Material.REDSTONE_TORCH
    }));

    public TeleportHelper(DeathTpPlusRenewed instance) {
        this.plugin = instance;
        log = DefaultLogger.getLogger();
        config = ConfigManager.getInstance();
    }

    public Boolean canTp(Player player, boolean isDeathTp) {
        boolean canTele = false;
        if (isDeathTp) {
            canTele = hasItem(player) && hasFunds(player);
        } else {
            if(config.isTombRequiresTPCost())
            {
                canTele = hasItem(player) && hasFunds(player);
            }
            else
            {
                canTele = config.isAllowTombAsTeleport();
            }
        }
        return canTele;
    }

    public void registerTp(Player player, boolean deathTp) {
        if (hasItem(player)) {
            if (config.getChargeItem().compareTo("0") != 0) {
                ItemStack itemInHand = player.getInventory().getItemInMainHand();

                if (itemInHand.getAmount() == 1) {
                    player.getInventory().clear(player.getInventory().getHeldItemSlot());
                } else {
                    itemInHand.setAmount(itemInHand.getAmount() - 1);
                    player.getInventory().setItemInMainHand(itemInHand);
                }
            }
        }

        if (hasFunds(player)) {
            double deathTpCost = Double.valueOf(config.getDeathtpCost().trim());
            if (plugin.isEconomyActive() && deathTpCost > 0.0) {
                plugin.getEconomy().withdrawPlayer(player, deathTpCost);
                if(deathTp)
                {
                    player.sendMessage(String.format("You used %s to use /deathtp.", plugin.getEconomy().format(deathTpCost)));
                }
                else
                {
                    player.sendMessage(String.format("The services cost %s.", plugin.getEconomy().format(deathTpCost)));
                }
            }
        }
    }

    private Boolean hasItem(Player player) {
        String chargeItem = config.getChargeItem();
        log.debug("chargeItem", chargeItem);
        // costs item in inventory
        
        boolean hasItem = ((chargeItem.compareTo("0") == 0) || (chargeItem.equalsIgnoreCase(player.getInventory().getItemInMainHand().getType().toString())));
        
        log.debug("hasItem", true);
        if(!hasItem)
        {
            player.sendMessage(String.format("You must be holding a %s to teleport.", chargeItem));
        }
        return hasItem;
    }

    private Boolean hasFunds(Player player) {
        double deathTpCost = Double.valueOf(config.getDeathtpCost().trim());
        log.debug("deathTpCost", deathTpCost);
        if (deathTpCost == 0) {
            return true;
        }

        // costs economy
        if (plugin.isEconomyActive()) {
            log.debug("isEconomyActive", "yes");
            if (plugin.getEconomy().getBalance(player) > deathTpCost) {
                log.debug("hasFunds", true);
                return true;
            } else {
                player.sendMessage(String.format("You need %s money to use /deathtp.", plugin.getEconomy().format(deathTpCost)));
                return false;
            }
        }
        return true;
    }

    // Code from Tele++
    public Location saveDeathLocation(DeathLocation locationRecord, World world) {
        log.debug("world", world);
        double x = locationRecord.getLocation().getBlockX();
        double y = locationRecord.getLocation().getBlockY();
        double z = locationRecord.getLocation().getBlockZ();
        log.debug("x,y,z:", x + "," + y + "," + z);

        x = x + .5D;
        z = z + .5D;

        if (y < 1.0D) {
            y = 1.0D;
        }

        while (blockIsAboveAir(world, x, y, z)) {
            y -= 1.0D;

            if (y < -512) {
                return null;
            }
        }

        while (!blockIsSafe(world, x, y, z)) {
            y += 1.0D;

            if (y > world.getMaxHeight()) {
                return null;
            }
        }
        if (y < 0) {

        }
        Location saveDeathLocation = new Location(world, x, y, z);
        log.debug("saveDeathLocation", saveDeathLocation);
        return saveDeathLocation;
    }

    private boolean blockIsAboveAir(World world, double x, double y, double z) {
        Material mat = world.getBlockAt((int) Math.floor(x), (int) Math.floor(y - 1.0D), (int) Math.floor(z)).getType();

        return saveBlocks.contains(mat);
    }

    public boolean blockIsSafe(Block block) {
        return blockIsSafe(block.getWorld(), block.getX(), block.getY(), block.getZ());
    }

    public boolean blockIsSafe(World world, double x, double y, double z) {
        Material mat1 = world.getBlockAt((int) Math.floor(x), (int) Math.floor(y), (int) Math.floor(z)).getType();
        Material mat2 = world.getBlockAt((int) Math.floor(x), (int) Math.floor(y + 1.0D), (int) Math.floor(z)).getType();

        return (saveBlocks.contains(mat1)) && (saveBlocks.contains(mat2));
    }

    public boolean canGoBetween(String thisWorld, World deathWorld, Player player) {
        boolean canGoBetween = false;

        if (!thisWorld.equals(deathWorld.getName())) {
            if ((player.hasPermission("deathtpplusrenewed.worldtravel") && config.getAllowWorldTravel().equalsIgnoreCase("permissions")) || config.getAllowWorldTravel().equalsIgnoreCase("yes")) {
                canGoBetween = true;
            }
        } else {
            canGoBetween = true;
        }
        return canGoBetween;
    }

    public Location findTeleportLocation(DeathLocation locationRecord, Player player) {

        log.debug("locationRecord", locationRecord);
        Location deathLocation = locationRecord.getLocation();
        log.debug("deathLocation", deathLocation);
        World deathWorld = player.getServer().getWorld(locationRecord.getWorldName());
        // check if world still exists otherwise display message and exit
        if (deathWorld == null) {
            log.debug("World: " + locationRecord.getWorldName() + " doesn't exist anymore");
            player.sendMessage("The deathlocation is in a world which is no more! RIP: " + locationRecord.getWorldName());
            return null;
        }

        // Added chunkload when chunk not loaded, code from Tele++
        int cx = deathLocation.getBlockX() >> 4;
        int cz = deathLocation.getBlockZ() >> 4;

        if (!deathWorld.isChunkLoaded(cx, cz)) {
            log.debug("Chunk at x: " + cx + " z: " + cz + " is not loaded, forcing load");
            deathWorld.loadChunk(cx, cz);
            if (!deathWorld.isChunkLoaded(cx, cz)) {
                log.severe("Chunk at x: " + cx + " z: " + cz + " is still not loaded");
            }
        }

        if (config.isTeleportToHighestBlock()) {

            Location yLocation = deathWorld.getHighestBlockAt(locationRecord.getLocation().getBlockX(), locationRecord.getLocation().getBlockZ()).getLocation();

            log.debug("yLocation", yLocation);
            int y = yLocation.getBlockY() + 2;
            int z = yLocation.getBlockZ();
            int x = yLocation.getBlockX();
            if (y < 2) {
                y = deathWorld.getMaxHeight() - 2;
            }
            if (deathWorld.getEnvironment().equals(Environment.NETHER) || (y > deathWorld.getMaxHeight())) {
                player.sendRawMessage("There is no save place to teleport you at location:");
                player.sendRawMessage("x: " + locationRecord.getLocation().getX() + " y: " + locationRecord.getLocation().getY() + " z: " + locationRecord.getLocation().getZ() + " in world: " + locationRecord.getWorldName());
                player.sendRawMessage("Have fun walking... sorry about that..");
                return null;
            }

            deathLocation = deathWorld.getBlockAt(x, y, z).getLocation();
            log.debug("deathLocation", deathLocation);
        } else {
            deathLocation = saveDeathLocation(locationRecord, deathWorld);
            if (deathLocation == null) {
                player.sendRawMessage("There is no save place to teleport you at location:");
                player.sendRawMessage("x: " + locationRecord.getLocation().getX() + " y: " + locationRecord.getLocation().getY() + " z: " + locationRecord.getLocation().getZ() + " in world: " + locationRecord.getWorldName());
                player.sendRawMessage("Have fun walking... sorry about that..");
                return null;
            }
        }
        return deathLocation;
    }
}

