package GitHubAnalyser;

public class gitHubAnalyser {
	
	
	private databasePaths database = new databasePaths();
	private createNetworkDatabase create = new createNetworkDatabase();
	private analyser analyser = new analyser();
	

	public static void main(String[] args) {

		
		gitHubAnalyser run = new gitHubAnalyser();		
				
//		run.createModuleNetwork();
//		run.createCommitersNetwork();
		
		run.SNA_ModuleNetwork();
//		run.SNA_CommitersNetwork();
		
	} 	
	
		
	
	
	
		
	
	

	private void createModuleNetwork() {	
		create.removeDataOnNetworkDatabase( database.getModuleNetworkDatabasePath() );
		create.createIndexesOnNetworkDatabase( database.getModuleNetworkDatabasePath(), "Repository", "repository" );
		create.executeQueryNetwork( database.getModuleNetworkDatabasePath(), Variables.ModuleNetwork ); // includes createModuleNetworkDatabase() method	
	}
	private void createCommitersNetwork() {		
		create.removeDataOnNetworkDatabase( database.getCommitersNetworkDatabasePath() );
		create.createIndexesOnNetworkDatabase( database.getCommitersNetworkDatabasePath(), "User", "user" );
		create.executeQueryNetwork( database.getCommitersNetworkDatabasePath(), Variables.CommitersNetwork ); // includes createModuleNetworkDatabase() method		
	}		
	private void SNA_ModuleNetwork() {		
		String NodeLabel = "Repository";
		String NodeProperty = "repository";
		analyser.networkAnalyse( database.getModuleNetworkDatabasePath(), NodeLabel, NodeProperty );		
	}	
	private void SNA_CommitersNetwork() {		
		String NodeLabel = "User";
		String NodeProperty = "username";
		analyser.networkAnalyse( database.getCommitersNetworkDatabasePath(), NodeLabel, NodeProperty );		
	}		

}
