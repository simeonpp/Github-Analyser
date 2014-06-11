package GitHubAnalyser;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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

public class neo4jAPI {
	
    private Node gitHubNode = null;
    private Node repoNode = null;
    private Node repoUser = null;
	
	//connection
    GraphDatabaseService db;    
    
    protected void setDatabasePath( String path ) {
    	this.db = new GraphDatabaseFactory().newEmbeddedDatabase( path );     }
    protected Node getGitHubNode() {
    	return this.gitHubNode;   }
    protected Node getRepoNode() {
    	return this.repoNode;    }
    
        
	protected void IndexesCreation(String index1, String index1On, String index2, String index2On, String index3, String index3On) {

		// notifications
	 	System.out.println( "-------------------\nStarting database...\n-------------------\n" );
	 		
		// createIndexes
		IndexDefinition indexDefinition; 
		try ( Transaction tx = db.beginTx() )
		{
		    Schema schema = db.schema();
		    indexDefinition = schema.indexFor( DynamicLabel.label( index1) ) // GitHub Index
		            .on( index1On )
		            .create();
		    indexDefinition = schema.indexFor( DynamicLabel.label( index2 ) ) // User Index
		            .on( index2On )
		            .create();
		    indexDefinition = schema.indexFor( DynamicLabel.label( index3 ) ) // Repository Index
		            .on( index3On )
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
	}
	
	

	protected void createGitHubNode() {
			ExecutionEngine engineGit = new ExecutionEngine( db );
    		
    		ResourceIterator<Node> resultGitIterator = null;    		
    		try ( Transaction ignoredGit = db.beginTx() ){
    			String queryString = "MERGE ( n:GitHub {description: {description}} ) RETURN n";
    			Map<String, Object> parameters = new HashMap<>();
    			parameters.put( "description", "Git revision control system" );
    			resultGitIterator = engineGit.execute( queryString, parameters ).columnAs( "n" );
    			gitHubNode = resultGitIterator.next();
    			ignoredGit.success();
    			
    			Variables.increaseNodeCounter(1);
    			System.out.println( "\nNode GitHub Created." ); // notification
    		}		
	}
	
	
	private static enum RelTypes implements RelationshipType
	{
		MANAGED_BY, HAS, CONTRIBUTED_BY
	}
	
	
	protected void createRepoNode(Node gitHubNode, String repositoryName) {
		ExecutionEngine engine = new ExecutionEngine( db );
		
		ResourceIterator<Node> resultIterator = null;		
		try ( Transaction ignored = db.beginTx() ){
			String queryString = "MERGE ( n:Repository {repository: {repoName}} ) RETURN n";
			Map<String, Object> parameters = new HashMap<>();
			parameters.put( "repoName", repositoryName );
			resultIterator = engine.execute( queryString, parameters ).columnAs( "n" );
			repoNode = resultIterator.next();
			ignored.success();
			
			Variables.increaseNodeCounter(1);
			
			Relationship relationMain = gitHubNode.createRelationshipTo(repoNode, RelTypes.HAS);
			relationMain.setProperty( "rel-type", "created" );
		}        
	}

	
	protected void createUserNode(Node repoNode, String repoOwner) {
		ExecutionEngine engine = new ExecutionEngine( db );
		
		ResourceIterator<Node> resultIterator = null;		
		try ( Transaction ignored = db.beginTx() ){
			String queryString = "MERGE ( n:User {username: {userName}} ) RETURN n";
			Map<String, Object> parameters = new HashMap<>();
			parameters.put( "userName", repoOwner );
			resultIterator = engine.execute( queryString, parameters ).columnAs( "n" );
			repoUser = resultIterator.next();
			ignored.success();
			
			Variables.increaseNodeCounter(1);
			
			Relationship relationOwner = repoNode.createRelationshipTo(repoUser, RelTypes.MANAGED_BY);
			relationOwner.setProperty( "rel-type", "Owned by" );
		}        
	}
	
	
	protected void createContributorNodes(Node repoNode, String contributor, Integer contributorCommits) {
		ExecutionEngine engine = new ExecutionEngine( db );
		
		ResourceIterator<Node> resultIterator = null;		
		try ( Transaction ignored = db.beginTx() ){
			String queryString = "MERGE ( n:User {username: {userName}} ) RETURN n";
			Map<String, Object> parameters = new HashMap<>();
			parameters.put( "userName", contributor );
			resultIterator = engine.execute( queryString, parameters ).columnAs( "n" );
			repoUser = resultIterator.next();
			ignored.success();
			
			Variables.increaseNodeCounter(1);
			
			Relationship relationContr = repoNode.createRelationshipTo(repoUser, RelTypes.CONTRIBUTED_BY);
			relationContr.setProperty( "commits", contributorCommits );
		}        
	}
	

	
	protected void shutDown() {
		// shutdown
 		db.shutdown();
 		System.out.println( "\n-------------------\nNoe4j database is shutdown!\n-------------------\n" );	// notification			
	}	
}
