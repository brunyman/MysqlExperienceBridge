package net.craftersland.bridge.exp;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerHandler implements Listener {
	
	private Exp exp;

	public PlayerHandler(Exp exp) {
		this.exp = exp;
	}
	
	@EventHandler
	public void onLogin(final PlayerJoinEvent event) {
		//Check if player has a MySQL account first
		if (exp.getExpMysqlInterface().hasAccount(event.getPlayer().getUniqueId()) == false) {
			if (event.getPlayer() != null) {
				exp.playersSync.put(event.getPlayer().getName(), true);
			}
		} else {
			Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(exp, new Runnable() {
				
				@Override
				public void run() {
					final Player p = event.getPlayer();
					if (p == null) return;					
					
					final float mysqlExp = exp.getExpMysqlInterface().getExp(p.getUniqueId());
					final int mysqlTotalExp = exp.getExpMysqlInterface().getTotalExp(p.getUniqueId());
					final int mysqlLvl = exp.getExpMysqlInterface().getLvl(p.getUniqueId());
					
					if (mysqlExp == 0 && mysqlTotalExp == 0) return;
					
					Bukkit.getScheduler().runTask(exp, new Runnable() {
						@Override
						public void run() {
							p.setExp(mysqlExp);
							p.setTotalExperience(mysqlTotalExp);
							p.setLevel(mysqlLvl);
							
							if (p.getTotalExperience() != mysqlTotalExp || p.getTotalExperience() != mysqlTotalExp) {
								p.setExp(mysqlExp);
								p.setTotalExperience(mysqlTotalExp);
								p.setLevel(mysqlLvl);
							}
							exp.playersSync.put(p.getName(), true);
						}
					});
				}
				
			}, Integer.parseInt(exp.getConfigHandler().getString("General.loginSyncDelay")) / 1000 * 20L);
		}
	}
	
	@EventHandler
	public void onDisconnect(final PlayerQuitEvent event) {
		if (event.getPlayer() != null) {
			if (exp.playersSync.containsKey(event.getPlayer()) == true) {
				Bukkit.getScheduler().runTaskLaterAsynchronously(exp, new Runnable() {
					@Override
					public void run() {
						Player p = event.getPlayer();
						if (p != null) {
							float experience = p.getExp();
							int expToLevel = p.getExpToLevel();
							int totalExp = p.getTotalExperience();
							int lvl = p.getLevel();
							exp.getExpMysqlInterface().setExperience(p.getUniqueId(), p, experience, expToLevel, totalExp, lvl);
						}
					}
				}, 1L);
			}
		}
	}

}
