package GitHubAnalyser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.neo4j.kernel.impl.util.FileUtils;


public class analyserAPI {	
	
		
	private neo4jAPI neo4jAPI = new neo4jAPI();
	private GitHubAPI gitHubAPI = new GitHubAPI();
	
	
	/**
	 * This method will delete all files in the GitHub Database Path
	 * as well as 'index' and 'schema' directories 
	 * @param databasePath 
	 */
	protected void removeData(String databasePath) {
		
		System.out.println("Deleting Database...");	
		
		// delete all files in Neo4j Database Path
		File file = new File( databasePath );        
        String[] myFiles;      
            if(file.isDirectory()){  
                myFiles = file.list();  
                for (int i=0; i<myFiles.length; i++) {  
                    File myFile = new File(file, myFiles[i]);   
                    myFile.delete();  
                }  
             } 
           
        // delete directories 'index' and 'schema' in Neo4j Database Path
	    try {
			FileUtils.deleteRecursively(new File(databasePath + "/index"));
			FileUtils.deleteRecursively(new File(databasePath + "/schema"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	    
	    System.out.println("The Database was successfuly deleted.\n");	// notification
		
	}
	
	
	
	/**
	 * This method will create passed Indexes into the passed Database
	 * @param databasePath String value for the Database Path. Example: "C:/Users/User/Documents/Neo4j/eclipse"
	 * @param IndexDefiner 
	 */
	protected void createIndexes(String databasePath, int indexDefiner) {
		
		String index1 = "GitHub";
		String index1On = "github";
		String index2 = "User";
		String index2On = "username";
		String index3 = "Repository";
		String index3On = "name";
		
		String indexModuleNetwork = "Repository";
		String indexModuleNetworkOn = "name";
		
		String indexCommitersNetwork = "User";
		String indexCommitersNetworkOn = "username";
		
		neo4jAPI.setDatabasePath( databasePath );
		if ( indexDefiner == Variables.MainDatabase ){
			System.out.printf( "\nCreating Indexes: %s on %s, %s on %s, %s on %s...", index1, index1On, index2, index2On, index3, index3On  ); 
			neo4jAPI.IndexesCreation( index1, index1On, index2, index2On, index3, index3On );		
		} else if ( indexDefiner == Variables.ModuleNetwork ) {
			System.out.printf( "\nCreating Indexes: %s on %s...", indexModuleNetwork, indexModuleNetworkOn  ); 
			neo4jAPI.IndexesCreation2( indexModuleNetwork, indexModuleNetworkOn );
		} else if ( indexDefiner == Variables.CommitersNetwork) {
			System.out.printf( "\nCreating Indexes: %s on %s...", indexCommitersNetwork, indexCommitersNetworkOn  ); 
			neo4jAPI.IndexesCreation2( indexCommitersNetwork, indexCommitersNetworkOn );
		} else { System.out.println("Sorry, the index definer is incorrect!"); }
        System.out.println( "Indexes Created!" ); // notification
        
    	// shutdown
        neo4jAPI.shutDown();		
	}
	
	
	
	
	/**
	 * This method creates Nodes and Relationships on the GitHub Database using GitHub and Neo4j APIs
	 * Level 1: Main GitHub Node
	 * Level 2: GitHub users
	 * Level 3: Users' repository(ies)
	 * Level 4: Contributors to certain repository
	 * Level 5: Contributors' repository(ies)
	 * @param databasePath 
	 * @throws IOException 
	 */
	protected void addData(String databasePath) throws IOException
	{		
		neo4jAPI.setDatabasePath( databasePath );		
		// -------------------------------------- LEVEL 1 GitHub --------------------------------------
		System.out.println("Creating main GitHub Node...");
		neo4jAPI.createGitHubNode();
		System.out.println("Main GitHub Node successfully created.");
		
		// -------------------------------------- LEVEL 2 Repositories --------------------------------------
		System.out.println("Getting list of Repositories from GitHub API...");
		gitHubAPI.setReposUserIds();
		ArrayList<String> repos = gitHubAPI.getGitHubRepos();
		ArrayList<Integer> reposId = gitHubAPI.getGitHubReposId();
		for (int i = 0; i < repos.size(); i++ ) {
			
			System.out.println("Creating Repository Node: " + repos.get(i) );
			neo4jAPI.createRepoNode ( neo4jAPI.getGitHubNode(), repos.get(i), reposId.get(i) );
			
		// -------------------------------------- LEVEL 3 Owner + Contributors --------------------------------------
			ArrayList<String> repoOwner = gitHubAPI.getGitHubReposOwner();
			neo4jAPI.createUserNode( neo4jAPI.getRepoNode(), repoOwner.get(i) );
			
			gitHubAPI.setRepoContributors( reposId.get(i), repoOwner.get(i), repos.get(i) );	// THROWS
			ArrayList<String> repoContributors = gitHubAPI.getContributors();
			ArrayList<Integer> repoContributorsCommits = gitHubAPI.getContributorsCommits();
			for (int j = 0; j < repoContributors.size(); j++){
				neo4jAPI.createContributorNodes( neo4jAPI.getRepoNode(), repoContributors.get(j), repoContributorsCommits.get(j) );
			}	
			System.out.println("Repository Node " + repos.get(i) + " and its owner and contributors were successfully created.");
		}		       
		
		// shutdown
		neo4jAPI.shutDown();			
	}


	
	/**
	 * This method execute a query on the Main Database to get all nodes and relationships 
	 * which would be created in the Network Database.
	 * After successful execution of the query the method calls 'createNetworkDatabase' method, which creates the Network Database.
	 * @param neo4jDbpath String value for the Database Path. Example: "C:/Users/User/Documents/Neo4j/eclipse"
	 * @param networkIdentifier Integer value to identify if the database is Module or Commiters Network. Example: 0 - Module Network, 1 - Commiters Network
	 * @return 
	 */
	protected List<String> executeNetworkQuery(String databasePath, int networkIdentifier) 
	{		
		System.out.println( "Executin Query on the database to retrive records..." ); 
		neo4jAPI.setDatabasePath( databasePath );
		String queryResults = neo4jAPI.executeQuery( Variables.getNetworkQuery( networkIdentifier ) );
		System.out.println( "Query successfully executed." );
		
		System.out.println( "Saving the results..." );
		String[] splitArray = queryResults.split(",");		
		List<String> splitList = new ArrayList<String>(Arrays.asList(splitArray));	
		
		// shutdown
		neo4jAPI.shutDown();
		return splitList;
	}



	protected void createNetworkDatabase(ArrayList<String> Name1, ArrayList<Integer> rel, ArrayList<String> Name2, String databasePath, int moduleNetwork) 
	{
		System.out.println( "Results successfully saved." );
		
		System.out.println( "Nodes and relationships which will be created:" );
		for (int i = 0; i < Name1.size(); i++){
			System.out.println("(" + Name1.get(i) + ")-[r:Degree: " + rel.get(i) + "]->(" + Name2.get(i) + ")");
		}
		
		neo4jAPI.setDatabasePath( databasePath );
		for (int i = 0; i < Name1.size(); i++){
			neo4jAPI.createNetworkDatabase( Variables.getNetworkQuery2( moduleNetwork ), Name1.get(i), Name2.get(i), rel.get(i) );
		} // ***End for loop (i)*** 
		
		// shutdown
		neo4jAPI.shutDown();		
	} 
}
