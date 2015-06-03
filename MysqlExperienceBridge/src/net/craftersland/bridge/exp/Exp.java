package net.craftersland.bridge.exp;

import java.io.File;
import java.util.logging.Logger;

import net.craftersland.bridge.exp.database.DatabaseManagerMysql;
import net.craftersland.bridge.exp.database.ExpMysqlInterface;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Exp extends JavaPlugin {
	
	public static Logger log;
	
	private ConfigHandler configHandler;
	private DatabaseManagerMysql databaseManager;
	private ExpMysqlInterface expMysqlInterface;
	
	@Override
    public void onEnable() {
		log = getLogger();
		log.info("Loading MysqlExperienceBridge v"+getDescription().getVersion()+"... ");
		
		//Create MysqlExperienceBridge folder
    	(new File("plugins"+System.getProperty("file.separator")+"MysqlExperienceBridge")).mkdir();
    	
    	//Load Configuration
    	configHandler = new ConfigHandler(this);
    	
    	//Setup Database
    	log.info("Using MySQL as Datasource...");
    	databaseManager = new DatabaseManagerMysql(this);
    	expMysqlInterface = new ExpMysqlInterface(this);
    	
    	if (databaseManager.getConnection() == null)
    	{
    		getServer().getPluginManager().disablePlugin(this);
            return;
    	}
    	
    	//Register Listeners
    	PluginManager pm = getServer().getPluginManager();
    	pm.registerEvents(new PlayerHandler(this), this);
    	
    	log.info("MysqlExperienceBridge has been successfully loaded!");
	}
	
	@Override
    public void onDisable() {
		if (this.isEnabled()) {
			//Closing database connection
			if (databaseManager.getConnection() != null) {
				log.info("Closing MySQL connection...");
				databaseManager.closeDatabase();
			}
		}
		log.info("MysqlExperienceBridge has been disabled");
	}
	
	public ConfigHandler getConfigHandler() {
		return configHandler;
	}
	
	public DatabaseManagerMysql getDatabaseManager() {
		return databaseManager;
	}
	
	public ExpMysqlInterface getExpMysqlInterface() {
		return expMysqlInterface;
	}

}
