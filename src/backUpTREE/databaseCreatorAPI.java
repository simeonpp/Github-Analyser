package backUpTREE;

public class databaseCreatorAPI {
	
	private databaseCreatorTREE databaseCreatorTREE = new databaseCreatorTREE();
	
	
	public static void main(String[] args) {

		databaseCreatorAPI exe = new databaseCreatorAPI();	
		
		/*
		 * Choose ONLY ONE Main methods for running the application
		 */		
//		exe.coldStart();
		exe.newFreshStart();
//		exe.checkForUpdates();

	} 	
	
		
		
	
	
	
	private void coldStart() {
		System.out.println("Cold Start Method Execution...\n");
		databaseCreatorTREE.createIndexes();						// create Neo4j Indexes
		databaseCreatorTREE.addData();								// create Nodes and Relationships
	} 	
	private void newFreshStart() {
		System.out.println("New Fresh Start Method Execution...\n");	
		databaseCreatorTREE.removeData();							// delete all database records
		databaseCreatorTREE.createIndexes();						// create Neo4j Indexes
		databaseCreatorTREE.addData();								// create Nodes and Relationships
	}	
	private void checkForUpdates() {
		System.out.println("Check For Updates Method Execution...\n");			
	}	
}
