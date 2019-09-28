package org.spigotmc.DeathTpPlusRenewed.events;

import org.bukkit.entity.Player;

/**
 * This class is for backward compatibility
 * @author mung3r
 *
 */
public class DeathStreakEvent extends org.spigotmc.DeathTpPlusRenewed.death.events.DeathStreakEvent {

    public DeathStreakEvent(Player player, Player killer, String message, Integer deaths) {
        super(player, killer, message, deaths);
    }

}
