package Tests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.schema.IndexDefinition;
import org.neo4j.graphdb.schema.Schema;


public class gitNeoBackUp {
	
	//	The Path of the Neo4j
	private static final String Neo4j_DBPath = "C:/Users/Alpi/Documents/Neo4j/eclipse";
	
	
	private static enum RelTypes implements RelationshipType
	{
		MANAGE
	}	
	
	

	public static void main(String[] args) {
		
		gitNeoBackUp gitNeo = new gitNeoBackUp();
		gitNeo.run();
		
	}
		
	
	void run()
	{		
		System.out.println( "\n-------------------\nStarting database ...\n-------------------" );
		
		// addData and relationships
        GraphDatabaseService db = new GraphDatabaseFactory().newEmbeddedDatabase( Neo4j_DBPath );        
        
        
		// createIndexes
		IndexDefinition indexDefinition; 
		try ( Transaction tx = db.beginTx() ) // User Index
		{
		    Schema schema = db.schema();
		    indexDefinition = schema.indexFor( DynamicLabel.label( "User" ) )
		            .on( "username" )
		            .create();
		    tx.success();
		}
		
		
		try ( Transaction tx = db.beginTx() ) // Repository Index
		{
		    Schema schema = db.schema();
		    indexDefinition = schema.indexFor( DynamicLabel.label( "Repository" ) )
		            .on( "repository" )
		            .create();
		    tx.success();
		}
		
		// **END*** createIndex
		
		
		//  wait
        try ( Transaction tx = db.beginTx() )
        {
            Schema schema = db.schema();
            schema.awaitIndexOnline( indexDefinition, 10, TimeUnit.SECONDS );
        }
        // ***END*** wait	
		

        
        // add Users, Repositories and Relationships
        try ( Transaction tx = db.beginTx() )
        {
            Label labelUsers = DynamicLabel.label( "User" );
            Label labelRepos = DynamicLabel.label( "Repository" );
            
            int nodeCounter = 0; // initialize counter to check nodes number 

            // Create GitHub Users Nodes
            ArrayList<String> userNames = new ArrayList<String>(Arrays.asList( "simeonpp", "lovell", "mperham", "defunkt" ) );
            
            for(int i = 0; i < userNames.size(); i++) 
            {
            	if (nodeCounter < 50){
            		
            		Node gitHubUser = db.createNode( labelUsers );
                    gitHubUser.setProperty( "username", userNames.get(i) );
                    
                    System.out.println( "\nCreated Node " + nodeCounter + " (User): " + userNames.get(i) ); // notification
                    nodeCounter++;
                          
    	            // gitHub user's repository ArrayList
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
    	            	if (nodeCounter < 50){  // check if node limit is reached
    	            		
    	            		Node gitHubRepo = db.createNode( labelRepos );
        	            	gitHubRepo.setProperty( "repository", userRepo.get(j) );
        	            	
        					Relationship relation = gitHubUser.createRelationshipTo(gitHubRepo, RelTypes.MANAGE);
        	            	relation.setProperty( "relationship-type", "Works on" );
        	            	
        	            	System.out.println( "Created Node " + nodeCounter + " (Repository): " + userRepo.get(j) );  // notification
        	            	nodeCounter++;
    	            		
    	            	}
    	            	else {
    	            		System.out.println("Maximum Node limit reached! Repository " + userRepo.get(j) + " was not created." ); // notification
    	            	} // ***End if-ELSE loop (nodeCounter)***     	
    	            }   // ***End for loop (j)***          
    	         	
            	} // ***End if-ELSE loop (nodeCounter)*** 
            	else {
            		System.out.println("Maximum Node limit reached! Username " + userNames.get(i) + " and his repositories were not created." ); // notification
            	} // ***End IF loop (nodeCounter)***
 
            }  // ***End for loop (i)*** 
            
            System.out.println( "\n---Creation Finished!---" ); // notification
            tx.success();
        }
        // ***END*** add Users, Repositories and Relationships
		
       
		// shutdown
		db.shutdown();
		System.out.println( "\n-------------------\nNoe4j database is shutdown!\n-------------------\n" );	// notification		
			
	}

}
