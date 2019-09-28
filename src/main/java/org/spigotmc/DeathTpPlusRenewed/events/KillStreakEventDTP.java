package org.spigotmc.DeathTpPlusRenewed.events;

import org.bukkit.entity.Player;

/**
 * This class is for backward compatibility
 * @author rramos
 *
 */
public class KillStreakEventDTP extends org.spigotmc.DeathTpPlusRenewed.death.events.KillStreakEvent {

    public KillStreakEventDTP(Player player, Player victim, String message, Integer kills, Boolean isMultiKill) {
        super(player, victim, message, kills, isMultiKill);
    }

}
