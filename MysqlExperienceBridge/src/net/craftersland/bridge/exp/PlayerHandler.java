package net.craftersland.bridge.exp;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerHandler implements Listener {
	
	private Exp exp;
	private int delay = 1;

	public PlayerHandler(Exp exp) {
		this.exp = exp;
	}
	
	@EventHandler
	public void onLogin(final AsyncPlayerPreLoginEvent event) {
		//Check if player has a MySQL account first
		if (exp.getExpMysqlInterface().hasAccount(event.getUniqueId()) == false) return;
		
		delay = Integer.parseInt(exp.getConfigHandler().getString("General.loginSyncDelay")) / 1000;
				
		//Added a small delay to prevent the onDisconnect handler overlapping onLogin on a BungeeCord configuration when switching servers.
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(exp, new Runnable() {
					
					@Override
					public void run() {
						Player p = Bukkit.getPlayer(event.getUniqueId());
						UUID playerUUID = event.getUniqueId();
						
						try {
							if (p.isOnline() == false) return;
						} catch (Exception e) {
							return;
						}
						
						float mysqlExp = exp.getExpMysqlInterface().getExp(playerUUID);
						int mysqlTotalExp = exp.getExpMysqlInterface().getTotalExp(playerUUID);
						int mysqlLvl = exp.getExpMysqlInterface().getLvl(playerUUID);
						
						if (mysqlExp == 0 && mysqlTotalExp == 0) return;
						
						p.setExp(mysqlExp);
						p.setTotalExperience(mysqlTotalExp);
						p.setLevel(mysqlLvl);
						
						if (p.getTotalExperience() != mysqlTotalExp || p.getTotalExperience() != mysqlTotalExp) {
							p.setExp(mysqlExp);
							p.setTotalExperience(mysqlTotalExp);
							p.setLevel(mysqlLvl);
						}
						
						exp.getExpMysqlInterface().setExperience(playerUUID, p, 0.00f, 0, 0, 0);
					}
					
				}, delay * 20L);
	}
	
	@EventHandler
	public void onDisconnect(PlayerQuitEvent event) {
		Player p = event.getPlayer();
		float experience = p.getExp();
		int expToLevel = p.getExpToLevel();
		int totalExp = p.getTotalExperience();
		int lvl = p.getLevel();
		
		if (experience == 0 && totalExp == 0) return;
		
		exp.getExpMysqlInterface().setExperience(p.getUniqueId(), p, experience, expToLevel, totalExp, lvl);
		
		p.setExp(0);
		p.setTotalExperience(0);
		p.setLevel(0);
		
		if (p.getTotalExperience() != 0) {
			p.setExp(0);
			p.setTotalExperience(0);
			p.setLevel(0);
		}
	}

}
