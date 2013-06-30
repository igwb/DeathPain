package me.igwb.DeathPain;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;


public class Plugin extends JavaPlugin{

    private static final int CONFIG_VERSION = 0;

    private EventListener el;
    private MyCommandExecutor cx;
    private MySQLConnector sqlConnector;
    private SeverityManager sm;
    private FacilityManager fm;
    private FacilityCreator fc;

    private boolean debug;
    private boolean deathMessagesOn, modifyDeathMessages, handleRespawns;
    private List<String> worlds;
    Location respawnPoint;
    
    @Override
    public void onEnable()  {

        loadConfig();
        initializeMySQL();

        el = new EventListener(this);
        cx = new MyCommandExecutor(this);
        fm = new FacilityManager(this);
        fc = new FacilityCreator(this);
        
        registerEvents();
        registerCommands();
    }

    private void loadConfig() {

        FileConfiguration config = this.getConfig();
        config.options().copyDefaults();
        this.saveDefaultConfig();

        //Check if configuration file versions match
        if(config.getInt("Version") != CONFIG_VERSION) {
            getLogger().warning("Your config is not up to date and needs to be regenerated!");
        }

        debug = config.getBoolean("Debug");
        worlds = config.getStringList("Worlds");

        deathMessagesOn = config.getBoolean("DeathMessages.enabled");
        modifyDeathMessages = config.getBoolean("DeathMessages.includeDeathCount");

        //Re-spawning
        List<String> respawnInfos;
        handleRespawns = config.getBoolean("Respawning.handleRespawns");
        respawnInfos = config.getStringList("Respawning.respawnPoint");
        respawnPoint = new Location(this.getServer().getWorld(respawnInfos.get(0)), Integer.parseInt(respawnInfos.get(1)), Integer.parseInt(respawnInfos.get(2)), Integer.parseInt(respawnInfos.get(3)));
        
        //Severity settings:
        sm = new SeverityManager(this, Integer.parseInt(config.getString("Punishments.death")), Integer.parseInt(config.getString("Punishments.interval")), Integer.parseInt(config.getString("Punishments.normalKill")), Integer.parseInt(config.getString("Punishments.revengeKill")), config.getBoolean("Punishments.intervalWhileOffline"), config.getInt("Punishments.interval"));
        
        //Print out the configuration if(debug)
        if(debug) {
            logMessage("Debug mode: " + debug);
            logMessage("Enabled in world(s): " + worlds);
            logMessage("Respawning at: " + respawnPoint.toString());
            logMessage("Config version: " + config.getInt("Version"));
        }

    }

    private void initializeMySQL() {
        FileConfiguration config = this.getConfig();
        config.options().copyDefaults();
        this.saveDefaultConfig();

        sqlConnector = new MySQLConnector(this, config.getString("MySQL.host"), config.getInt("MySQL.port"), config.getString("MySQL.user"), config.getString("MySQL.password"), config.getString("MySQL.database"));

    }

    private void registerEvents() {

        getServer().getPluginManager().registerEvents(el,this);

    }

    private void registerCommands() {

        getCommand("death").setExecutor(cx);
    }

    public boolean getDebug() {
        return debug;
    }

    public boolean getDeathMessagesOn() {

        return deathMessagesOn;
    }

    public boolean getModifyDeathMessages() {

        return modifyDeathMessages;
    }

    public void logMessage(String message){
        getLogger().info(message);
    }

    public void logSevere(String message){
        getLogger().severe(message);
    }

    public MySQLConnector getSQL() {
        return sqlConnector;
    }
    
    public List<String> getActiveWorlds() {

        return worlds;
    }

    public Location getRespawnPoint() {
   
        return respawnPoint;
    }
    

    public boolean isHandelingRespawns() {
        
        return handleRespawns;
    }

    public SeverityManager getSeverityManager() {
       
        return sm;
    }

    public FacilityManager getFacilityManager() {
        
        return fm;
    }
    
    public FacilityCreator getFacilityCreator() {
        
        return fc;
    }
    
}
