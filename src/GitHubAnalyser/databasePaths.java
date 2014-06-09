package GitHubAnalyser;

public class databasePaths {
	
	//	The Path for Neo4j GitHub Database
	private static final String Neo4j_GitHub_DBPath = "C:/Users/Alpi/Documents/Neo4j/eclipse";
	// SHOULD BE THE SAME
	//	The Path for Neo4j Existed Database
	private static final String Neo4j_DBPath = "C:/Users/Alpi/Documents/Neo4j/analyserTest";
	
	//	The Path for Neo4j for Module Network Database
	private static final String Neo4j_DBPath_ModuleNetwork = "C:/Users/Alpi/Documents/Neo4j/analyserTestModuleNetwork";
	
	//	The Path for Neo4j for Commiters Network Database
	private static final String Neo4j_DBPath_CommitersNetwork = "C:/Users/Alpi/Documents/Neo4j/analyserTestCommitersNetwork";
	
	
	
	protected String getGitHubDatabasePath() {
		return Neo4j_GitHub_DBPath;	}	
	protected String getMainDatabasePath() {
		return Neo4j_DBPath;	}	
	protected String getModuleNetworkDatabasePath() {
		return Neo4j_DBPath_ModuleNetwork;	}	
	protected String getCommitersNetworkDatabasePath() {
		return Neo4j_DBPath_CommitersNetwork;	}
	
	
	
	/**
	 * Method to get String value for the Database that is used
	 * @param neo4jDbpath String value of the path to the Database. Example: "C:/Users/User/Documents/Neo4j/eclipse";
	 * @return A String Value ("Module" or "Commiters" or "GitHub" or "Unknow")
	 */
	protected String getDisplayDatabaseName(String neo4jDbpath) {
		String displayNetworkName = "";
		if ( neo4jDbpath == Neo4j_DBPath_ModuleNetwork ){
			displayNetworkName = "Module";
		} else if ( neo4jDbpath == Neo4j_DBPath_CommitersNetwork ) {
			displayNetworkName = "Commiters";
		} else if ( neo4jDbpath == Neo4j_DBPath ) {
			displayNetworkName = "(GitHub)";
		} else {
			displayNetworkName = "Unknown";
		}
		return displayNetworkName;
	}
}
