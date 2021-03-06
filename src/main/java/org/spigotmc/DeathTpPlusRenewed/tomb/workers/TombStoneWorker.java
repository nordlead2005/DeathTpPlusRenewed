package org.spigotmc.DeathTpPlusRenewed.tomb.workers;

/**
 * PluginName: DeathTpPlusRenewed
 * Class: TombStoneWorker
 * User: DonRedhorse
 * Date: 19.10.11
 * Time: 22:23
 */

import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.spigotmc.DeathTpPlusRenewed.DeathTpPlusRenewed;
import org.spigotmc.DeathTpPlusRenewed.commons.ConfigManager;
import org.spigotmc.DeathTpPlusRenewed.tomb.TombStoneHelper;
import org.spigotmc.DeathTpPlusRenewed.tomb.models.TombStoneBlock;

import java.util.Iterator;

public class TombStoneWorker extends Thread {
    private DeathTpPlusRenewed plugin;
    private ConfigManager config;
    private TombStoneHelper tombStoneHelper;

    public TombStoneWorker(DeathTpPlusRenewed instance) {
        this.plugin = instance;
        config = ConfigManager.getInstance();
        tombStoneHelper = TombStoneHelper.getInstance();
    }

    public void run() {
        long cTime = System.currentTimeMillis() / 1000;
        for (Iterator<TombStoneBlock> iter = tombStoneHelper.getTombStoneList().iterator(); iter
                .hasNext(); ) {
            TombStoneBlock tStoneBlock = iter.next();

            // "empty" option checks - Delete chest if empty
            //  or keep the chest if not empty (ignoring remove security and remove tombstone)
            if (config.isKeepTombStoneUntilEmpty() || config.isRemoveTombStoneWhenEmpty()) {
                if (tStoneBlock.getBlock().getState() instanceof Chest) {
                    int itemCount = 0;

                    Chest sChest = (Chest) tStoneBlock.getBlock().getState();
                    Chest lChest = (tStoneBlock.getLBlock() != null) ? (Chest) tStoneBlock
                            .getLBlock().getState() : null;

                    boolean hasItems = false;
                    for (ItemStack item : sChest.getInventory().getContents()) {
                        if (item != null && (item.getAmount() > 0)) {
                            hasItems = true;
                            break;
                        }
                    }
                    if (!hasItems && lChest != null) {
                        for (ItemStack item : lChest.getInventory()
                                .getContents()) {
                            if (item != null && (item.getAmount() > 0)) {
                                hasItems = true;
                                break;
                            }
                        }
                    }

                    if (config.isKeepTombStoneUntilEmpty() && hasItems) {
                            continue;
                    }
                    if (config.isRemoveTombStoneWhenEmpty()) {
                        if (itemCount == 0) {
                            tombStoneHelper.destroyTombStone(tStoneBlock);
                        }
                        iter.remove(); // TODO bugcheck on this addition
                    }
                }
            }

// Security removal check
            if (config.isRemoveTombStoneSecurity()) {
                Player p = plugin.getServer().getPlayer(tStoneBlock.getOwner());

                if (cTime >= (tStoneBlock.getTime() + Long.parseLong(config.getRemoveTombStoneSecurityTimeOut()))) {
                    if (tStoneBlock.getLwcEnabled() && plugin.getLwcPlugin() != null) {
                        tombStoneHelper.deactivateLWC(tStoneBlock, false);
                        tStoneBlock.setLwcEnabled(false);
                        if (p != null) {
                            plugin.sendMessage(p,
                                    "LWC protection disabled on your tombstone!");
                        }
                    }
                    if (tStoneBlock.getBlockLockerSign() != null
                            && plugin.getBlockLockerPlugin() != null) {
                        tombStoneHelper.deactivateBlockLocker(tStoneBlock);
                        if (p != null) {
                            plugin.sendMessage(p,
                                    "BlockLocker protection disabled on your tombstone!");
                        }
                    }
                }
            }

// Block removal check
            if (config.isRemoveTombStone()
                    && cTime > (tStoneBlock.getTime() + Long.parseLong(config.getRemoveTombStoneTime()))) {
                tombStoneHelper.destroyTombStone(tStoneBlock);
// TODO this originally included
// the only instance of
// removeTombStone(tblock, false).
// check for bugs caused by the
// change to always true.
                iter.remove();
            }
        }
    }
}

