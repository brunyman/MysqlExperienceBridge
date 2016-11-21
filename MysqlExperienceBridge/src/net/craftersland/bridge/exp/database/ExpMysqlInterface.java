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
	
	public ExpMysqlInterface(Exp exp) {
		this.exp = exp;
	}
	
	public boolean hasAccount(UUID player) {
		PreparedStatement preparedUpdateStatement = null;
		ResultSet result = null;
		Connection conn = exp.getDatabaseManager().getConnection();
	      try {	 
	        String sql = "SELECT `player_uuid` FROM `" + exp.getConfigHandler().getString("database.mysql.tableName") + "` WHERE `player_uuid` = ?";
	        preparedUpdateStatement = conn.prepareStatement(sql);
	        preparedUpdateStatement.setString(1, player.toString());
	        result = preparedUpdateStatement.executeQuery();
	        while (result.next()) {
	        	return true;
	        }
	      } catch (SQLException e) {
	        Exp.log.severe("Error: " + e.getMessage());
	      } finally {
		    	try {
		    		if (result != null) {
		    			result.close();
		    		}
		    		if (preparedUpdateStatement != null) {
		    			preparedUpdateStatement.close();
		    		}
		    	} catch (Exception e) {
		    		e.printStackTrace();
		    	}
		    }
	      return false;
    }
	
	public void createAccount(UUID player, Player name) {
		PreparedStatement preparedStatement = null;
		Connection conn = exp.getDatabaseManager().getConnection();
		try {			 
	        String sql = "INSERT INTO `" + exp.getConfigHandler().getString("database.mysql.tableName") + "`(`player_uuid`, `player_name`, `exp`, `exp_to_level`, `total_exp`, `exp_lvl`, `last_seen`) " + "VALUES(?, ?, ?, ?, ?, ?, ?)";
	        preparedStatement = conn.prepareStatement(sql);
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
	      } catch (SQLException e) {
	    	  Exp.log.severe("Error: " + e.getMessage());
	      } finally {
	    	  try {
	    		  if (preparedStatement != null) {
	    			  preparedStatement.close();
	    		  }
	    	  } catch (Exception e) {
	    		  e.printStackTrace();
	    	  }
	      }
	}
	
	public Float getExp(UUID player) {
		if (!hasAccount(player)) {
			createAccount(player, null);
		}
		PreparedStatement preparedUpdateStatement = null;
		ResultSet result = null;
		Connection conn = exp.getDatabaseManager().getConnection();
	      try {
	        String sql = "SELECT `exp` FROM `" + exp.getConfigHandler().getString("database.mysql.tableName") + "` WHERE `player_uuid` = ?";
	        preparedUpdateStatement = conn.prepareStatement(sql);
	        preparedUpdateStatement.setString(1, player.toString());
	        result = preparedUpdateStatement.executeQuery();
	        while (result.next()) {
	        	return result.getFloat("exp");
	        }
	      } catch (SQLException e) {
	    	  Exp.log.severe("Error: " + e.getMessage());
	      } finally {
		    	try {
		    		if (result != null) {
		    			result.close();
		    		}
		    		if (preparedUpdateStatement != null) {
		    			preparedUpdateStatement.close();
		    		}
		    	} catch (Exception e) {
		    		e.printStackTrace();
		    	}
		    }
		return null;
	}
	
	public Integer getLvl(UUID player) {
		if (!hasAccount(player)) {
			createAccount(player, null);
		}
		PreparedStatement preparedUpdateStatement = null;
		ResultSet result = null;
		Connection conn = exp.getDatabaseManager().getConnection();
	      try {
	        String sql = "SELECT `exp_lvl` FROM `" + exp.getConfigHandler().getString("database.mysql.tableName") + "` WHERE `player_uuid` = ?";
	        preparedUpdateStatement = conn.prepareStatement(sql);
	        preparedUpdateStatement.setString(1, player.toString());
	        result = preparedUpdateStatement.executeQuery();
	        while (result.next()) {
	        	return result.getInt("exp_lvl");
	        }
	      } catch (SQLException e) {
	    	  Exp.log.severe("Error: " + e.getMessage());
	      } finally {
		    	try {
		    		if (result != null) {
		    			result.close();
		    		}
		    		if (preparedUpdateStatement != null) {
		    			preparedUpdateStatement.close();
		    		}
		    	} catch (Exception e) {
		    		e.printStackTrace();
		    	}
		    }
		return null;
	}
	
	public Integer getTotalExp(UUID player) {
		if (!hasAccount(player)) {
			createAccount(player, null);
		}
		PreparedStatement preparedUpdateStatement = null;
		ResultSet result = null;
		Connection conn = exp.getDatabaseManager().getConnection();
	      try {	 
	        String sql = "SELECT `total_exp` FROM `" + exp.getConfigHandler().getString("database.mysql.tableName") + "` WHERE `player_uuid` = ?";
	        preparedUpdateStatement = conn.prepareStatement(sql);
	        preparedUpdateStatement.setString(1, player.toString());
	        result = preparedUpdateStatement.executeQuery();
	        while (result.next()) {
	        	return result.getInt("total_exp");
	        }
	      } catch (SQLException e) {
	    	  Exp.log.severe("Error: " + e.getMessage());
	      } finally {
		    	try {
		    		if (result != null) {
		    			result.close();
		    		}
		    		if (preparedUpdateStatement != null) {
		    			preparedUpdateStatement.close();
		    		}
		    	} catch (Exception e) {
		    		e.printStackTrace();
		    	}
		    }
		return null;
	}
	
	public boolean setExperience(UUID player, Player name, Float experience, Integer expToLevel, Integer totalExp, Integer lvl) {
		if (!hasAccount(player)) {
			createAccount(player, name);
		}
		PreparedStatement preparedUpdateStatement = null;
		Connection conn = exp.getDatabaseManager().getConnection();
        try {
			String updateSqlExp = "UPDATE `" + exp.getConfigHandler().getString("database.mysql.tableName") + "` " + "SET `player_name` = ?" + ", `exp` = ?" + ", `exp_to_level` = ?" + ", `total_exp` = ?" + ", `exp_lvl` = ?" + ", `last_seen` = ?" + " WHERE `player_uuid` = ?";
			preparedUpdateStatement = conn.prepareStatement(updateSqlExp);
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
		} finally {
			try {
				if (preparedUpdateStatement != null) {
					preparedUpdateStatement.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
        return false;
	}

}
