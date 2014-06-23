package GitHubAnalyser;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;


public class mySqlAPI {		
	
	private static Connection connect;	
	
	protected void connection() throws SQLException, ClassNotFoundException {		
		Class.forName("com.mysql.jdbc.Driver");
		connect = DriverManager.getConnection(Variables.MySQL_host, Variables.MySQL_username, Variables.MySQL_password);
		System.out.println( "-------------------\nConnection to  " + Variables.MySQL_host + " established!\n-------------------\n" );
	}		
	
	protected int executeQuery(String query) throws SQLException {
		int maxInt = 0;
		
		PreparedStatement statement = connect.prepareStatement( query );
		ResultSet queryResult = statement.executeQuery();
		while(queryResult.next()){
			maxInt = (int) queryResult.getObject("max");
		}
		statement.close();	
		return maxInt;		
	}	
	
	protected Integer searchIdQuery(String column, String table, String searchColumn, String searchValue) throws SQLException {
		int id = 0;
		PreparedStatement statement = connect.prepareStatement( " SELECT " + column 
															  + " FROM " + table 
															  + " WHERE " + searchColumn + "='" + searchValue + "';" );
		ResultSet queryResult = statement.executeQuery();
		while(queryResult.next()){
			id = (int) queryResult.getObject(column);
		}
		statement.close();	
		return id;		
	}	
	
	protected void insertData(String value, String query) 
			  throws SQLException, ClassNotFoundException {		
		PreparedStatement statement = connect.prepareStatement( query );	
		statement.setString( 1, value );
		statement.executeUpdate();
		statement.close();
	}
	protected void inserData(String value1, String value2, String query)
			  throws SQLException, ClassNotFoundException {	
		PreparedStatement statement = connect.prepareStatement( query );	
		statement.setString( 1, value1 );
		statement.setString( 2, value2 );
		statement.executeUpdate();
		statement.close();
	}
	protected void inserData(String value1, String value2, String value3, String query)
			  throws SQLException, ClassNotFoundException {	
		PreparedStatement statement = connect.prepareStatement( query );	
		statement.setString( 1, value1 );
		statement.setString( 2, value2 );
		statement.setString( 3, value3 );
		statement.executeUpdate();
		statement.close();
	}	
	
	protected void deleteAllData() throws SQLException{															
		ArrayList<String> tablesArray = new ArrayList<String>(Arrays.asList("contributions","forks","ownership","repository", "rmbc","rmcc","rmdc","rmde","rmfo","rmtc","rmwa","rmwcc","rmwd","umbc","umcc","umdc","umde","umfo","umtc","umwa","umwcc","umwd","user","watches"));
		for ( int i = 0; i < tablesArray.size(); i ++ ){
			truncateTable(tablesArray.get(i));
		}
	}
	
	private void truncateTable(String table) throws SQLException{
		PreparedStatement statement = connect.prepareStatement( " TRUNCATE " + table + "; " );																
		statement.executeUpdate();
		statement.close();
		System.out.println("Table ### " + table + " ### was successfully TRUNCATED!");
	}
	
	
	protected void shutDown() throws SQLException {
		connect.close();
		System.out.println( "-------------------\nCLOSED connection to " + Variables.MySQL_host + "!\n-------------------\n\n" );
	}


	
//	public static void main(String[] args) throws ClassNotFoundException, SQLException {
//		
//		mySqlAPI API = new mySqlAPI();
//		
////		ArrayList<String> sqlSearchRepoQuery = new ArrayList<String>(Arrays.asList( "repositoryId", "repository", "repoName" ));
////		ArrayList<String> sqlSearchUserQuery = new ArrayList<String>(Arrays.asList( "userId", "user", "username" ));
////		
//		API.connection();
//		API.deleteAllData();
//		API.shutDown();
//		
//	}
	
	
	
	
}
