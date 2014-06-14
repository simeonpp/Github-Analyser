package GitHubAnalyser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class analyserController {
	
	
	private analyserAPI analyserAPI = new analyserAPI();
	private databasePaths database = new databasePaths();
	private analyserAPI_ASD analyserAPI_ASD = new analyserAPI_ASD();
	

	public static void main(String[] args) throws IOException {
		
		analyserController run = new analyserController();	
		
		/*
		 * Methods to create the Main GitHub Database
		 * Choose ONLY ONE 
		 */		
//		run.coldStart();
//		run.newFreshStart();
//		run.checkForUpdates();
		
		
		
		/*
		 * Methods to execute SNA analysis on the database
		 * Choose ONLY ONE 
		 */				
		run.createModuleNetwork();
//		run.createCommitersNetwork();
		
//		run.SNA_ModuleNetwork();
//		run.SNA_CommitersNetwork();
		
	} 	
	
	
	
	
	
	
	
		
	
	private void coldStart() throws IOException {
		System.out.println("Cold Start Method Execution...\n");
		analyserAPI.createIndexes( database.getGitHubDatabasePath(), Variables.MainDatabase  );						
		analyserAPI.addData( database.getGitHubDatabasePath() );								
	} 	
	private void newFreshStart() throws IOException {
		System.out.println("New Fresh Start Method Execution...\n");	
		analyserAPI.removeData( database.getGitHubDatabasePath() );							
		analyserAPI.createIndexes( database.getGitHubDatabasePath(), Variables.MainDatabase );						
		analyserAPI.addData( database.getGitHubDatabasePath() );								
	}	
	private void checkForUpdates() {
		System.out.println("Check For Updates Method Execution...\n");			
	}
	
	
	
	
	
	
		
	
	

	private void createModuleNetwork() {	
		int ModuleNetwork = Variables.ModuleNetwork;
		analyserAPI.removeData( database.getModuleNetworkDatabasePath() );
		analyserAPI.createIndexes( database.getModuleNetworkDatabasePath(), ModuleNetwork );		
		List<String> splitList = analyserAPI.executeNetworkQuery( database.getGitHubDatabasePath(), ModuleNetwork );
		analyserAPI.createNetworkDatabase( Variables.splitResultStringInParts( splitList, Variables.Name1 ), Variables.convertStringListToIntegerList ( Variables.splitResultStringInParts( splitList, Variables.RelString )), Variables.splitResultStringInParts( splitList, Variables.Name2 ), database.getModuleNetworkDatabasePath() ,ModuleNetwork );
	}
	private void createCommitersNetwork() {	
		int CommitersNetwork = Variables.CommitersNetwork;
		analyserAPI.removeData( database.getCommitersNetworkDatabasePath() );
		analyserAPI.createIndexes( database.getCommitersNetworkDatabasePath(), CommitersNetwork );		
		List<String> splitList = analyserAPI.executeNetworkQuery( database.getGitHubDatabasePath(), CommitersNetwork );
		analyserAPI.createNetworkDatabase( Variables.splitResultStringInParts( splitList, Variables.Name1 ), Variables.convertStringListToIntegerList ( Variables.splitResultStringInParts( splitList, Variables.RelString )), Variables.splitResultStringInParts( splitList, Variables.Name2 ), database.getCommitersNetworkDatabasePath() ,CommitersNetwork );		
	}		
	private void SNA_ModuleNetwork() {		
		String NodeLabel = "Repository";
		String NodeProperty = "repository";
		analyserAPI_ASD.networkAnalyse( database.getModuleNetworkDatabasePath(), NodeLabel, NodeProperty );		
	}	
	private void SNA_CommitersNetwork() {		
		String NodeLabel = "User";
		String NodeProperty = "username";
		analyserAPI_ASD.networkAnalyse( database.getCommitersNetworkDatabasePath(), NodeLabel, NodeProperty );		
	}		

}
