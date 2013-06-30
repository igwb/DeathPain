package me.igwb.DeathPain;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;

import org.bukkit.Location;
import java.io.File;



public class FacilityManager {

    private Plugin parent;
    private ArrayList<Facility> facilityList = new ArrayList<Facility>();
    private final String FACILITY_FOLDER;
  
    public FacilityManager(Plugin parent) {
        
        this.parent = parent;
        FACILITY_FOLDER = parent.getDataFolder() + "/Facilities";
      
        if(parent.getDebug()) {
            parent.logMessage(FACILITY_FOLDER);
        }
        
        if(!new File(FACILITY_FOLDER).exists()) {
            new File(FACILITY_FOLDER).mkdir();
            parent.logMessage("Creating facilities storrage directory.");
        }
        
        loadFacilities();
    }
    
    public void loadFacilities() {
        File folder = new File(FACILITY_FOLDER);
        FileInputStream reader = null;
        Properties props;
        File[] files;
        Location start, end;
        
        try {
            //Clear the old list first
            facilityList.clear();
            
            files = folder.listFiles();

            if(files != null) {
                for (File f : files) {
                    reader = new FileInputStream(f);
                    props = new Properties();
                    props.load(reader);

                    start = new Location(parent.getServer().getWorld(props.getProperty("world")), Double.parseDouble(props.getProperty("startX")), Double.parseDouble(props.getProperty("startY")), Double.parseDouble(props.getProperty("startZ")), Float.parseFloat(props.getProperty("sYaw")), Float.parseFloat(props.getProperty("sPitch")));
                    end = new Location(parent.getServer().getWorld(props.getProperty("world")), Double.parseDouble(props.getProperty("endX")), Double.parseDouble(props.getProperty("endY")), Double.parseDouble(props.getProperty("endZ")));
                    
                    //TODO: Add check if facility is still valid (end sign exists?)
                    facilityList.add(new Facility(props.getProperty("Name"), Integer.parseInt(props.getProperty("TimeLimit")), Integer.parseInt(props.getProperty("minSeverity")), Integer.parseInt(props.getProperty("maxSeverity")), start, end));
                }
            } else {
                parent.logSevere("Loading facilities failed! \"" + FACILITY_FOLDER + "\" No facilities exist or folder access denied.");
                parent.logSevere(this.getClass().getName());
            }
            
            if(facilityList.size() == 0) {
                parent.getLogger().warning("No facilities loaded.");
            }

        } catch (FileNotFoundException e) {
            parent.logSevere("Unexpected error while loading facilities!");
            e.printStackTrace();
        } catch (IOException e) {
            parent.logSevere("Unexpected error while loading facilities!");
            e.printStackTrace();
        } finally {
            if(reader != null) {
                try { 
                    reader.close(); 
                } catch (Exception e){

                }
            }
        }
    }
    
    public void saveFacility(Facility saveFacility) {
        File folder = new File(FACILITY_FOLDER), inFileProps;
        FileOutputStream writer = null;
        Properties props = new Properties();
        Integer sX, sY, sZ, eX, eY, eZ;

        try {
            props.setProperty("Name", saveFacility.getName());
            props.setProperty("TimeLimit", saveFacility.getTimeLimit().toString());
            props.setProperty("minSeverity", saveFacility.getMinSeverity().toString());
            props.setProperty("maxSeverity", saveFacility.getMaxSeverity().toString());
            props.setProperty("world", saveFacility.getStartPoint().getWorld().getName());

            sX = saveFacility.getStartPoint().getBlockX();
            sY = saveFacility.getStartPoint().getBlockY();
            sZ = saveFacility.getStartPoint().getBlockZ();
            props.setProperty("startX", sX.toString());
            props.setProperty("startY", sY.toString());
            props.setProperty("startZ", sZ.toString());
            props.setProperty("sYaw", Float.toString(saveFacility.getStartPoint().getYaw()));
            props.setProperty("sPitch", Float.toString(saveFacility.getStartPoint().getPitch()));
            
            eX = saveFacility.getEndSignLocation().getBlockX();
            eY = saveFacility.getEndSignLocation().getBlockY();
            eZ = saveFacility.getEndSignLocation().getBlockZ();
            props.setProperty("endX", eX.toString());
            props.setProperty("endY", eY.toString());
            props.setProperty("endZ", eZ.toString());

            inFileProps = new File(FACILITY_FOLDER + "/" + saveFacility.getName() + ".properties");
           //Create file if not exists
            if(!inFileProps.exists()) {
                inFileProps.createNewFile();
            }
            
            //Write the data
            writer = new FileOutputStream(inFileProps);
            props.store(writer, "Facility");

        } catch (FileNotFoundException e) {
            parent.logSevere("Unexpected error while saving facilities!");
            e.printStackTrace();
        } catch (IOException e) {
            parent.logSevere("Unexpected error while saving facilities!");
            e.printStackTrace();
        } finally {
            if(writer != null) {
                try { 
                    writer.close(); 
                } catch (Exception e){

                }
            }
        }    
    }
    
    public void addFacility(Facility theFacility) {
        
        facilityList.add(theFacility);
    }
    
    public ArrayList<Facility> getAllFacilities() {
        
        return facilityList;
    }
    
    public Facility findAppropriateFacility(Integer severity) {
        
        ArrayList<Facility> facilitiesPossible = new ArrayList<Facility>();
        
        for (Facility possibleFacility : facilityList) {
            if(possibleFacility.getMaxSeverity() >= severity && possibleFacility.getMinSeverity() <= severity) {
                facilitiesPossible.add(possibleFacility);
            }
        }
        
        //Check how many facilities are possible.
        if(facilitiesPossible.size() == 1) {
           
            return facilitiesPossible.get(0);
        } else if(facilitiesPossible.size() > 1) {
           //Choose a random facility if more than one is possible.
            
            Random r = new Random();
            
            return facilitiesPossible.get(r.nextInt(facilitiesPossible.size()));
        }
        
        return null;
    }

    public boolean deleteFacility(String name) {

        File facilityFile;
        boolean success = false;

        for (Facility fac : facilityList) {
            if(fac.getName().equalsIgnoreCase(name)) {
                facilityFile = new File(FACILITY_FOLDER + "/" + fac.getName() + ".properties");
                if(facilityFile.exists()) {
                    success = facilityFile.delete();
                }
            }
        }
        
        loadFacilities();
        return success;
    }
}
