package me.igwb.DeathPain;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class EventListener implements Listener{

    Plugin parent;

    EventListener(Plugin parent) {
        this.parent = parent;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent death) {
        try {
            if(!(death.getEntity() instanceof Player)) {
                return;
            }

            String cause = null, killer = null;
            int x, y, z;
            Player theDeadOne = (Player) death.getEntity();

            x = theDeadOne.getLocation().getBlockX();
            y = theDeadOne.getLocation().getBlockY();
            z = theDeadOne.getLocation().getBlockZ();

            if(death.getEntity().getKiller() == null) {
                cause = theDeadOne.getLastDamageCause().getCause().toString();
            } else {
                cause = theDeadOne.getLastDamageCause().getCause().toString();
                killer = death.getEntity().getKiller().getName();
            }

            if(parent.getDebug()) {
                parent.logMessage("Player " + theDeadOne.getName() + " died");
                parent.logMessage("Killed by: " + killer);
            }

            if(parent.getStatistics()) {
                parent.getSQL().logDeath(theDeadOne.getName(), cause, killer, System.currentTimeMillis(), x, y, z);
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent death) {

        if(parent.getDeathMessagesOn()) {
            if(parent.getModifyDeathMessages()) {
                death.setDeathMessage("[" + parent.getSQL().getDeathCount(death.getEntity().getName()) + "] " + death.getDeathMessage());
            }
        } else {
            death.setDeathMessage(null);
        }
    }

    @EventHandler
    void onPlayerRespawn(PlayerRespawnEvent respawn) { 
        if(parent.getDebug()) {
            parent.logMessage("Player " + respawn.getPlayer().getDisplayName() + " respawned");
        }

    }
}
