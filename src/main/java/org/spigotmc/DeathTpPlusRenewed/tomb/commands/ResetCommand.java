package org.spigotmc.DeathTpPlusRenewed.tomb.commands;

/**
 * PluginName: DeathTpPlusRenewed
 * Class: ResetCommand
 * User: DonRedhorse
 * Date: 19.10.11
 * Time: 22:07
 */

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.spigotmc.DeathTpPlusRenewed.DeathTpPlusRenewed;
import org.spigotmc.DeathTpPlusRenewed.commons.DefaultLogger;

public class ResetCommand implements CommandExecutor {

    private DeathTpPlusRenewed plugin;
    private DefaultLogger log;

    public ResetCommand(DeathTpPlusRenewed instance) {
        this.plugin = instance;
        log = DefaultLogger.getLogger();
        log.informational("dtpreset command registered");
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label,
                             String[] args) {
        log.debug("dtpreset command executing");
        if (!plugin.hasPerm(sender, "tombstone.reset", false)) {
            plugin.sendMessage(sender, "Permission Denied");
            return true;
        }
        Player p = (Player) sender;
        p.setCompassTarget(p.getWorld().getSpawnLocation());
        plugin.sendMessage(sender, "Your compass has been reset to the spawn location!");
        return true;
    }

}
