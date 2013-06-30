package me.igwb.DeathPain;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.block.Sign;

public class EventListener implements Listener{

    Plugin parent;

    EventListener(Plugin parent) {
        this.parent = parent;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent death) {
        try {
           
            //Check if the dead entity was a player.
            if(!(death.getEntity() instanceof Player)) {
                return;
            }

            //Check if the plugin is enabled in the world the death occured
            if(!parent.getActiveWorlds().contains(death.getEntity().getLocation().getWorld().getName())) {
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

            parent.getSQL().logDeath(theDeadOne.getName(), cause, killer, System.currentTimeMillis(), x, y, z);

            
            //Change the serverity level of the player
            if(death.getEntity().getKiller() == null) {
                parent.getSeverityManager().playerDied(theDeadOne.getName());
            } else {
                parent.getSeverityManager().playerKilled(death.getEntity().getKiller().getName(), theDeadOne.getName());
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
                death.setDeathMessage("[" + (parent.getSQL().getDeathCount(death.getEntity().getName()) + 1) + "] " + death.getDeathMessage());
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

        //Check if the plugin is active in the world the players re-spawns
        //TODO: This may lead to unwanted behavior when the player dies in a disabled and re-spawns in an active world.
        if(!parent.getActiveWorlds().contains(respawn.getPlayer().getLocation().getWorld().getName())) {
            return;
        }
        
        //Find a location for the user to re-spawn
        Integer severity;
        Facility respawnFacility;
        severity = parent.getSQL().getSeverity(respawn.getPlayer().getName());
        respawnFacility = parent.getFacilityManager().findAppropriateFacility(severity);
        
        //Check if facility was found and if player has ignore permission
        if(respawnFacility != null) {
            
            if(parent.getDebug()) {
                parent.logMessage("Respawning player at facility");
            }
            respawn.setRespawnLocation(respawnFacility.getStartPoint());
        } else {
            
            //Respawn player at default spawn if no facility could be found for him.
            if(parent.getDebug()) {
                parent.logMessage("Respawning player at spawn");
            }
            respawn.setRespawnLocation(parent.getRespawnPoint());
        }
    }

    @EventHandler
    void onPlayerInteractEvent(PlayerInteractEvent interact) {
        
        //Check if block is present
        if(interact.getClickedBlock() == null) {
            return;
        }
        
        //Check if an end sign was clicked
        if(interact.getClickedBlock().getState() instanceof Sign) {
            ArrayList<Facility> facilitys;
            
            facilitys = parent.getFacilityManager().getAllFacilities();
            
            for (Facility fac : facilitys) {
                if(interact.getClickedBlock().getLocation().equals(fac.getEndSignLocation())) {
                    interact.getPlayer().teleport(parent.getRespawnPoint());
                }
            }
        }
        
        //Check if a facility is being created and if the player is the one creating - otherwise return
        if(!parent.getFacilityCreator().getIsActive() || parent.getFacilityCreator().getPlayerActive() != interact.getPlayer().getName()) {
            return;
        }


        if(interact.getClickedBlock().getState() instanceof Sign) {
            Sign theSign = (Sign)(interact.getClickedBlock().getState());

            if(!parent.getFacilityCreator().isWaitingForExitSign()) {
                parent.getFacilityCreator().setFacilityName(theSign.getLine(0));
                parent.getFacilityCreator().setFacilityMinSeverity(theSign.getLine(1));
                parent.getFacilityCreator().setFacilityMaxSeverity(theSign.getLine(2));
                parent.getFacilityCreator().setFacilityTimeLimit(theSign.getLine(3));

                parent.getFacilityCreator().setFacilityStart(new Location(theSign.getLocation().getWorld(), theSign.getLocation().getX(), theSign.getLocation().getY(), theSign.getLocation().getZ(), interact.getPlayer().getLocation().getYaw(), interact.getPlayer().getLocation().getPitch()));

                interact.getPlayer().sendMessage("Start point registered successfully.");
                interact.getPlayer().sendMessage("Place a sign at the exit and hit it to complete. - Hit any other block to cancel.");


                parent.getFacilityCreator().setWaitingForExitSign(true);
            } else {

                parent.getFacilityCreator().setFacilityEnd(theSign.getLocation());
                if(parent.getFacilityCreator().completeCreation()) {
                    interact.getPlayer().sendMessage("Creation was successful!");
                    theSign.setLine(0, "=======");
                    theSign.setLine(1, "Click to");
                    theSign.setLine(2, "exit");
                    theSign.setLine(3, "=====");
                } else {
                    interact.getPlayer().sendMessage("Sorry, creation failed!");
                }
            }
        } else {

            parent.getFacilityCreator().setActive(false);
            interact.getPlayer().sendMessage("Facility creation aborted!");
        }
    }
}
