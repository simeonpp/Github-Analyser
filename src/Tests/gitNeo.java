package Tests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.schema.IndexDefinition;
import org.neo4j.graphdb.schema.Schema;


public class gitNeo {
	
	//	The Path of the Neo4j
	private static final String Neo4j_DBPath = "C:/Users/Alpi/Documents/Neo4j/eclipse";
	
	
	private static enum RelTypes implements RelationshipType
	{
		MANAGE, HAS
	}	
	
	

	public static void main(String[] args) {
		
		gitNeo gitNeo = new gitNeo();
//		gitNeo.createIndexes();
		gitNeo.authenticating();
		gitNeo.addUserAndRepos();
		
	}
		
	
	private void createIndexes() {
		
		System.out.println( "\n-------------------\nStarting database...\n-------------------" ); // notification
		System.out.println( "Creating Indexes..." ); // notification
		
        GraphDatabaseService db = new GraphDatabaseFactory().newEmbeddedDatabase( Neo4j_DBPath );        
        
        
		// createIndexes
		IndexDefinition indexDefinition; 
		try ( Transaction tx = db.beginTx() )
		{
		    Schema schema = db.schema();
		    indexDefinition = schema.indexFor( DynamicLabel.label( "User" ) ) // User Index
		            .on( "username" )
		            .create();
		    indexDefinition = schema.indexFor( DynamicLabel.label( "Repository" ) ) // Repository Index
		            .on( "repository" )
		            .create();
		    indexDefinition = schema.indexFor( DynamicLabel.label( "GitHub" ) ) // GitHub Index
		            .on( "github" )
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
 		System.out.println( "-------------------\nNoe4j database is shutdown!\n-------------------\n" );	// notification	
		
	}  // ***END*** createIndexes method 

	
	
	
	
	private void authenticating() {
		
		// Authenticating in order to avoid API rate limit (5000 requests per hour)
		//OAuth2 token authentication
		GitHubClient client = new GitHubClient();
		client.setOAuth2Token("094b156b9be686a7863f256a58e59740af958a85");
		
	} // ***END*** authenticating method 
	
	

	void addUserAndRepos()
	{		
		        
		System.out.println( "\n-------------------\nStarting database...\n-------------------" ); // notification
		
		// addData and relationships
        GraphDatabaseService db = new GraphDatabaseFactory().newEmbeddedDatabase( Neo4j_DBPath );
        
        // add Users, Repositories and Relationships
        try ( Transaction tx = db.beginTx() )
        {
        	Label labelGitHub = DynamicLabel.label( "GitHub" );
            Label labelUsers = DynamicLabel.label( "User" );
            Label labelRepos = DynamicLabel.label( "Repository" );
            
            int nodeCounter = 0; // initialize counter to check nodes number 
            
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
    			
    			System.out.println( "\nNode GitHub Created." ); // notification
    		}
    		
    		
    		System.out.println( "\nCreating Nodes for Users and Repositories... " ); // notification

            // Create GitHub Users Nodes
            ArrayList<String> userNames = new ArrayList<String>(Arrays.asList( "lovell", "divegeek", "richardsnee", "FortAwesome", "whhglyphs", "stephenhutchings", "defunkt" ) );
            
            for(int i = 0; i < userNames.size(); i++) 
            {
            	if (nodeCounter < 50000){
            		
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
            			
            			System.out.println( "\nNode " + nodeCounter + " (User) Created : " + userNames.get(i) ); // notification
                        nodeCounter++;
            		}
                    
            		Relationship relationMain = gitHub.createRelationshipTo(gitHubUser, RelTypes.HAS);
            		relationMain.setProperty( "relationship-type", "Registered account" );
                    
                    
                    
                          
    	            // GitHub user's repository ArrayList
                    ArrayList<String> userRepo = new ArrayList<String>();  // (Arrays.asList("TTT", "WWW", "RRR"));
                    
                    RepositoryService service = new RepositoryService();
                    try {
    					for (Repository repo : service.getRepositories(userNames.get(i)) )
    						userRepo.add(repo.getName() );
    				} catch (IOException e) {
    					// TODO Auto-generated catch block
    					e.printStackTrace();
    				}
                    
                    	            
    	            // Create Repositories Nodes and Relationship(s)
    	            for(int j = 0; j < userRepo.size(); j++)
    	            {
    	            	if (nodeCounter < 50000){  // check if node limit is reached
    	            		
    	            		
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
    	            			
    	            			System.out.println( "Node " + nodeCounter + " (Repository) Created: " + userRepo.get(j) );  // notification
    	                        nodeCounter++;
    	            		}
    	            		
    	            		Relationship relation = gitHubUser.createRelationshipTo(gitHubRepo, RelTypes.MANAGE);
	                        relation.setProperty( "relationship-type", "Works on" );                 
    	            		
    	            	}
    	            	else {
    	            		System.out.println("Maximum Node limit reached! Repository " + userRepo.get(j) + " for user " + userNames.get(i) + " was not created." ); // notification
    	            	} // ***End if-ELSE loop (nodeCounter)***     	
    	            }   // ***End for loop (j)***          
    	         	
            	} // ***End if-ELSE loop (nodeCounter)*** 
            	else {
            		System.out.println("Maximum Node limit reached! Username " + userNames.get(i) + " and his repositories were not created." ); // notification
            	} // ***End IF loop (nodeCounter)***
 
            }  // ***End for loop (i)*** 
            
            System.out.println( "\n---Creation Finished!---" ); // notification
            tx.success();
         // ***End*** add Users, Repositories and Relationships
        } // ***End*** try 
        
		       
		// shutdown
		db.shutdown();
		System.out.println( "\n-------------------\nNoe4j database is shutdown!\n-------------------\n" );	// notification		
			
	} // ***END*** addUserAndRepos method 

}
