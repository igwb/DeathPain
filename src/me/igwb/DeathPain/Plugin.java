package me.igwb.DeathPain;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;


public class Plugin extends JavaPlugin{

    private static final int CONFIG_VERSION = 0;

    private EventListener el;
    private MyCommandExecutor cx;
    private MySQLConnector sqlConnector;


    private boolean debug;
    private boolean statistics, deathMessagesOn, modifyDeathMessages;
    private List<String> worlds;

    @Override
    public void onEnable()  {

        loadConfig();
        initializeMySQL();

        el = new EventListener(this);
        cx = new MyCommandExecutor(this);

        registerEvents();
        registerCommands();
    }

    private void loadConfig() {

        FileConfiguration config = this.getConfig();
        config.options().copyDefaults();
        this.saveDefaultConfig();

        //Check if configfile versions match
        if(config.getInt("Version") != CONFIG_VERSION) {
            getLogger().warning("Your config is not up to date and needs to be regenerated!");
        }

        debug = config.getBoolean("Debug");
        statistics = config.getBoolean("Statistics");
        worlds = config.getStringList("Worlds");

        deathMessagesOn = config.getBoolean("DeathMessages.enabled");
        modifyDeathMessages = config.getBoolean("DeathMessages.includeDeathCount");


        //Print out the configuration if(debug)
        if(debug) {
            logMessage("Debug mode: " + debug);
            logMessage("Statistics: " + statistics);
            logMessage("Enabled in world(s): " + worlds);
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

    public boolean getStatistics() {
        return statistics;
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

}
