package backUp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
import org.neo4j.kernel.impl.util.FileUtils;


	public class createNetworkDatabase {
		
		
	private static databasePaths database = new databasePaths();
	
	private static enum RelTypes implements RelationshipType
	{
		EDGE
	}	
	
	
	/**
	 * This method will delete all files in the passed Database Path 
	 * as well as 'index' and 'schema' directories 
	 * @param neo4jDbpath String value for the Database Path. Example: "C:/Users/User/Documents/Neo4j/eclipse"
	 */
	protected void removeDataOnNetworkDatabase(String neo4jDbpath) {
		
		String displayNetworkName = database.getDisplayDatabaseName( neo4jDbpath );	
		
		System.out.println("\nDeleting data in " + displayNetworkName + " Network Database...");	
		
		// delete all files in Neo4j Database Path
		File file = new File( neo4jDbpath );        
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
			FileUtils.deleteRecursively(new File( neo4jDbpath + "/index"));
			FileUtils.deleteRecursively(new File( neo4jDbpath + "/schema"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	    
	    System.out.println("The data in " + displayNetworkName + " Network Database was successfully deleted.\n");	// notification
		
	}
		
			
	
	/**
	 * This method will create passed Indexes into the passed Database
	 * @param neo4jDbpath String value for the Database Path. Example: "C:/Users/User/Documents/Neo4j/eclipse"
	 * @param nodeLabel String value. Label to be created as Index. Example: "User"
	 * @param nodeOn String value. Example: "user"
	 */
	protected void createIndexesOnNetworkDatabase(String neo4jDbpath, String nodeLabel, String nodeOn) {
		
		String displayNetworkName = database.getDisplayDatabaseName(neo4jDbpath);
		
		System.out.println( "-------------------\nStarting " + displayNetworkName + " Module Database...\n-------------------\n" ); // notification
		System.out.println( "Creating Indexes in " + displayNetworkName + " Module Database..." ); // notification
		
        GraphDatabaseService db_ModuleNetwork = new GraphDatabaseFactory().newEmbeddedDatabase( neo4jDbpath );        
        
        
		// createIndexes
		IndexDefinition indexDefinition; 
		try ( Transaction tx = db_ModuleNetwork.beginTx() )
		{
		    Schema schema = db_ModuleNetwork.schema();
		    indexDefinition = schema.indexFor( DynamicLabel.label( nodeLabel ) ) // GitHub Index
		            .on( nodeOn )
		            .create();
		    tx.success();
		}		
		// **End*** createIndex
		
		
		//  wait
        try ( Transaction tx = db_ModuleNetwork.beginTx() )
        {
            Schema schema = db_ModuleNetwork.schema();
            schema.awaitIndexOnline( indexDefinition, 10, TimeUnit.SECONDS );
        }
        // ***End*** wait	
        
        System.out.println( "Index(es) " + nodeLabel + " Created!" ); // notification
        
    	// shutdown
        db_ModuleNetwork.shutdown();
 		System.out.println( "\n-------------------\nNoe4j " + displayNetworkName + " Network Database is shutdown!\n-------------------\n" );	// notification	
		
	}
	
	
	/*
	 * Method for creating Module Network
	 * based on already created database 		
	 * 											*****************************************************************
	 */
	protected void executeQueryNetwork(String neo4jDbpath, int networkIdentifier) {
		
		String rows = "";		
		
		System.out.println( "-------------------\nStarting Neo4j (GitHub) Database...\n-------------------\n" ); // notification
		System.out.println( "Executin Query on the database to retrive records..." ); // notification
		GraphDatabaseService db = new GraphDatabaseFactory().newEmbeddedDatabase( database.getMainDatabasePath() );		
		
		ExecutionEngine engine = new ExecutionEngine( db );		
		ExecutionResult result;
		try ( Transaction ignored = db.beginTx() )
		{
			result = engine.execute( getNetworkQuery( networkIdentifier ) );
			
			
			for ( Map<String, Object> row : result )
			{
			    for ( Entry<String, Object> column : row.entrySet() )
			    {
			        rows += column.getValue() + ",";
			    }
//			    rows += "\n";
			    
			}
			ignored.success();
		}
			
			System.out.println( "Query successfully executed." ); // notification
			
			System.out.println( "Saving the results..." ); // notification
			String[] splitArray = rows.split(",");
			List<String> splitList = new ArrayList<String>(Arrays.asList(splitArray));
			
			ArrayList<String> Name1 = new ArrayList<String>();
			ArrayList<String> RelString = new ArrayList<String>();
			ArrayList<String> Name2 = new ArrayList<String>();
			for (int i = 0; i < splitList.size(); i++){
				Name1.add(splitList.get(i));
				i += 2;
			}
			for (int i = 1; i < splitList.size(); i++){
				if ( networkIdentifier == Variables.ModuleNetwork ) {
					RelString.add(splitList.get(i));
				} else if ( networkIdentifier == Variables.CommitersNetwork ) {
					Name2.add(splitList.get(i));
				} else { }
				i += 2;
			}
			for (int i = 2; i < splitList.size(); i++){
				if ( networkIdentifier == Variables.ModuleNetwork ) {
					Name2.add(splitList.get(i));
				} else if ( networkIdentifier == Variables.CommitersNetwork ) {
					RelString.add(splitList.get(i));
				} else { }
				i += 2;
			}
						
			ArrayList <Integer> Rel = new ArrayList <Integer>();
			Rel = convertStringListToIntegerList(RelString);
			System.out.println( "Results successfully saved." ); // notification				
			
			// shutdown
			System.out.println( "\nPreparing to shut down Neo4j (GitHub) Database" ); // notification
	 		db.shutdown();
	 		System.out.println( "-------------------\nNeo4j (GitHub) Database is shutdown!\n-------------------\n" );	// notification	
			createNetworkDatabase(Name1, Rel, Name2, neo4jDbpath, networkIdentifier);
		
	}  
		



	private static void createNetworkDatabase(ArrayList<String> Name1, ArrayList<Integer> Rel, ArrayList<String> Name2, String neo4jDbpath, int networkIdentifier) {
		
		String displayNetworkName = database.getDisplayDatabaseName( neo4jDbpath );
		
		System.out.println( "Nodes and relationships which will be created:" ); // notification
		for (int i = 0; i < Name1.size(); i++){
			System.out.println("(" + Name1.get(i) + ")-[r:Degree: " + Rel.get(i) + "]->(" + Name2.get(i) + ")");
		}
				
		System.out.println( "-------------------\nStarting Neo4j " + displayNetworkName + " Network Database...\n-------------------\n" ); // notification
		GraphDatabaseService db_ModuleNetwork = new GraphDatabaseFactory().newEmbeddedDatabase( neo4jDbpath );
		 
		
		for (int i = 0; i < Name1.size(); i++){
			
			// create User - Execution Engine to achieve MERGE
			ExecutionEngine engine = new ExecutionEngine( db_ModuleNetwork );
			
			Node repository = null;
			ResourceIterator<Node> resultIterator = null;
			
			try ( Transaction ignored = db_ModuleNetwork.beginTx() ){
				String queryString = getNetworkQuery2( networkIdentifier );
				Map<String, Object> parameters = new HashMap<>();
				parameters.put( "Name", Name1.get(i) );
				resultIterator = engine.execute( queryString, parameters ).columnAs( "n" );
				repository = resultIterator.next();
				
				ignored.success();			
				System.out.println( "Node " + Variables.nodeCounterAnalyse + " (Repository/User) Created : " + Name1.get(i)); // notification
				Variables.increaseNodeCounterAnalyse(1);
			}
	        
//			Relationship relationMain = gitHub.createRelationshipTo(gitHubUser, RelTypes.EDGE);
//			relationMain.setProperty( "relationship-type", "Registered account" );			
			
			
			Node repository2 = null;
			ResourceIterator<Node> resultIterator2 = null;
			
			try ( Transaction ignored = db_ModuleNetwork.beginTx() ){
				String queryString = getNetworkQuery2( networkIdentifier );
				Map<String, Object> parameters = new HashMap<>();
				parameters.put( "Name", Name2.get(i) );
				resultIterator2 = engine.execute( queryString, parameters ).columnAs( "n" );
				repository2 = resultIterator2.next();				
							
				System.out.println( "Node " + Variables.nodeCounterAnalyse + " (Repository/User) Created : " + Name2.get(i)); // notification
				Variables.increaseNodeCounterAnalyse(1);
			
				
				Relationship relationship = repository.createRelationshipTo( repository2, RelTypes.EDGE );
				relationship.setProperty( "degree", Rel.get(i) );
				
				System.out.println( "Relationship EDGE created between Node (" + Name1.get(i) + ") and Node (" + Name2.get(i) + ") with degree of relationship of " + Rel.get(i)); // notification
				
				ignored.success();
			}			
		} // ***End for loop (i)*** 
		
		// shutdown
					System.out.println( "\nPreparing to shut down Neo4j " + displayNetworkName + " Network Database" ); // notification
					db_ModuleNetwork.shutdown();
			 		System.out.println( "-------------------\nNeo4j " + displayNetworkName + " Network Database is shutdown!\n-------------------" );	// notification	
	}
	
	
	
	
	
		
	
	
	
	
	
	
	
	private static ArrayList<Integer> convertStringListToIntegerList(ArrayList<String> listOfStrings) {
		ArrayList <Integer> intArray = new ArrayList <Integer>();
       
       for(int i = 0; i < listOfStrings.size(); i++)
       {
    	   intArray.add( Integer.parseInt(listOfStrings.get(i)) );
       }
       
       return intArray;
	}
	
	
	
	
	private static String getNetworkQuery(int networkIdentifier) {
		String query = "";
		
		if ( networkIdentifier == Variables.ModuleNetwork ) {
			query = "MATCH (a:Repository)-[r:CONTRIBUTED]->(b:Contributor), (c:Contributor)<-[r2:CONTRIBUTED]-(d:Repository) "
					+ "WHERE a.repository < d.repository AND "
					 + " b.username = c.username "
				+ "RETURN DISTINCT a.repository, SUM(r.commits+r2.commits) as SUM, d.repository; ";
			
		} else if ( networkIdentifier == Variables.CommitersNetwork ){
			query = "MATCH (a:Contributor)<-[r:CONTRIBUTED]-(b:Repository), (d:Repository)-[r2:CONTRIBUTED]->(c:Contributor) "
					+ "WHERE a.username < c.username AND "
					 + " b.repository = d.repository  "
				+ "RETURN DISTINCT a.username, c.username, SUM(r.commits+r2.commits) as SUM; ";
			
		} else {
			System.out.println( "A match on Network Identifier was not found! Warning! No query will be return! " );
		}
				
		return query;		
	}
	
	
	
	private static String getNetworkQuery2(int networkIdentifier) {
		String query = "";
		
		if ( networkIdentifier == Variables.ModuleNetwork ) {
			query = "MERGE ( n:Repository {repository: {Name}} ) RETURN n";
			
		} else if ( networkIdentifier == Variables.CommitersNetwork ){
			query = "MERGE ( n:User {username: {Name}} ) RETURN n";
			
		} else {
			System.out.println( "A match on Network Identifier was not found! Warning! No query will be return! " );
		}
				
		return query;		
	}
	

}
