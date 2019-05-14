package net.craftersland.bridge.exp.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

import org.bukkit.Bukkit;

import net.craftersland.bridge.exp.Exp;

public class MysqlSetup {
	
	private Connection conn = null;
	private Exp eco;
	
	public MysqlSetup(Exp eco) {
		this.eco = eco;
		connectToDatabase();
		setupDatabase();
		databaseMaintenanceTask();
	}
	
	public void connectToDatabase() {
		Exp.log.info("Connecting to the database...");
		try {
       	 	//Load Drivers
            Class.forName("com.mysql.jdbc.Driver");
            Properties properties = new Properties();
            properties.setProperty("user", eco.getConfigHandler().getString("database.mysql.user"));
            properties.setProperty("password", eco.getConfigHandler().getString("database.mysql.password"));
            properties.setProperty("autoReconnect", "true");
            properties.setProperty("verifyServerCertificate", "false");
            properties.setProperty("useSSL", eco.getConfigHandler().getString("database.mysql.ssl"));
            properties.setProperty("requireSSL", eco.getConfigHandler().getString("database.mysql.ssl"));
            //Connect to database
            conn = DriverManager.getConnection("jdbc:mysql://" + eco.getConfigHandler().getString("database.mysql.host") + ":" + eco.getConfigHandler().getString("database.mysql.port") + "/" + eco.getConfigHandler().getString("database.mysql.databaseName") + "?", properties);
           
          } catch (ClassNotFoundException e) {
        	  Exp.log.severe("Could not locate drivers for mysql! Error: " + e.getMessage());
            return;
          } catch (SQLException e) {
        	  Exp.log.severe("Could not connect to mysql database! Error: " + e.getMessage());
            return;
          }
		Exp.log.info("Database connection successful!");
	}
	
	public void setupDatabase() {
		//Create tables if needed
		if (conn != null) {
			PreparedStatement query = null;
		      try {	        
		        String data = "CREATE TABLE IF NOT EXISTS `" + eco.getConfigHandler().getString("database.mysql.tableName") + "` (id int(10) AUTO_INCREMENT, player_uuid varchar(50) NOT NULL UNIQUE, player_name varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL, exp float(10,10) NOT NULL, exp_to_level int(10) NOT NULL, total_exp int(20) NOT NULL, exp_lvl int(10) NOT NULL, last_seen varchar(30) NOT NULL, PRIMARY KEY(id));";
		        query = conn.prepareStatement(data);
		        query.execute();
		      } catch (SQLException e) {
		        e.printStackTrace();
		        Exp.log.severe("Error creating tables! Error: " + e.getMessage());
		      } finally {
		    	  try {
		    		  if (query != null) {
		    			  query.close();
		    		  }
		    	  } catch (Exception e) {
		    		  e.printStackTrace();
		    	  }
		      }
		}
	}
	
	public Connection getConnection() {
		checkConnection();
		return conn;
	}
	
	public void checkConnection() {
		try {
			if (conn == null) {
				Exp.log.warning("Connection failed. Reconnecting...");
				reConnect();
			}
			if (!conn.isValid(3)) {
				Exp.log.warning("Connection is idle or terminated. Reconnecting...");
				reConnect();
			}
			if (conn.isClosed() == true) {
				Exp.log.warning("Connection is closed. Reconnecting...");
				reConnect();
			}
		} catch (Exception e) {
			Exp.log.severe("Could not reconnect to Database! Error: " + e.getMessage());
		}
	}
	
	public boolean reConnect() {
		try {            
            long start = 0;
			long end = 0;
			
		    start = System.currentTimeMillis();
		    Exp.log.info("Attempting to establish a connection to the MySQL server!");
            Class.forName("com.mysql.jdbc.Driver");
            Properties properties = new Properties();
            properties.setProperty("user", eco.getConfigHandler().getString("database.mysql.user"));
            properties.setProperty("password", eco.getConfigHandler().getString("database.mysql.password"));
            properties.setProperty("autoReconnect", "true");
            properties.setProperty("verifyServerCertificate", "false");
            properties.setProperty("useSSL", eco.getConfigHandler().getString("database.mysql.ssl"));
            properties.setProperty("requireSSL", eco.getConfigHandler().getString("database.mysql.ssl"));
            properties.setProperty("useUnicode", "true");
            properties.setProperty("characterEncoding", "utf8");
            properties.setProperty("characterSetResults", "utf8");
            properties.setProperty("connectionCollation", "utf8mb4_unicode_ci");
            conn = DriverManager.getConnection("jdbc:mysql://" + eco.getConfigHandler().getString("database.mysql.host") + ":" + eco.getConfigHandler().getString("database.mysql.port") + "/" + eco.getConfigHandler().getString("database.mysql.databaseName"), properties);
		    end = System.currentTimeMillis();
		    Exp.log.info("Connection to MySQL server established!");
		    Exp.log.info("Connection took " + ((end - start)) + "ms!");
            return true;
		} catch (Exception e) {
			Exp.log.severe("Error re-connecting to the database! Error: " + e.getMessage());
			return false;
		}
	}
	
	public void closeConnection() {
		try {
			Exp.log.info("Closing database connection...");
			conn.close();
			conn = null;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void databaseMaintenanceTask() {
		if (eco.getConfigHandler().getBoolean("database.maintenance.enabled") == true) {
			Bukkit.getScheduler().runTaskLaterAsynchronously(eco, new Runnable() {

				@Override
				public void run() {
					if (conn != null) {
						long inactivityDays = Long.parseLong(eco.getConfigHandler().getString("database.maintenance.inactivity"));
						long inactivityMils = inactivityDays * 24 * 60 * 60 * 1000;
						long curentTime = System.currentTimeMillis();
						long inactiveTime = curentTime - inactivityMils;
						Exp.log.info("Database maintenance task started...");
						PreparedStatement preparedStatement = null;
						try {
							String sql = "DELETE FROM `" + eco.getConfigHandler().getString("database.mysql.tableName") + "` WHERE `last_seen` < ?";
							preparedStatement = conn.prepareStatement(sql);
							preparedStatement.setString(1, String.valueOf(inactiveTime));
							preparedStatement.execute();
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							try {
								if (preparedStatement != null) {
									preparedStatement.close();
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						Exp.log.info("Database maintenance complete!");
					}
				}
				
			}, 100 * 20L);
		}
	}

}
