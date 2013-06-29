package me.igwb.DeathPain;

public class SeverityManager {

    Plugin parent;
    
    private final int MAX_SEVERITY = 20, MIN_SEVERITY = 0;
    
    private int perDeath, perInterval, perKill, perRevengeKill;
    private int interval;
    private boolean intervalWhileOffline;
    
    public SeverityManager(Plugin parent, int perDeath, int perInterval, int perKill, int perRevengeKill, boolean intervalWhileOffline, int interval) {
        this.parent = parent;
        
        this.perDeath = perDeath;
        this.perInterval = perInterval;
        this.perKill = perKill;
        this.perRevengeKill = perRevengeKill;
        this.intervalWhileOffline = intervalWhileOffline;
        this.interval = interval;
        
        if(parent.getDebug()) {
            parent.logMessage("=== Severity ===");
            parent.logMessage("perDeath " + perDeath);
            parent.logMessage("perInterval " + perInterval);
            parent.logMessage("perKill " + perKill);
            parent.logMessage("perRevengeKill " + perRevengeKill);
            parent.logMessage("intervalWhileOffline " + intervalWhileOffline);
            parent.logMessage("interval " + interval);
        }
    }
    
    public void playerDied(String player) {
        int oldSeverity , newSeverity;
        oldSeverity = parent.getSQL().getSeverity(player);
        
        newSeverity = normalizeSeverity(oldSeverity + perDeath);
        
        parent.getSQL().setSeverity(player, newSeverity);
    }
    
    public void playerKilled(String killer, String victim) {
        
        int oldSeverityKiller, oldSeverityVictim, newSeverityKiller, newSeverityVictim;
        
        oldSeverityKiller = parent.getSQL().getSeverity(killer);
        oldSeverityVictim = parent.getSQL().getSeverity(victim);
        
        newSeverityKiller = normalizeSeverity(oldSeverityKiller + perKill);
        newSeverityVictim = normalizeSeverity(oldSeverityVictim + perDeath);
        
        parent.getSQL().setSeverity(killer, newSeverityKiller);
        parent.getSQL().setSeverity(victim, newSeverityVictim);
    }
    
    private void intervalDiscount() {
        
    }
    
    private int normalizeSeverity(int severity) {
        
        return Math.min(MAX_SEVERITY, Math.max(MIN_SEVERITY, severity));
    }
    
}
