package GitHubAnalyser;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
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
	
	databasePaths databasePaths = new databasePaths();
	
    private Node gitHubNode = null;
    private Node repoNode = null;
    private Node repoUser = null;
    private Node repository = null;
	private Node repository2 = null;
	
	//connection
    GraphDatabaseService db;    
    
    protected void setDatabasePath( String databasePath ) {
    	String displayNetworkName = databasePaths.getDisplayDatabaseName( databasePath );
    	System.out.println( "-------------------\nStarting Neo4j " + displayNetworkName + " Network Database...\n-------------------\n" );
    	this.db = new GraphDatabaseFactory().newEmbeddedDatabase( databasePath );     
    }
    
    protected Node getGitHubNode() { return this.gitHubNode; }
    protected Node getRepoNode() { return this.repoNode; }
    protected Node getRepositoryNode() { return this.repository; }
    protected Node getRepository2Node() { return this.repository2; }
    
        
	protected void IndexesCreation(String index1, String index1On, String index2, String index2On, String index3, String index3On) {

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
	
	
	protected void IndexesCreation2(String index, String indexOn) {
		 		
		// createIndexes
		IndexDefinition indexDefinition; 
		try ( Transaction tx = db.beginTx() )
		{
		    Schema schema = db.schema();
		    indexDefinition = schema.indexFor( DynamicLabel.label( index) ) // GitHub Index
		            .on( indexOn )
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
		MANAGED_BY, HAS, CONTRIBUTED_BY, EDGE
	}
	
	
	protected void createRepoNode(Node gitHubNode, String repositoryName, Integer repositoryId) {
		ExecutionEngine engine = new ExecutionEngine( db );
		
		ResourceIterator<Node> resultIterator = null;		
		try ( Transaction ignored = db.beginTx() ){
			String queryString = "MERGE ( n:Repository {name: {repoName}, Id: {repoId} } ) RETURN n";
			Map<String, Object> parameters = new HashMap<>();
			parameters.put( "repoName", repositoryName );
			parameters.put( "repoId", repositoryId );
			resultIterator = engine.execute( queryString, parameters ).columnAs( "n" );
			repoNode = resultIterator.next();
			ignored.success();
			
			Variables.increaseNodeCounter(1);
			
			Relationship relationMain = gitHubNode.createRelationshipTo(repoNode, RelTypes.HAS);
			relationMain.setProperty( "rel-type", "creates" );
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

	

	protected String executeQuery(String query) {
		String rows = "";
		ExecutionEngine engine = new ExecutionEngine( db );		
		ExecutionResult result;
		try ( Transaction ignored = db.beginTx() )
		{
			result = engine.execute( query );			
			for ( Map<String, Object> row : result )
			{
			    for ( Entry<String, Object> column : row.entrySet() )
			    {
			        rows += column.getValue() + ",";
			    }
			    //  rows += "\n";			    
			}
			ignored.success();
		}	
		return rows;
	}	
	
	
	

	
	protected void createNetworkDatabase(String query, String name, String name2, Integer rel) {
		
		ExecutionEngine engine = new ExecutionEngine( this.db );		
		ResourceIterator<Node> resultIterator = null;		
		try ( Transaction ignored = db.beginTx() ){
			String queryString = query;
			Map<String, Object> parameters = new HashMap<>();
			parameters.put( "Name", name );
			resultIterator = engine.execute( queryString, parameters ).columnAs( "n" );
			repository = resultIterator.next();
			
			ignored.success();			
			System.out.println( "Node " + Variables.nodeCounterAnalyse + " (Repository/User) Created : " + name );
			Variables.increaseNodeCounterAnalyse(1);
		}        
//		Relationship relationMain = gitHub.createRelationshipTo(gitHubUser, RelTypes.EDGE);
//		relationMain.setProperty( "relationship-type", "Registered account" );			
		
		ResourceIterator<Node> resultIterator2 = null;		
		try ( Transaction ignored = db.beginTx() ){
			String queryString = query;
			Map<String, Object> parameters = new HashMap<>();
			parameters.put( "Name", name2 );
			resultIterator2 = engine.execute( queryString, parameters ).columnAs( "n" );
			repository2 = resultIterator2.next();				
						
			System.out.println( "Node " + Variables.nodeCounterAnalyse + " (Repository/User) Created : " + name2 );
			Variables.increaseNodeCounterAnalyse(1);		
			
			Relationship relationship = repository.createRelationshipTo( repository2, RelTypes.EDGE );
			relationship.setProperty( "degree", rel );
			
			System.out.println( "Relationship EDGE created between Node (" + name + ") and Node (" + name2 + ") with degree of relationship of " + rel );
			ignored.success();		
		}
	}
	
	
	
	
	
	
	protected void shutDown() {
		// shutdown
		String displayNetworkName = "";
		System.out.println( "\nPreparing to shut down Neo4j " + displayNetworkName + " Network Database" );
 		db.shutdown();
 		System.out.println( "-------------------\nNeo4j " + displayNetworkName + " Network Database is shutdown!\n-------------------" );
	}
}
