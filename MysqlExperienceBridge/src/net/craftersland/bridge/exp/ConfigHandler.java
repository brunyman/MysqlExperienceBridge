package net.craftersland.bridge.exp;

import java.io.File;

public class ConfigHandler {
	
	private Exp exp;
	
	public ConfigHandler(Exp exp) {
		this.exp = exp;
		
		if (!(new File("plugins"+System.getProperty("file.separator")+"MysqlExperienceBridge"+System.getProperty("file.separator")+"config.yml").exists())) {
			Exp.log.info("No config file found! Creating new one...");
			exp.saveDefaultConfig();
		}
		try {
			exp.getConfig().load(new File("plugins"+System.getProperty("file.separator")+"MysqlExperienceBridge"+System.getProperty("file.separator")+"config.yml"));
		} catch (Exception e) {
			Exp.log.info("Could not load config file!");
			e.printStackTrace();
		}
	}
	
	public String getString(String key) {
		if (!exp.getConfig().contains(key)) {
			exp.getLogger().severe("Could not locate '"+key+"' in the config.yml inside of the MysqlEconomyBridge folder! (Try generating a new one by deleting the current)");
			return "errorCouldNotLocateInConfigYml:"+key;
		} else {
			return exp.getConfig().getString(key);
		}
	}

}
