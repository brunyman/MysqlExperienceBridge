package net.craftersland.bridge.exp;

import java.util.HashMap;
import java.util.logging.Logger;

import net.craftersland.bridge.exp.database.ExpMysqlInterface;
import net.craftersland.bridge.exp.database.MysqlSetup;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Exp extends JavaPlugin {
	
	public static Logger log;
	public static String pluginName = "MysqlExperienceBridge";
	public HashMap<String, Boolean> playersSync = new HashMap<String, Boolean>();
	
	private static ConfigHandler configHandler;
	private static MysqlSetup databaseManager;
	private static ExpMysqlInterface expMysqlInterface;
	private static BackgroundTask bt;
	
	@Override
    public void onEnable() {
		log = getLogger();
    	//Load Configuration
    	configHandler = new ConfigHandler(this);
    	databaseManager = new MysqlSetup(this);
    	expMysqlInterface = new ExpMysqlInterface(this);
    	bt = new BackgroundTask(this);
    	//Register Listeners
    	PluginManager pm = getServer().getPluginManager();
    	pm.registerEvents(new PlayerHandler(this), this);
    	log.info(pluginName + " loaded successfully!");
	}
	
	@Override
    public void onDisable() {
		Bukkit.getScheduler().cancelTasks(this);
		HandlerList.unregisterAll(this);
		if (databaseManager.getConnection() != null) {
			bt.onShutDownDataSave();
			databaseManager.closeConnection();
		}
		log.info(pluginName + " is disabled!");
	}
	
	public ConfigHandler getConfigHandler() {
		return configHandler;
	}
	public MysqlSetup getDatabaseManager() {
		return databaseManager;
	}
	public ExpMysqlInterface getExpMysqlInterface() {
		return expMysqlInterface;
	}
	public BackgroundTask getBackgroundTask() {
		return bt;
	}

}
