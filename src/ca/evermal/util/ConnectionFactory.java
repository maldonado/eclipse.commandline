package ca.evermal.util;

import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

import eclipse.commandline.Activator;

public class ConnectionFactory {

	private static final String dataBasePath = getPath();

	public static Connection getSqlite(){
		Connection connection = null;

		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:" +dataBasePath+ "test.db");
		} catch ( Exception e ) {
			System.out.println(e);
		}

		return connection;	
	}

	private static String getPath() {
		Properties properties = new Properties();
		try{
			URL url = Activator.getMyBundle().getEntry("eclipseCommandline.properties");
			InputStream stream = url.openStream();
			properties.load(stream);	
		}
		catch(Exception e ){
			System.out.println(e);
		}
		return properties.getProperty("database.path");
	}

	public static void verifyDataBase(){
		Connection dataBaseConnection = ConnectionFactory.getSqlite();
		try {
			String commentClass = "CREATE TABLE IF NOT EXISTS comment_class (id integer primary key autoincrement,projectName text,"
					+ "fileName text,className text,access text,isAbstract text,isEnum text,isInterface text, startline integer, endline integer)";

			PreparedStatement ps1 = dataBaseConnection.prepareStatement(commentClass);
			ps1.execute();

			String comment = "CREATE TABLE IF NOT EXISTS comment (id integer primary key autoincrement,	commentClassId integer,	startLine integer,"
					+ "endLine integer,commentText text,type text,location text,description text)";

			PreparedStatement ps2 = dataBaseConnection.prepareStatement(comment);
			ps2.execute();
		}
		catch(SQLException e){
			System.out.println(e);
		}
	}

}