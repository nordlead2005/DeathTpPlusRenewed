package org.spigotmc.DeathTpPlusRenewed.death.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.spigotmc.DeathTpPlusRenewed.DeathTpPlusRenewed;
import org.spigotmc.DeathTpPlusRenewed.commons.DefaultLogger;
import org.spigotmc.DeathTpPlusRenewed.death.persistence.DeathRecordDao;
import org.spigotmc.DeathTpPlusRenewed.death.persistence.DeathRecord;
import org.spigotmc.DeathTpPlusRenewed.death.persistence.DeathRecord.DeathRecordType;

/**
 * PluginName: DeathTpPlusRenewed
 * Class: KillsCommand
 * User: DonRedhorse
 * Date: 19.10.11
 * Time: 22:09
 */

public class KillsCommand implements CommandExecutor {

    private DefaultLogger log;
    private DeathRecordDao deathLog;

    public KillsCommand(DeathTpPlusRenewed instance) {
        log = DefaultLogger.getLogger();
        log.debug("kills command registered");

    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        log.debug("kills command executing");
        boolean canUseCommand = false;
        if (sender instanceof Player) {
            Player player = (Player) sender;

            canUseCommand = player.hasPermission("deathtpplusrenewed.deathtp.kills");

        }

        if (canUseCommand) {

            int total;

            if (args.length > 2) {
                return false;
            }

            switch (args.length) {
                case 0:
                    Player player = (Player) sender;
                    total = deathLog.getTotalByType(player.getName(), DeathRecordType.kill);
                    if (total > -1) {
                        sender.sendMessage(String.format("You have %d kill(s)", total));
                    } else {
                        sender.sendMessage("No record found.");
                    }
                    break;
                case 1:
                    total = deathLog.getTotalByType(args[0], DeathRecordType.kill);
                    if (total > -1) {
                        sender.sendMessage(String.format("%s has %d kill(s)", args[0], total));
                    } else {
                        sender.sendMessage("No record found.");
                    }
                    break;
                case 2:
                    DeathRecord record = deathLog.getRecordByType(args[0], args[1], DeathRecordType.kill);
                    if (record != null) {
                        sender.sendMessage(String.format("%s killed %s %d time(s)", args[0], args[1], record.getCount()));
                    }
                    break;
            }

        }

        return true;

    }
}
