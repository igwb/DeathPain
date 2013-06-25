package me.igwb.DeathPain;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;


public class Plugin extends JavaPlugin{

	private static final int CONFIG_VERSION = 0;
	
	private EventListener EL;
	private MySQLConnector SQLConnector;
	
	private boolean debug;
	private boolean statistics;
	private List<String> worlds;
	
	@Override
	public void onEnable()  {
	
		loadConfig();
		initializeMySQL();
		
		EL = new EventListener(this);
		
		RegisterEvents();
	}
	
	private void loadConfig() {

		FileConfiguration config = this.getConfig();
		config.options().copyDefaults();
		this.saveDefaultConfig();
		
		//Check if configfile versions match
		if(config.getInt("Version") != CONFIG_VERSION)
			getLogger().warning("Your config is not up to date and needs to be regenerated!");
		
		
		debug = config.getBoolean("Debug");
		statistics = config.getBoolean("Statistics");
		worlds = config.getStringList("Worlds");
		
		//Print out the configuration if(debug)
		if(debug) {
			   LogMessage("Debug mode: " + debug);
			   LogMessage("Statistics: " + statistics);
			   LogMessage("Enabled in world(s): " + worlds);
			   LogMessage("Config version: " + config.getInt("Version"));
		}
		
	}
	
	private void initializeMySQL() {
		FileConfiguration config = this.getConfig();
		config.options().copyDefaults();
		this.saveDefaultConfig();
		
		SQLConnector = new MySQLConnector(this, config.getString("MySQL.host"), config.getInt("MySQL.port"), config.getString("MySQL.user"), config.getString("MySQL.password"), config.getString("MySQL.database"));
		
	}
	
	private void RegisterEvents() {
		
		getServer().getPluginManager().registerEvents(EL,this);
		
	}
	
	public boolean getDebug() {
		return debug;
	}
	
	public boolean getStatistics() {
		return statistics;
	}
	
	public void LogMessage(String message){
		getLogger().info(message);
	}

	public void LogSevere(String message){
		getLogger().severe(message);
	}

	public MySQLConnector getSQL() {
		return SQLConnector;
	}
	
}
