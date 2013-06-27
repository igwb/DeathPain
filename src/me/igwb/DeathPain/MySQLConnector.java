package me.igwb.DeathPain;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.sql.PreparedStatement;

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
        	parent.logSevere(MySQLConnector.class.getName());
        	parent.logSevere(e.getMessage());

        } finally {
            try {
                if (st != null) {
                    st.close();
                }
                if (con != null) {
                    con.close();
                }

            } catch (SQLException e) {
            	parent.logMessage(MySQLConnector.class.getName());
            	parent.logSevere(e.getMessage());
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
            	parent.logMessage("MySQL initialized!");
                parent.logMessage("MySQL version is " + rs.getString(1));
            }

        } catch (SQLException ex) {
        	parent.logSevere(MySQLConnector.class.getName());
        	parent.logSevere(ex.getMessage());

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
            	parent.logMessage(MySQLConnector.class.getName());
            	parent.logSevere(ex.getMessage());
            }
        }
	}
	
	private Connection getConnection() {
		
		try {
			return DriverManager.getConnection(url, user, password);
		} catch (SQLException e) {
        	parent.logSevere(MySQLConnector.class.getName());
        	parent.logSevere(e.getMessage());
        	return null;
		}
	}
	
	public void logDeath(String name, String cause, String killer, long timeOfDeath, int x, int y, int z) {
       
		DateFormat dateFormat = new SimpleDateFormat("yyyy-M-d HH:m:s");
		parent.logMessage(dateFormat.format(timeOfDeath));
		
		Connection con = null;
        java.sql.PreparedStatement pst = null;
		ResultSet rs = null;
		
		
		 try {
	            con = getConnection();
	            
	            
	            pst = con.prepareStatement("USE " + database);
	            pst.executeUpdate();
	            
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
	        	parent.logSevere(MySQLConnector.class.getName());
	        	parent.logSevere(e.getMessage());

	        } finally {
	            try {
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
	            	parent.logMessage(MySQLConnector.class.getName());
	            	parent.logSevere(e.getMessage());
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
	        	parent.logSevere(MySQLConnector.class.getName());
	        	parent.logSevere(e.getMessage());

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
	            	parent.logMessage(MySQLConnector.class.getName());
	            	parent.logSevere(e.getMessage());
	            }
	        }
	}
	
	public String[] getDeathRanking() {
	
		String[] results = null;
		Integer count, i = 0;
		
		Connection con = null;
        PreparedStatement pst = null;
		ResultSet rs = null;
		
		 try {
	            con = getConnection();

	            pst = con.prepareStatement("SELECT COUNT(*) FROM Players;");
	            rs = pst.executeQuery();
	            
	            rs.next();
	            count = rs.getInt(1);
	         
	            pst = con.prepareStatement("SELECT Name, DeathCount FROM Players ORDER BY DeathCount, Name DESC;");
	            rs = pst.executeQuery();
	            
	            results = new String[count + 1];
	            while(rs.next()) {
	            	i ++;
	            	results[i] = "[" + rs.getInt("DeathCount") + "] " + rs.getString("Name");
	            }
	            
	        } catch (SQLException e) {
	        	parent.logSevere(MySQLConnector.class.getName());
	        	parent.logSevere(e.getMessage());
	        } finally {
	            try {
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
	            	parent.logMessage(MySQLConnector.class.getName());
	            	parent.logSevere(e.getMessage());
	            }
	        }
		
		 
		return results;
	}
	
}
