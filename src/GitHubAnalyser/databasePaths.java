package GitHubAnalyser;

public class databasePaths {
	
	//	The Path for Neo4j (Main GitHub, Module Network & Commiters Network) Databases
	private static final String Neo4j_GitHub_DBPath = "C:/Users/Alpi/Documents/Neo4j/GraphDatabase-GitHub";	
	private static final String Neo4j_DBPath_ModuleNetwork = "C:/Users/Alpi/Documents/Neo4j/GraphDatabase-ModuleNetwork";	
	private static final String Neo4j_DBPath_CommitersNetwork = "C:/Users/Alpi/Documents/Neo4j/GraphDatabase-CommitersNetwork";	
	
	
	protected String getGitHubDatabasePath() {
		return Neo4j_GitHub_DBPath;	}	
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
			displayNetworkName = "Module Network";
		} else if ( neo4jDbpath == Neo4j_DBPath_CommitersNetwork ) {
			displayNetworkName = "Commiters Network";
		} else if ( neo4jDbpath == Neo4j_GitHub_DBPath ) {
			displayNetworkName = "Main GitHub";
		} else {
			displayNetworkName = "Unknown";
		}
		return displayNetworkName;
	}
}
