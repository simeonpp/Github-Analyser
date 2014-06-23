package GitHubAnalyser;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class analyserBoundary {
	
	
	private analyserController analyserController = new analyserController();
	private databasePaths database = new databasePaths();
	

	public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException {
		
		analyserBoundary run = new analyserBoundary();	
		
		/*
		 * Methods to create the Main GitHub Database
		 * Choose ONLY ONE 
		 */		
//		run.coldStart();
		run.newFreshStart();		
		/*
		 * Methods to create Network Database
		 */				
		run.createModuleNetwork();
		run.createCommitersNetwork();		
		/*
		 * Methods to execute SNA analysis on the database
		 */	
//		run.SNA_ModuleNetwork();
//		run.SNA_CommitersNetwork();
		
		run.insertDataIntoMySQLDatabase();
		
	} 	
	
	
	
	
	
	
	
	



	private void coldStart() throws IOException {
		System.out.println("Cold Start Method Execution...\n");
		analyserController.createIndexes( database.getGitHubDatabasePath(), Variables.MainDatabase  );						
		analyserController.addData( database.getGitHubDatabasePath() );								
	} 	
	private void newFreshStart() throws IOException {
		System.out.println("New Fresh Start Method Execution...\n");	
		analyserController.removeData( database.getGitHubDatabasePath() );							
		analyserController.createIndexes( database.getGitHubDatabasePath(), Variables.MainDatabase );						
		analyserController.addData( database.getGitHubDatabasePath() );								
	}		
	
	

	private void createModuleNetwork() {	
		int ModuleNetwork = Variables.ModuleNetwork;
		analyserController.removeData( database.getModuleNetworkDatabasePath() );
		analyserController.createIndexes( database.getModuleNetworkDatabasePath(), ModuleNetwork );		
		List<String> splitList = analyserController.executeNetworkQuery( database.getGitHubDatabasePath(), ModuleNetwork);
		analyserController.createNetworkDatabase( Variables.splitResultStringInThreeParts( splitList, Variables.Name1 ), Variables.convertStringListToIntegerList ( Variables.splitResultStringInThreeParts( splitList, Variables.RelString )), Variables.splitResultStringInThreeParts( splitList, Variables.Name2 ), database.getModuleNetworkDatabasePath() ,ModuleNetwork );
	}
	private void createCommitersNetwork() {	
		int CommitersNetwork = Variables.CommitersNetwork;
		analyserController.removeData( database.getCommitersNetworkDatabasePath() );
		analyserController.createIndexes( database.getCommitersNetworkDatabasePath(), CommitersNetwork );		
		List<String> splitList = analyserController.executeNetworkQuery( database.getGitHubDatabasePath(), CommitersNetwork );
		analyserController.createNetworkDatabase( Variables.splitResultStringInThreeParts( splitList, Variables.Name1 ), Variables.convertStringListToIntegerList ( Variables.splitResultStringInThreeParts( splitList, Variables.RelString )), Variables.splitResultStringInThreeParts( splitList, Variables.Name2 ), database.getCommitersNetworkDatabasePath() ,CommitersNetwork );		
	}	
	
	
	
	private void SNA_ModuleNetwork() {		
		int SNA_ModuleNetwork = Variables.SNA_ModuleNetwork;
		List<String> splitList = analyserController.executeNetworkQuery( database.getModuleNetworkDatabasePath(), SNA_ModuleNetwork );
		analyserController.networkAnalyse( Variables.splitResultStringInOnePart( splitList ), database.getModuleNetworkDatabasePath(),  SNA_ModuleNetwork  );		
	}	
	private void SNA_CommitersNetwork() {		
		int SNA_CommitersNetwork = Variables.SNA_CommitersNetwork;
		List<String> splitList = analyserController.executeNetworkQuery( database.getCommitersNetworkDatabasePath(), SNA_CommitersNetwork );
		analyserController.networkAnalyse( Variables.splitResultStringInOnePart( splitList ), database.getCommitersNetworkDatabasePath(),  SNA_CommitersNetwork  );		
	}	
	
	
	
	
	
	private void insertDataIntoMySQLDatabase() throws ClassNotFoundException, SQLException {
//		analyserController.MySQLDeleteData();
		analyserController.MySQLInsertData();
		analyserController.MySQLInsertMetrics();
	}
}
