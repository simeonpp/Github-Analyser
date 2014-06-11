package GitHubAnalyser;

import java.io.IOException;

public class databaseCreatorAPI {
	
	private databaseCreator databaseCreator = new databaseCreator();
	
	
	public static void main(String[] args) throws IOException {

		databaseCreatorAPI exe = new databaseCreatorAPI();	
		
		/*
		 * Choose ONLY ONE Main methods for running the application
		 */		
//		exe.coldStart();
		exe.newFreshStart();
//		exe.checkForUpdates();
	} 	
	
	
	
	
	private void coldStart() throws IOException {
		System.out.println("Cold Start Method Execution...\n");
		databaseCreator.createIndexes();						// create Neo4j Indexes
		databaseCreator.addData();								// create Nodes and Relationships
	} 	
	private void newFreshStart() throws IOException {
		System.out.println("New Fresh Start Method Execution...\n");	
		databaseCreator.removeData();							// delete all database records
		databaseCreator.createIndexes();						// create Neo4j Indexes
		databaseCreator.addData();								// create Nodes and Relationships
	}	
	private void checkForUpdates() {
		System.out.println("Check For Updates Method Execution...\n");			
	}	
}
