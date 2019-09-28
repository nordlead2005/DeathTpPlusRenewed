package org.spigotmc.DeathTpPlusRenewed.death.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.spigotmc.DeathTpPlusRenewed.DeathTpPlusRenewed;
import org.spigotmc.DeathTpPlusRenewed.commons.ConfigManager;
import org.spigotmc.DeathTpPlusRenewed.commons.DefaultLogger;
import org.spigotmc.DeathTpPlusRenewed.death.persistence.StreakRecord;

/**
 * PluginName: DeathTpPlusRenewed
 * Class: StreakCommand
 * User: DonRedhorse
 * Date: 19.10.11
 * Time: 22:10
 */

public class StreakCommand implements CommandExecutor {

    private DefaultLogger log;
    private ConfigManager config;

    public StreakCommand(DeathTpPlusRenewed instance) {
        log = DefaultLogger.getLogger();
        config = ConfigManager.getInstance();
        log.debug("streak command registered");
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        log.debug("streak command executing");
        boolean canUseCommand = false;
        if (sender instanceof Player) {
            Player player = (Player) sender;

            canUseCommand = player.hasPermission("deathtpplusrenewed.deathtp.streak");

        }

        if (canUseCommand) {
            if (config.isShowStreaks()) {

                StreakRecord streak = DeathTpPlusRenewed.getStreakLog().getRecord(args.length > 0 ? args[0] : ((Player) sender).getName());

                if (streak != null) {
                    if (streak.getCount() < 0) {
                        if (args.length > 0) {
                            sender.sendMessage(String.format("%s is on a %d death streak.", args[0], streak.getCount() * -1));
                        } else {
                            sender.sendMessage(String.format("You are on a %d death streak.", streak.getCount() * -1));
                        }
                    } else {
                        if (args.length > 0) {
                            sender.sendMessage(String.format("%s is on a %d kill streak.", args[0], streak.getCount()));
                        } else {
                            sender.sendMessage(String.format("You are on a %d kill streak.", streak.getCount()));
                        }
                    }
                } else {
                    sender.sendMessage("No record found.");
                }


            }
            return true;
        }


        return false;
    }
}
