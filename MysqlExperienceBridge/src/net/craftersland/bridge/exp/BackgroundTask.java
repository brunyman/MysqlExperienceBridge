package net.craftersland.bridge.exp;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class BackgroundTask {
	
	private Exp m;
	
	public BackgroundTask(Exp m) {
		this.m = m;
		runTask();
	}
	
	private void runTask() {
		if (m.getConfigHandler().getBoolean("General.saveDataTask.enabled") == true) {
			Exp.log.info("Data save task is enabled.");
		} else {
			Exp.log.info("Data save task is disabled.");
		}
		Bukkit.getScheduler().runTaskTimerAsynchronously(m, new Runnable() {

			@Override
			public void run() {
				runSaveData();
			}
			
		}, m.getConfigHandler().getInteger("General.saveDataTask.interval") * 60 * 20L, m.getConfigHandler().getInteger("General.saveDataTask.interval") * 60 * 20L);
	}
	
	private void runSaveData() {
		if (m.getConfigHandler().getBoolean("General.saveDataTask.enabled") == true) {
			if (Bukkit.getOnlinePlayers().isEmpty() == false) {
				List<Player> onlinePlayers = new ArrayList<Player>(Bukkit.getOnlinePlayers());
				if (m.getConfigHandler().getBoolean("General.saveDataTask.hideLogMessages") == false) {
					Exp.log.info("Saving online players data...");
				}
				for (Player p : onlinePlayers) {
					if (p.isOnline() == true) {
						float experience = p.getExp();
						int expToLevel = p.getExpToLevel();
						int totalExp = p.getTotalExperience();
						int lvl = p.getLevel();
						m.getExpMysqlInterface().setExperience(p.getUniqueId(), p, experience, expToLevel, totalExp, lvl);
					}
				}
				if (m.getConfigHandler().getBoolean("General.saveDataTask.hideLogMessages") == false) {
					Exp.log.info("Data save complete for " + onlinePlayers.size() + " players.");
				}
				onlinePlayers.clear();
			}
		}
	}
	
	public void onShutDownDataSave() {
		Exp.log.info("Saving online players data...");
		List<Player> onlinePlayers = new ArrayList<Player>(Bukkit.getOnlinePlayers());
		
		for (Player p : onlinePlayers) {
			if (p.isOnline() == true) {
				float experience = p.getExp();
				int expToLevel = p.getExpToLevel();
				int totalExp = p.getTotalExperience();
				int lvl = p.getLevel();
				m.getExpMysqlInterface().setExperience(p.getUniqueId(), p, experience, expToLevel, totalExp, lvl);
			}
		}
		Exp.log.info("Data save complete for " + onlinePlayers.size() + " players.");
	}

}
