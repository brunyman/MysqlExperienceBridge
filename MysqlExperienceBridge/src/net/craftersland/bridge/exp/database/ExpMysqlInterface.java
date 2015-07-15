package net.craftersland.bridge.exp.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.entity.Player;

import net.craftersland.bridge.exp.Exp;

public class ExpMysqlInterface {
	
	private Exp exp;
	private Connection conn;
	private String tableName = "meb_experience";
	
	public ExpMysqlInterface(Exp exp) {
		this.exp = exp;
		this.conn = ((DatabaseManagerMysql)exp.getDatabaseManager()).getConnection();
	}
	
	public boolean hasAccount(UUID player) {
		exp.getDatabaseManager().checkConnection();
	      try {
	    	  tableName = exp.getConfigHandler().getString("database.mysql.tableName");
	 
	        String sql = "SELECT `player_uuid` FROM `" + tableName + "` WHERE `player_uuid` = ?";
	        PreparedStatement preparedUpdateStatement = conn.prepareStatement(sql);
	        preparedUpdateStatement.setString(1, player.toString());
	        
	        
	        ResultSet result = preparedUpdateStatement.executeQuery();
	 
	        while (result.next()) {
	        	return true;
	        }
	      } catch (SQLException e) {
	        Exp.log.severe("Error: " + e.getMessage());
	      }
	      return false;
    }
	
	public boolean createAccount(UUID player, Player name) {
		exp.getDatabaseManager().checkConnection();
		try {
			tableName = exp.getConfigHandler().getString("database.mysql.tableName");
			 
	        String sql = "INSERT INTO `" + tableName + "`(`player_uuid`, `player_name`, `exp`, `exp_to_level`, `total_exp`, `exp_lvl`, `last_seen`) " + "VALUES(?, ?, ?, ?, ?, ?, ?)";
	        PreparedStatement preparedStatement = conn.prepareStatement(sql);
	        
	        preparedStatement.setString(1, player.toString() + "");
	        if (name == null) {
	        	preparedStatement.setString(2, "none");
	        } else {
	        	preparedStatement.setString(2, name.getName().toString() + "");
	        }
	        preparedStatement.setFloat(3, 0);
	        preparedStatement.setInt(4, 0);
	        preparedStatement.setInt(5, 0);
	        preparedStatement.setInt(6, 0);
	        preparedStatement.setString(7, String.valueOf(System.currentTimeMillis()));
	        
	        preparedStatement.executeUpdate();
	        return true;
	      } catch (SQLException e) {
	    	  Exp.log.severe("Error: " + e.getMessage());
	      }
		return false;
	}
	
	public Float getExp(UUID player) {
		if (!hasAccount(player)) {
			createAccount(player, null);
		}
		
	      try {
	    	  tableName = exp.getConfigHandler().getString("database.mysql.tableName");
	 
	        String sql = "SELECT `exp` FROM `" + tableName + "` WHERE `player_uuid` = ?";
	        
	        PreparedStatement preparedUpdateStatement = conn.prepareStatement(sql);
	        preparedUpdateStatement.setString(1, player.toString());
	        ResultSet result = preparedUpdateStatement.executeQuery();
	 
	        while (result.next()) {
	        	return result.getFloat("exp");
	        }
	      } catch (SQLException e) {
	    	  Exp.log.severe("Error: " + e.getMessage());
	      }
		return null;
	}
	
	public Integer getLvl(UUID player) {
		if (!hasAccount(player)) {
			createAccount(player, null);
		}
		
	      try {
	    	  tableName = exp.getConfigHandler().getString("database.mysql.tableName");
	 
	        String sql = "SELECT `exp_lvl` FROM `" + tableName + "` WHERE `player_uuid` = ?";
	        
	        PreparedStatement preparedUpdateStatement = conn.prepareStatement(sql);
	        preparedUpdateStatement.setString(1, player.toString());
	        ResultSet result = preparedUpdateStatement.executeQuery();
	 
	        while (result.next()) {
	        	return result.getInt("exp_lvl");
	        }
	      } catch (SQLException e) {
	    	  Exp.log.severe("Error: " + e.getMessage());
	      }
		return null;
	}
	
	public Integer getTotalExp(UUID player) {
		if (!hasAccount(player)) {
			createAccount(player, null);
		}
		
	      try {
	    	  tableName = exp.getConfigHandler().getString("database.mysql.tableName");
	 
	        String sql = "SELECT `total_exp` FROM `" + tableName + "` WHERE `player_uuid` = ?";
	        
	        PreparedStatement preparedUpdateStatement = conn.prepareStatement(sql);
	        preparedUpdateStatement.setString(1, player.toString());
	        ResultSet result = preparedUpdateStatement.executeQuery();
	 
	        while (result.next()) {
	        	return result.getInt("total_exp");
	        }
	      } catch (SQLException e) {
	    	  Exp.log.severe("Error: " + e.getMessage());
	      }
		return null;
	}
	
	public boolean setExperience(UUID player, Player name, Float experience, Integer expToLevel, Integer totalExp, Integer lvl) {
		if (!hasAccount(player)) {
			createAccount(player, name);
		}
		
        try {
        	tableName = exp.getConfigHandler().getString("database.mysql.tableName");
        	
			String updateSqlExp = "UPDATE `" + tableName + "` " + "SET `player_name` = ?" + ", `exp` = ?" + ", `exp_to_level` = ?" + ", `total_exp` = ?" + ", `exp_lvl` = ?" + ", `last_seen` = ?" + " WHERE `player_uuid` = ?";
			PreparedStatement preparedUpdateStatement = conn.prepareStatement(updateSqlExp);
			preparedUpdateStatement.setString(1, name.getName().toString() + "");
			preparedUpdateStatement.setFloat(2, experience);
			preparedUpdateStatement.setInt(3, expToLevel);
			preparedUpdateStatement.setInt(4, totalExp);
			preparedUpdateStatement.setInt(5, lvl);
			preparedUpdateStatement.setString(6, String.valueOf(System.currentTimeMillis()));
			preparedUpdateStatement.setString(7, player.toString() + "");
			
			preparedUpdateStatement.executeUpdate();
			return true;
		} catch (SQLException e) {
			Exp.log.severe("Error: " + e.getMessage());
		}
        return false;
	}

}
