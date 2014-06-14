package GitHubAnalyser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import org.neo4j.kernel.impl.util.FileUtils;


public class TestDatabaseCreatorAPI {	
	
	private databasePaths database = new databasePaths();	
	private neo4jAPI neo4jAPI = new neo4jAPI();
	private TestGitHubAPI gitHubAPI = new TestGitHubAPI();
	
	private String GitHubDatabasePath = database.getGitHubDatabasePath();
	
	/**
	 * This method will delete all files in the GitHub Database Path
	 * as well as 'index' and 'schema' directories 
	 */
	protected void removeData() {
		
		System.out.println("Deleting Database...");	
		
		// delete all files in Neo4j Database Path
		File file = new File( GitHubDatabasePath );        
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
			FileUtils.deleteRecursively(new File(GitHubDatabasePath + "/index"));
			FileUtils.deleteRecursively(new File(GitHubDatabasePath + "/schema"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	    
	    System.out.println("The Database was successfuly deleted.\n");	// notification
		
	}
	
	
	
	/**
	 * This method will create Indexes (GitHub, User, Contributor, Repository and ContributorRepository)
	 * into the GitHub Database 
	 */
	protected void createIndexes() {
		
		String index1 = "GitHub";
		String index1On = "github";
		String index2 = "User";
		String index2On = "username";
		String index3 = "Repository";
		String index3On = "name";
		
		neo4jAPI.setDatabasePath( GitHubDatabasePath );
		System.out.printf( "\nCreating Indexes: %s on %s, %s on %s, %s on %s...", index1, index1On, index2, index2On, index3, index3On  ); 
		neo4jAPI.IndexesCreation( index1, index1On, index2, index2On, index3, index3On );		    
        System.out.println( "Indexes Created!" ); // notification
        neo4jAPI.shutDown();		
	}
	
	
	
	
	/**
	 * This method creates Nodes and Relationships on the GitHub Database using GitHub and Neo4j APIs
	 * Level 1: Main GitHub Node
	 * Level 2: GitHub users
	 * Level 3: Users' repository(ies)
	 * Level 4: Contributors to certain repository
	 * Level 5: Contributors' repository(ies)
	 * @throws IOException 
	 */
	protected void addData() throws IOException
	{		
		neo4jAPI.setDatabasePath( GitHubDatabasePath );		
		// -------------------------------------- LEVEL 1 GitHub --------------------------------------
		System.out.println("Creating main GitHub Node...");
		neo4jAPI.createGitHubNode();
		System.out.println("Main GitHub Node successfully created.");
		
		// -------------------------------------- LEVEL 2 Repositories --------------------------------------
		System.out.println("Getting list of Repositories from GitHub API...");
		gitHubAPI.setRepositories();
		ArrayList<String> repos = gitHubAPI.getGitHubRepos();
		ArrayList<Integer> reposId = gitHubAPI.getGitHubReposId();
		for (int i = 0; i < repos.size(); i++ ) {
			
			System.out.println("Creating Repository Node: " + repos.get(i) );
			neo4jAPI.createRepoNode ( neo4jAPI.getGitHubNode(), repos.get(i), reposId.get(i) );
			
		// -------------------------------------- LEVEL 3 Owner + Contributors --------------------------------------
			String repoOwner = gitHubAPI.getRepoOwner( repos.get(i) );
			neo4jAPI.createUserNode( neo4jAPI.getRepoNode(), repoOwner );
			
			gitHubAPI.setRepoContributors( reposId.get(i), repoOwner, repos.get(i) );	// THROWS
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
}
