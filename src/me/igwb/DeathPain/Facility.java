package me.igwb.DeathPain;

import org.bukkit.Location;

public class Facility {

    private Integer minSeverity, maxSeverity, timeLimit;
    private String name, wgRegion;
    private Location startPoint, endSignLocation;
    
    public Facility(String name, Integer timeLimit, Integer minSeverity, Integer maxSeverity, Location startPoint, Location endSignLocation) {
        
        this.name = name;
        this.timeLimit = timeLimit;
        this.minSeverity = minSeverity;
        this.maxSeverity = maxSeverity;
        this.startPoint = startPoint;
        this.endSignLocation = endSignLocation;
        
    }
    
    public Integer getMinSeverity() {
       
        return minSeverity;
    }
    
    public Integer getMaxSeverity() {
        
        return maxSeverity;
    }
    
    public Location getStartPoint()  {
        
        return startPoint;
    }
    
    public Location getEndSignLocation() {
        
        return endSignLocation;
    }
    
    public String getName() {
        
        return name;
    }
    
    public Integer getTimeLimit() {
        
        return timeLimit;
    }
}
