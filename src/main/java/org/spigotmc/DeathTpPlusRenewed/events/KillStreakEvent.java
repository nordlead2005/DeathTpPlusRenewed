package org.spigotmc.DeathTpPlusRenewed.events;

import org.bukkit.entity.Player;

/**
 * This class is for backward compatibility
 * @author rramos
 *
 */
public class KillStreakEvent extends org.spigotmc.DeathTpPlusRenewed.death.events.KillStreakEvent {

    public KillStreakEvent(Player player, Player victim, String message, Integer kills, Boolean isMultiKill) {
        super(player, victim, message, kills, isMultiKill);
    }

}
