package me.igwb.DeathPain;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class MySQLConnector {

	private Plugin parent;
	private String host, user, password, database, url;
	private int port;
	
	public MySQLConnector(Plugin parent, String host, int port, String user, String password, String database) {
		
		this.parent = parent;
		
		this.host = host;
		this.port = port;
		this.user = user;
		
		this.password = password;
		this.database = database;
		
		url = "jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database;
		
		initialize();
		
		if(parent.getDebug()) {
			printSQLVersion();
		}
	}
	
	private void initialize() {
       
		Connection con = null;
        Statement st = null;
		
        try {
            con = getConnection();
            st = con.createStatement();
            
            st.executeUpdate("USE " + database);
            st.executeUpdate("CREATE TABLE IF NOT EXISTS Players(Id INT PRIMARY KEY AUTO_INCREMENT, Name VARCHAR(30), DeathCount INT, LastDeath TIMESTAMP);");
            st.executeUpdate("CREATE TABLE IF NOT EXISTS Deaths(Id INT PRIMARY KEY AUTO_INCREMENT, Name VARCHAR(30), Cause VARCHAR(25), Killer INT, DeathTime TIMESTAMP, x INT, y INT, z INT);");
            
            

        } catch (SQLException e) {
        	parent.LogSevere(MySQLConnector.class.getName());
        	parent.LogSevere(e.getMessage());

        } finally {
            try {
                if (st != null) {
                    st.close();
                }
                if (con != null) {
                    con.close();
                }

            } catch (SQLException e) {
            	parent.LogMessage(MySQLConnector.class.getName());
            	parent.LogSevere(e.getMessage());
            }
        }
	}
	
	private void printSQLVersion() {
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
		
        try {
            con = DriverManager.getConnection(url, user, password);
            st = con.createStatement();
            rs = st.executeQuery("SELECT VERSION()");

            if (rs.next()) {
            	parent.LogMessage("MySQL initialized!");
                parent.LogMessage("MySQL version is " + rs.getString(1));
            }

        } catch (SQLException ex) {
        	parent.LogSevere(MySQLConnector.class.getName());
        	parent.LogSevere(ex.getMessage());

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
                if (con != null) {
                    con.close();
                }

            } catch (SQLException ex) {
            	parent.LogMessage(MySQLConnector.class.getName());
            	parent.LogSevere(ex.getMessage());
            }
        }
	}
	
	private Connection getConnection() {
		
		try {
			return DriverManager.getConnection(url, user, password);
		} catch (SQLException e) {
        	parent.LogSevere(MySQLConnector.class.getName());
        	parent.LogSevere(e.getMessage());
        	return null;
		}
	}
	
	public void logDeath(String name, String cause, String killer, long timeOfDeath, int x, int y, int z) {
       
		DateFormat dateFormat = new SimpleDateFormat("yyyy-M-d HH:m:s");
		parent.LogMessage(dateFormat.format(timeOfDeath));
		
		Connection con = null;
        Statement st = null;
        java.sql.PreparedStatement pst = null;
		ResultSet rs = null;
		
		
		 try {
	            con = getConnection();
	            st = con.createStatement();
	            
	            
	            st.executeUpdate("USE " + database);
	            
	            //Check if the player already died - create entry if not
	            pst = con.prepareStatement("SELECT * FROM players WHERE Name=?;");
	            pst.setString(1, name);
	            rs = pst.executeQuery();
	            
	            if(!rs.next()) {
	            	pst = con.prepareStatement("INSERT INTO players(Name, DeathCount, LastDeath) Values(?, ?, ?);");
	            	pst.setString(1, name);
	            	pst.setInt(2, 1);
	            	pst.setString(3, dateFormat.format(timeOfDeath));
	            	
	            	pst.executeUpdate();
	            
	            } else {
	            	pst = con.prepareStatement("UPDATE players SET DeathCount = ? WHERE name = ?;");
	            	pst.setInt(1, getDeathCount(name) + 1);
	            	pst.setString(2, name);
	            	
	            	pst.executeUpdate();
	            }

	            
	            //st.executeUpdate("INSERT INTO deaths(Name, Cause, Killer, DeathTime, x, y, z) VALUES(" + name + ", " + cause + ", " + killer + ", \'" + dateFormat.format(timeOfDeath) + "\', " + x + ", " + y + ", " + z + ");");
	            pst = con.prepareStatement("INSERT INTO deaths(Name, Cause, Killer, DeathTime, x, y, z) VALUES(?, ?, ?, ?, ?, ?, ?);");
	            
	            pst.setString(1, name);
	            pst.setString(2, cause);
	            pst.setString(3, killer);
	            pst.setString(4, dateFormat.format(timeOfDeath));
	            pst.setInt(5, x);
	            pst.setInt(6, y);
	            pst.setInt(7, z);
	            
	            pst.executeUpdate();
	            
	        } catch (SQLException e) {
	        	parent.LogSevere(MySQLConnector.class.getName());
	        	parent.LogSevere(e.getMessage());

	        } finally {
	            try {
	                if (st != null) {
	                    st.close();
	                }
	                if (pst != null) {
	                    pst.close();
	                }
	                if(rs != null) {
	                	rs.close();
	                }
	                if (con != null) {
	                    con.close();
	                }

	            } catch (SQLException e) {
	            	parent.LogMessage(MySQLConnector.class.getName());
	            	parent.LogSevere(e.getMessage());
	            }
	        }	
	}
	
	public int getDeathCount(String player) {
	
		Connection con = null;
        Statement st = null;
		ResultSet rs = null;
		
		 try {
	            con = getConnection();
	            st = con.createStatement();
	            
	            st.executeUpdate("USE " + database);
	           rs = st.executeQuery("SELECT DeathCount FROM players WHERE name= \'" + player + "\';");
	          
	            if(rs.next()) {
	            	return rs.getInt(1);
	            } else {
	            	return 0;
	            }

	        } catch (SQLException e) {
	        	parent.LogSevere(MySQLConnector.class.getName());
	        	parent.LogSevere(e.getMessage());

	        	return -1;
	        } finally {
	            try {
	                if (st != null) {
	                    st.close();
	                }
	                if(rs != null) {
	                	rs.close();
	                }
	                if (con != null) {
	                    con.close();
	                }

	            } catch (SQLException e) {
	            	parent.LogMessage(MySQLConnector.class.getName());
	            	parent.LogSevere(e.getMessage());
	            }
	        }
	}
	
}
