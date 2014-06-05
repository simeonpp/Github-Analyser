package GitHubAnalyser;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.eclipse.egit.github.core.Contributor;
import org.eclipse.egit.github.core.IRepositoryIdProvider;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.schema.IndexDefinition;
import org.neo4j.graphdb.schema.Schema;
import org.neo4j.kernel.impl.util.FileUtils;

import scala.Int;



public class databaseCreator {
	
//	The Path of the Neo4j
	private static final String Neo4j_DBPath = "C:/Users/Alpi/Documents/Neo4j/eclipse";
	
	
	private static enum RelTypes implements RelationshipType
	{
		MANAGE, HAS, CONTRIBUTED
	}	
	
	
	/* 
	 * Initialize main values
	 * required by the Application
	 */
	private int nodeCounter = 0;					// Initialize counter to check nodes number
	private int nodeLimit = 100;					// Node Limit Initialize
	private String authToken = "a22ecd1b13fd684e0b5e018abe8a73d6290ef0f5";
	
	
	/* 
	 * Main method
	 */
	public static void main(String[] args) {

		databaseCreator exe = new databaseCreator();
		
		/*
		 * Main methods for running the application
		 * Choose ONLY ONE
		 */
//		exe.coldStart();
		exe.newFreshStart();
//		exe.checkForUpdates();

	} 	// ***END*** Main method 
	
	
	
	
	private void coldStart() {
		System.out.println("Cold Start Method Execution...\n");
		createIndexes();										// create Neo4j Indexes
		addData();												// create Nodes and Relationships
	} 	// ***END*** coldStart method 
	
	
	private void newFreshStart() {
		System.out.println("New Fresh Start Method Execution...\n");	
		removeData();											// delete all database records
		createIndexes();										// create Neo4j Indexes
		addData();												// create Nodes and Relationships
	}	// ***END*** newFreshStart method 
	
	
	private void checkForUpdates() {
		System.out.println("Check For Updates Method Execution...\n");			
	}	// ***END*** checkForUpdates method
	
	
	
	
	
	
	
	/*
	 * Method for creating Indexes in Neo4j Database		*****************************************************************
	 */
	private void createIndexes() {
		
		System.out.println( "-------------------\nStarting database...\n-------------------\n" ); // notification
		System.out.println( "Creating Indexes..." ); // notification
		
        GraphDatabaseService db = new GraphDatabaseFactory().newEmbeddedDatabase( Neo4j_DBPath );        
        
        
		// createIndexes
		IndexDefinition indexDefinition; 
		try ( Transaction tx = db.beginTx() )
		{
		    Schema schema = db.schema();
		    indexDefinition = schema.indexFor( DynamicLabel.label( "GitHub" ) ) // GitHub Index
		            .on( "github" )
		            .create();
		    indexDefinition = schema.indexFor( DynamicLabel.label( "User" ) ) // User Index
		            .on( "username" )
		            .create();
		    indexDefinition = schema.indexFor( DynamicLabel.label( "Contributor" ) ) // User Index
		            .on( "contributor" )
		            .create();
		    indexDefinition = schema.indexFor( DynamicLabel.label( "Repository" ) ) // Repository Index
		            .on( "repository" )
		            .create();
		    indexDefinition = schema.indexFor( DynamicLabel.label( "ContributorRepository" ) ) // User Index
		            .on( "contributorRepository" )
		            .create();
		    tx.success();
		}		
		// **End*** createIndex
		
		
		//  wait
        try ( Transaction tx = db.beginTx() )
        {
            Schema schema = db.schema();
            schema.awaitIndexOnline( indexDefinition, 10, TimeUnit.SECONDS );
        }
        // ***End*** wait	
        
        System.out.println( "Indexes Created!" ); // notification
        
    	// shutdown
 		db.shutdown();
 		System.out.println( "\n-------------------\nNoe4j database is shutdown!\n-------------------\n" );	// notification	
		
	}	/*
	 	 *	***END*** createIndexes method 		***************************************************************** 
	 	 */
	
	
	
	/*
	 * Method for creating Nodes
	 * and Relationships amongst them
	 * in Neo4j Database		
	 * 											*****************************************************************
	 */
	void addData()
	{		
		     
//		System.out.println( authenticating(clientName) );
		System.out.println( "-------------------\nStarting database...\n-------------------" ); // notification

		
		// addData and relationships
        GraphDatabaseService db = new GraphDatabaseFactory().newEmbeddedDatabase( Neo4j_DBPath );
        
        // add Users, Repositories and Relationships
        try ( Transaction tx = db.beginTx() )
        {
//        	Label labelGitHub = DynamicLabel.label( "GitHub" );
//            Label labelUsers = DynamicLabel.label( "User" );
//            Label labelRepos = DynamicLabel.label( "Repository" );
           
            
            // -------------------------------------- LEVEL 1 --------------------------------------
            
            // Create GitHub Main Node            
            // create User - Execution Engine to achieve MERGE
    		ExecutionEngine engineGit = new ExecutionEngine( db );
    		
            Node gitHub = null;
    		ResourceIterator<Node> resultGitIterator = null;
    		
    		try ( Transaction ignoredGit = db.beginTx() ){
    			String queryString = "MERGE ( n:GitHub {description: {description}} ) RETURN n";
    			Map<String, Object> parameters = new HashMap<>();
    			parameters.put( "description", "Git revision control system" );
    			resultGitIterator = engineGit.execute( queryString, parameters ).columnAs( "n" );
    			gitHub = resultGitIterator.next();
    			ignoredGit.success();
    			
    			nodeCounter++;
    			System.out.println( "\nNode GitHub Created." ); // notification
    		}
    		
    		
    		System.out.println( "Creating Nodes for Users and Repositories... " ); // notification
    		
    		// -------------------------------------- END LEVEL 1 --------------------------------------
    		
    		// -------------------------------------- LEVEL 2 --------------------------------------
    		
            // Create GitHub Users Nodes
            ArrayList<String> userNames = new ArrayList<String>(Arrays.asList( "simeonpp", "whhglyphs" ) ); // "lovell", "divegeek", "richardsnee", "FortAwesome", "stephenhutchings", whhglyphs (bug)
            
            for(int i = 0; i < userNames.size(); i++) 
            {
            	if (nodeCounter < nodeLimit){
            		
            		// create User - Execution Engine to achieve MERGE
            		ExecutionEngine engine = new ExecutionEngine( db );
            		
            		Node gitHubUser = null;
            		ResourceIterator<Node> resultIterator = null;
            		
            		try ( Transaction ignored = db.beginTx() ){
            			String queryString = "MERGE ( n:User {username: {username}} ) RETURN n";
            			Map<String, Object> parameters = new HashMap<>();
            			parameters.put( "username", userNames.get(i) );
            			resultIterator = engine.execute( queryString, parameters ).columnAs( "n" );
            			gitHubUser = resultIterator.next();
            			ignored.success();
            			
            			System.out.println( "\nNode " + nodeCounter + " (User) Created : " + userNames.get(i) + "\n-----------------------------------"); // notification
                        nodeCounter++;
            		}
                    
            		Relationship relationMain = gitHub.createRelationshipTo(gitHubUser, RelTypes.HAS);
            		relationMain.setProperty( "relationship-type", "Registered account" );
                    
                    
            		// -------------------------------------- END LEVEL 2 --------------------------------------
            		
            		// -------------------------------------- LEVEL 3 --------------------------------------
            		
    	            // GitHub user's repository ArrayList
                    ArrayList<String> userRepo = new ArrayList<String>();  // (Arrays.asList("TTT", "WWW", "RRR"));
                    
                    RepositoryService service = new RepositoryService();
                    
                    // OAuth2 token authentication :::: Authenticating in order to avoid API rate limit (5000 requests per hour)          		
                    service.getClient().setOAuth2Token(authToken);
            		
            		
                    try {
    					for (Repository repo : service.getRepositories(userNames.get(i)) )
    						userRepo.add(repo.getName() );
    				} catch (IOException e) {
    					System.out.println( "Unable to get user's repositories " + e.getMessage() );
    					e.printStackTrace();
    				}
                    
                    	            
    	            // Create Repositories Nodes and Relationship(s)
    	            for(int j = 0; j < userRepo.size(); j++)
    	            {
    	            	if (nodeCounter < nodeLimit){  // check if node limit is reached
    	            		
    	            		
    	            		// create Repositories - Execution Engine to achieve MERGE
    	            		ExecutionEngine engineRepo = new ExecutionEngine( db );
    	            		
    	            		Node gitHubRepo = null;
    	            		ResourceIterator<Node> resultRepoIterator = null;
    	            		
    	            		try ( Transaction ignored = db.beginTx() ){
    	            			String queryString = "MERGE ( n:Repository {repository: {repository}} ) RETURN n";
    	            			Map<String, Object> parameters = new HashMap<>();
    	            			parameters.put( "repository", userRepo.get(j) );
    	            			resultRepoIterator = engineRepo.execute( queryString, parameters ).columnAs( "n" );
    	            			gitHubRepo = resultRepoIterator.next();
    	            			ignored.success();
    	            			
    	            			System.out.println( "\nNode " + nodeCounter + " (Repository) Created: " + userRepo.get(j) );  // notification
    	                        nodeCounter++;
    	            		}
    	            		
    	            		Relationship relation = gitHubUser.createRelationshipTo(gitHubRepo, RelTypes.MANAGE);
	                        relation.setProperty( "relationship-type", "Works on" );   
	                        
	                        // --------------------------------------  END LEVEL 3 --------------------------------------
	                        
	                        // -------------------------------------- LEVEL 4 --------------------------------------
	                        
	                        // Create GitHub Contributors Users Nodes
	                        ArrayList<String> contrUserNames = new ArrayList<String>();
	                        ArrayList<Integer> contrUserCommits = new ArrayList<Integer>();
	                        
	                        // initialize stings for getting repository's contributors
	                        String repoId = "1336779"; 						
	                		String gitHubUsername = userNames.get(i);				
	                		String usernameRepo = userRepo.get(j);
	                		
	                		
	                		// GitHub API get repository's contributors
	                		try {
								for ( Contributor repo : service.getContributors(RepositoryId(repoId, gitHubUsername, usernameRepo), true))
								{
									if ( repo.getLogin() != null ) {					// check if the contributor is null (very unlikely to happen, but just in case)		
										contrUserNames.add( repo.getLogin() );
										contrUserCommits.add( repo.getContributions() );
									} else { }
								}
							} catch (IOException e1) {
								System.out.println( "Unable to get contributors " + e1.getMessage() );
								e1.printStackTrace();
							}
	                		
	                		
//	                		contrUserNames.remove(gitHubUsername);							 // remove repository's owner
    	            		
	                		
	                		// Neo4j API
	                        for(int k = 0; k < contrUserNames.size(); k++) 
	                        {
	                        	if (nodeCounter < nodeLimit){
	                        		
	                        		// create Contributor User - Execution Engine to achieve MERGE
	                        		
	                        		Node gitHubContrUser = null;
	                        		ResourceIterator<Node> resultContrIterator = null;
	                        		
	                        		try ( Transaction ignored = db.beginTx() ){
	                        			String queryString = "MERGE ( n:Contributor {username: {username}} ) RETURN n";
	                        			Map<String, Object> parameters = new HashMap<>();
	                        			parameters.put( "username", contrUserNames.get(k) );
	                        			resultContrIterator = engine.execute( queryString, parameters ).columnAs( "n" );
	                        			gitHubContrUser = resultContrIterator.next();
	                        			ignored.success();
	                        			
	                        			System.out.println( "Node " + nodeCounter + " (Contributor) Created : " + contrUserNames.get(k) ); // notification
	                                    nodeCounter++;
	                        		}
	                                
	                        		Relationship relationContr = gitHubRepo.createRelationshipTo(gitHubContrUser, RelTypes.CONTRIBUTED);
	                        		relationContr.setProperty( "relationship-type", "Contributed by user" );
	                        		relationContr.setProperty( "commits", contrUserCommits.get(k) );
	                        		
	                        		
	                        		// -------------------------------------- END LEVEL 4 --------------------------------------
	                        		
	                        		// -------------------------------------- LEVEL 5 --------------------------------------
	                        		
	                        		// GitHub Contributor user's repository ArrayList
	                                ArrayList<String> userContrRepo = new ArrayList<String>();  // (Arrays.asList("TTT", "WWW", "RRR"));
	                                
	                                try {
	                					for (Repository repo : service.getRepositories(contrUserNames.get(k)) )
	                						userContrRepo.add(repo.getName() );
	                				} catch (IOException e) {
	                					System.out.println( "Unable to get Contributor user's repositories " + e.getMessage() );
	                					e.printStackTrace();
	                				}
	                                
	                                	            
	                	            // Create Repositories Nodes and Relationship(s)
	                	            for(int l = 0; l < userContrRepo.size(); l++)
	                	            {
	                	            	if (nodeCounter < nodeLimit){  // check if node limit is reached
	                	            			                	            		
	                	            		Node gitHubContrRepo = null;
	                	            		ResourceIterator<Node> resultContrRepoIterator = null;
	                	            		
	                	            		try ( Transaction ignored = db.beginTx() ){
	                	            			String queryString = "MERGE ( n:ContributorRepository {repository: {repository}} ) RETURN n";
	                	            			Map<String, Object> parameters = new HashMap<>();
	                	            			parameters.put( "repository", userContrRepo.get(l) );
	                	            			resultContrRepoIterator = engineRepo.execute( queryString, parameters ).columnAs( "n" );
	                	            			gitHubContrRepo = resultContrRepoIterator.next();
	                	            			ignored.success();
	                	            			
	                	            			System.out.println( "Node " + nodeCounter + " (Contributor Repository) Created: " + userContrRepo.get(l) );  // notification
	                	                        nodeCounter++;
	                	            		}
	                	            			                	            		
	                	            		Relationship relationContrRepo = gitHubContrUser.createRelationshipTo(gitHubContrRepo, RelTypes.MANAGE);
	                	            		relationContrRepo.setProperty( "relationship-type", "Works on" );
	                        		
	                        		// -------------------------------------- END LEVEL 5 --------------------------------------
	                	            	}
	                	            	else {
	                	            		System.out.println("Maximum Node limit of " + nodeLimit + " Nodes reached! Contributor Repository " + userContrRepo.get(l) + " for Contributor " + contrUserNames.get(k) + " on Repository " + userRepo.get(j) + " for user " + userNames.get(i) + " was not created." ); // notification
	                	            	}// ***End if-ELSE loop (nodeCounter) (l)***
	                	            }  // ***End for loop (l)*** 
	                	            
	                	         // -------------------------------------- END LEVEL 5 --------------------------------------
	                        	}
	                        	else {
	                        		System.out.println("Maximum Node limit of " + nodeLimit + " Nodes reached! Contributor " + contrUserNames.get(k) + " on Repository " + userRepo.get(j) + " for user " + userNames.get(i) + " was not created." ); // notification
	                        	} // ***End if-ELSE loop (nodeCounter) (k)***	                        		
	                        }   // ***End for loop (k)***  
	                        		
	                     // -------------------------------------- END LEVEL 4 --------------------------------------
    	            	}
    	            	else {
    	            		System.out.println("Maximum Node limit of " + nodeLimit + " Nodes reached! Repository " + userRepo.get(j) + " for user " + userNames.get(i) + " was not created." ); // notification
    	            	} // ***End if-ELSE loop (nodeCounter)***     	
    	            }   // ***End for loop (j)***          
    	         	
            	} // ***End if-ELSE loop (nodeCounter)*** 
            	else {
            		System.out.println("Maximum Node limit of " + nodeLimit + " Nodes reached! Username " + userNames.get(i) + " and his repositories were not created." ); // notification
            	} // ***End IF loop (nodeCounter)***
 
            }  // ***End for loop (i)*** 
            
            System.out.println( "\n---Graph Database Created Successfully!---" ); // notification
            tx.success();
         // ***End*** add Users, Repositories and Relationships
        } // ***End*** try 
        
		       
		// shutdown
		db.shutdown();
		System.out.println( "\n-------------------\nNoe4j database is shutdown!\n-------------------" );	// notification		
			
	} 
	/*
 	 *	***END*** addData method 		***************************************************************** 
 	 */
	
	
	
	/*
	 * Method for creating repository from id.  The id is split on the '/' character and the
	 * first two non-empty segments are interpreted to be the repository owner
	 * and name.	
	 * 									*****************************************************************
	 */
	private static IRepositoryIdProvider RepositoryId(String userId, String username, String repo) {
		if (userId == null || userId.length() == 0)
			return null;
		
		String owner = username;
		String name = repo;
		for (String segment : userId.split("/")) //$NON-NLS-1$
			if (segment.length() > 0)
				if (owner == null)
					owner = segment;
				else if (name == null)
					name = segment;
				else
					break;

		return owner != null && owner.length() > 0 && name != null
				&& name.length() > 0 ? new RepositoryId(owner, name) : null;
	} 
	/*
 	 *	***END*** IRepositoryIdProvider method 		***************************************************************** 
 	 */
	
	
	/*
	 * Method for deleting all data
	 * The method deletes all files in Neo4j Path 
	 * and deletes 'index' and 'schema' directories as well
	 * 									*****************************************************************
	 */
	private void removeData() {
		
		System.out.println("Deleting Database...");	
		
		// delete all files in Neo4j Database Path
		File file = new File(Neo4j_DBPath);        
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
			FileUtils.deleteRecursively(new File(Neo4j_DBPath + "/index"));
			FileUtils.deleteRecursively(new File(Neo4j_DBPath + "/schema"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	    
	    System.out.println("The Database was successfuly deleted.\n");	// notification
		
	}
	/*
 	 *	***END*** removeData method 		***************************************************************** 
 	 */

}
