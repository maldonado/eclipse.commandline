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

	private static final Properties PROPERTIES = loadProperties();

	private static final String LITE_CREATE_TABLE_COMMENT_CLASS ="CREATE TABLE IF NOT EXISTS comment_class (id integer primary key autoincrement,projectName text,"
			+ "fileName text,className text,access text,isAbstract text,isEnum text,isInterface text, startline integer, endline integer, analyzed integer)";

	private static final String LITE_CREATE_TABLE_COMMENT = "CREATE TABLE IF NOT EXISTS comment (id integer primary key autoincrement,"
			+ "commentClassId integer,startLine integer, endLine integer,commentText text,type text,location text,description text , dictionary_hit integer, " 
			+ "jdeodorant_hit integer, refactoring_list_name text)";

	private static final String LITE_CREATE_TABLE_PROCESSED_COMMENT = "CREATE TABLE IF NOT EXISTS processed_comment (id integer primary key autoincrement,"
			+ "commentClassId integer,startLine integer, endLine integer,commentText text,type text,location text,description text, dictionary_hit integer, "
			+ "jdeodorant_hit integer, refactoring_list_name text)";

	private static final String PSQL_CREATE_TABLE_COMMENT_CLASS ="CREATE TABLE IF NOT EXISTS comment_class (id serial primary key ,projectName text,"
			+ "fileName text,className text,access text,isAbstract text,isEnum text,isInterface text, startline integer, endline integer, analyzed integer)";

	private static final String PSQL_CREATE_TABLE_COMMENT = "CREATE TABLE IF NOT EXISTS comment (id serial primary key,"
			+ "commentClassId integer,startLine integer, endLine integer,commentText text,type text,location text,description text , dictionary_hit integer, " 
			+ "jdeodorant_hit integer, refactoring_list_name text)";

	private static final String PSQL_CREATE_TABLE_PROCESSED_COMMENT = "CREATE TABLE IF NOT EXISTS processed_comment (id serial primary key,"
			+ "commentClassId integer,startLine integer, endLine integer,commentText text,type text,location text,description text, dictionary_hit integer, "
			+ "jdeodorant_hit integer, refactoring_list_name text)";
	
	
	public static Connection getSqlite(){
		Connection connection = null;
		String dataBasePath = PROPERTIES.getProperty("sqlite.database.path");
		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:" + dataBasePath);
		} catch ( Exception e ) {
			System.out.println(e);
		}

		return connection;	
	}
	
	public static Connection getPostgresql(){
		Connection connection = null;
		String database = PROPERTIES.getProperty("psql.dbname");
		String username = PROPERTIES.getProperty("psql.user");
		String password = PROPERTIES.getProperty("psql.psw");
		
		try {
			 Class.forName("org.postgresql.Driver");
//	         connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/"+database+","+username+","+password);
	         connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/"+database, username, password);
		} catch ( Exception e ) {
			System.out.println(e);
		}
		
		return connection;
	}

	private static Properties loadProperties() {
		Properties properties = new Properties();
		try{
			URL url = Activator.getMyBundle().getEntry("eclipseCommandline.properties");
			InputStream stream = url.openStream();
			properties.load(stream);	
		}
		catch(Exception e ){
			System.out.println(e);
		}
		return properties;
	}

	public static Connection createSqliteTables(){
		Connection connection = getSqlite();
		try {
			createTable(LITE_CREATE_TABLE_COMMENT_CLASS, connection);
			createTable(LITE_CREATE_TABLE_COMMENT, connection);
			createTable(LITE_CREATE_TABLE_PROCESSED_COMMENT, connection);
			return connection;
		}
		catch(SQLException e){
			System.out.println(e);
			return null;
		}
	}
	
	public static Connection createPostgresqlTables(){
		Connection connection = getPostgresql();
		try {
			createTable(PSQL_CREATE_TABLE_COMMENT_CLASS, connection);
			createTable(PSQL_CREATE_TABLE_COMMENT, connection);
			createTable(PSQL_CREATE_TABLE_PROCESSED_COMMENT, connection);
			return connection;
		}
		catch(SQLException e){
			System.out.println(e);
			return null;
		}
	}

	private static void createTable(String tableSql, Connection connection) throws SQLException {
		PreparedStatement ps = connection.prepareStatement(tableSql);
		ps.execute();
	}

}