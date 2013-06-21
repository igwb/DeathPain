package me.igwb.DeathPain;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class EventListener implements Listener{

	Plugin parent;
	
	EventListener(Plugin parent) {
		this.parent = parent;
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent death) { 
		if(parent.getDebug()) {
			parent.LogMessage("Player " + death.getEntity().getDisplayName() + " died");
		}
		
		
	}
	
	@EventHandler
	void onPlayerRespawn(PlayerRespawnEvent respawn) { 
		if(parent.getDebug()) {
			parent.LogMessage("Player " + respawn.getPlayer().getDisplayName() + " respawned");
		}
		
	}
}
