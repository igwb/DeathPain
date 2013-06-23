package me.igwb.DeathPain;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.sql.Connection;
import java.sql.Statement;

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
		
		if(parent.getDebug()) {
			printSQLVersion();
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
	
}
