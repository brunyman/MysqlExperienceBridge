package net.craftersland.bridge.exp;

import java.io.File;

public class ConfigHandler {
	
	private Exp exp;
	
	public ConfigHandler(Exp exp) {
		this.exp = exp;
		loadConfig();
	}
	
	public void loadConfig() {
		File pluginFolder = new File("plugins" + System.getProperty("file.separator") + Exp.pluginName);
		if (pluginFolder.exists() == false) {
    		pluginFolder.mkdir();
    	}
		File configFile = new File("plugins" + System.getProperty("file.separator") + Exp.pluginName + System.getProperty("file.separator") + "config.yml");
		if (configFile.exists() == false) {
			Exp.log.info("No config file found! Creating new one...");
			exp.saveDefaultConfig();
		}
    	try {
    		Exp.log.info("Loading the config file...");
    		exp.getConfig().load(configFile);
    	} catch (Exception e) {
    		Exp.log.severe("Could not load the config file! You need to regenerate the config! Error: " + e.getMessage());
			e.printStackTrace();
    	}
	}
	
	public String getString(String key) {
		if (!exp.getConfig().contains(key)) {
			exp.getLogger().severe("Could not locate " + key + " in the config.yml inside of the " + Exp.pluginName + " folder! (Try generating a new one by deleting the current)");
			return "errorCouldNotLocateInConfigYml:" + key;
		} else {
			return exp.getConfig().getString(key);
		}
	}
	
	public Integer getInteger(String key) {
		if (!exp.getConfig().contains(key)) {
			exp.getLogger().severe("Could not locate " + key + " in the config.yml inside of the " + Exp.pluginName + " folder! (Try generating a new one by deleting the current)");
			return null;
		} else {
			return exp.getConfig().getInt(key);
		}
	}
	
	public Boolean getBoolean(String key) {
		if (!exp.getConfig().contains(key)) {
			exp.getLogger().severe("Could not locate " + key + " in the config.yml inside of the " + Exp.pluginName + " folder! (Try generating a new one by deleting the current)");
			return null;
		} else {
			return exp.getConfig().getBoolean(key);
		}
	}

}
