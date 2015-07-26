package net.craftersland.bridge.exp;

import java.io.File;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.logging.Logger;

import net.craftersland.bridge.exp.database.DatabaseManagerMysql;
import net.craftersland.bridge.exp.database.ExpMysqlInterface;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Exp extends JavaPlugin {
	
	public static Logger log;
	public HashMap<String, Boolean> playersSync = new HashMap<String, Boolean>();
	
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
    	
    	if (getConfigHandler().getString("database.maintenance.enabled").matches("true")) {
    		runMaintenance();
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
				savePlayerData();
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
	
    public void runMaintenance() {
		
		Bukkit.getScheduler().runTaskLaterAsynchronously(this, new Runnable() {
			@Override
			public void run() {
				if (databaseManager.getConnection() == null) return;
				log.info("Database maintenance task started...");
				
				long inactivityDays = Long.parseLong(getConfigHandler().getString("database.maintenance.inactivity"));
				long inactivityMils = inactivityDays * 24 * 60 * 60 * 1000;
				long curentTime = System.currentTimeMillis();
				long inactiveTime = curentTime - inactivityMils;
				String tableName = getConfigHandler().getString("database.mysql.tableName");
				
				try {
					String sql = "DELETE FROM `" + tableName + "` WHERE `last_seen` <?";
					PreparedStatement preparedStatement = databaseManager.getConnection().prepareStatement(sql);
					preparedStatement.setString(1, String.valueOf(inactiveTime));
					
					preparedStatement.executeUpdate();
				} catch (Exception e) {
					log.severe("Error: " + e.getMessage());
				}
				
				log.info("Database maintenance task ended.");
			}
		}, 400L);	
	}
    
    private void savePlayerData() {
    	if (Bukkit.getOnlinePlayers().isEmpty() == true) return;
		log.info("Saving players data...");
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (playersSync.containsKey(p.getName()) == false) return;
			getExpMysqlInterface().setExperience(p.getUniqueId(), p, p.getExp(), p.getExpToLevel(), p.getTotalExperience(), p.getLevel());
		}
    }

}
