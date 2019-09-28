package org.spigotmc.DeathTpPlusRenewed.tomb.commands;

/**
 * PluginName: DeathTpPlusRenewed
 * Class: ListCommand
 * User: DonRedhorse
 * Date: 19.10.11
 * Time: 22:07
 */

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.spigotmc.DeathTpPlusRenewed.DeathTpPlusRenewed;
import org.spigotmc.DeathTpPlusRenewed.commons.DefaultLogger;
import org.spigotmc.DeathTpPlusRenewed.tomb.TombStoneHelper;
import org.spigotmc.DeathTpPlusRenewed.tomb.models.TombStoneBlock;

import java.util.ArrayList;

public class ListCommand implements CommandExecutor {

    private DeathTpPlusRenewed plugin;
    private DefaultLogger log;
    private TombStoneHelper tombStoneHelper;

    public ListCommand(DeathTpPlusRenewed instance) {
        this.plugin = instance;
        log = DefaultLogger.getLogger();
        tombStoneHelper = TombStoneHelper.getInstance();
        log.informational("dtplist command registered");
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label,
                             String[] args) {
        log.debug("dtplist command executing");
        if (!plugin.hasPerm(sender, "tombstone.list", false)) {
            plugin.sendMessage(sender, "Permission Denied");
            return true;
        }
        ArrayList<TombStoneBlock> pList = tombStoneHelper.getPlayerTombStoneList(sender.getName());
        if (pList == null) {
            plugin.sendMessage(sender, "You have no tombstones.");
            return true;
        }
        plugin.sendMessage(sender, "Tombstone List:");
        int i = 0;
        for (TombStoneBlock tombStone : pList) {
            i++;
            if (tombStone.getBlock() == null) {
                continue;
            }
            int X = tombStone.getBlock().getX();
            int Y = tombStone.getBlock().getY();
            int Z = tombStone.getBlock().getZ();
            plugin.sendMessage(sender, " " + i + " - World: "
                    + tombStone.getBlock().getWorld().getName() + " @(" + X + ","
                    + Y + "," + Z + ")");
        }
        return true;
    }

}

