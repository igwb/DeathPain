package me.igwb.DeathPain;

import org.bukkit.Location;

public class FacilityCreator {

    private String playerActive, facilityName, facilityMaxSeverity, facilityMinSeverity, facilityTimeLimit;
    private Location facilityStart, facilityEnd;
    private boolean active = false, waitingForExitSign;
    
    Plugin parent;
    
    public FacilityCreator(Plugin parent) {

        this.parent = parent;
    }

    public boolean getIsActive() {
        return active;
    }

    public boolean completeCreation() {
        try {
            parent.getFacilityManager().saveFacility(new Facility(facilityName, Integer.parseInt(facilityTimeLimit), Integer.parseInt(facilityMinSeverity), Integer.parseInt(facilityMaxSeverity), facilityStart, facilityEnd));    
            parent.getFacilityManager().loadFacilities(); 
            return true;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            setActive(false);
        }
    }
    
    public void setActive(boolean active) {

        this.active = active;

        if(!active) {
            playerActive = null;
            facilityName = null;
            facilityMaxSeverity = null;
            facilityMinSeverity = null;
            facilityTimeLimit = null;
            facilityStart = null;
            facilityEnd = null;
            waitingForExitSign = false;
        }
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public void setFacilityMaxSeverity(String facilityMaxSeverity) {
        this.facilityMaxSeverity = facilityMaxSeverity;
    }

    public void setFacilityMinSeverity(String facilityMinSeverity) {
        this.facilityMinSeverity = facilityMinSeverity;
    }

    public void setFacilityTimeLimit(String facilityTimeLimit) {
        this.facilityTimeLimit = facilityTimeLimit;
    }

    public void setFacilityStart(Location facilityStart) {
        this.facilityStart = facilityStart;
    }

    public void setFacilityEnd(Location facilityEnd) {
        this.facilityEnd = facilityEnd;
    }


    public String getPlayerActive() {
        return playerActive;
    }


    public void setPlayerActive(String playerActive) {
        this.playerActive = playerActive;
    }

    public boolean isWaitingForExitSign() {
        return waitingForExitSign;
    }

    public void setWaitingForExitSign(boolean waitingForExitSign) {
        this.waitingForExitSign = waitingForExitSign;
    }

}
