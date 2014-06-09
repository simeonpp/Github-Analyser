package backUp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;



public class analyser {
		
	private databasePaths database = new databasePaths();	
	
	
	
	protected void networkAnalyse(String neo4jDbpath, String nodeLabel, String nodeProperty) {
		
		String displayNetworkName = database.getDisplayDatabaseName(neo4jDbpath);
				
		System.out.println( "-------------------\nStarting Neo4j " + displayNetworkName + " Network Database...\n-------------------\n" ); // notification
		System.out.println( "Executin Query on " + displayNetworkName + " Newtork Database..." ); // notification
		GraphDatabaseService db = new GraphDatabaseFactory().newEmbeddedDatabase( neo4jDbpath );
		
		ArrayList<String> repositoryNames = new ArrayList<String>();
		String rows = "";
		
		ExecutionEngine engine = new ExecutionEngine( db );			
		ExecutionResult result;
		try ( Transaction ignored = db.beginTx() )
		{
			result = engine.execute( " MATCH (a:" + nodeLabel + ") "
								   + " RETURN a." + nodeProperty + "; " );
				
		
			for ( Map<String, Object> row : result )
			{
			    for ( Entry<String, Object> column : row.entrySet() )
			    {
			        rows += column.getValue() + ",";;
			    }
//			    rows += "\n";
			    
			}
			ignored.success();
		}
				
		String[] splitArray = rows.split(",");
		List<String> splitList = new ArrayList<String>(Arrays.asList(splitArray));
		
		for (int i = 0; i < splitList.size(); i++){
			repositoryNames.add(splitList.get(i));
		}
						
		
		ArrayList<String> vertexDegree = degreeOfAVertex( db, repositoryNames, nodeLabel, nodeProperty );
		ArrayList<String> weightedDegree = weightedDegreeOfAVertex( db, repositoryNames, nodeLabel, nodeProperty );
		ArrayList<String> clusteringCoefficient = clusteringCoefficientOfAVertex( db, repositoryNames, nodeLabel, nodeProperty );
		ArrayList<String> weightedClusteringCoefficient = weightedClusteringCoefficientOfAVertex( db, repositoryNames, nodeLabel, nodeProperty );
		ArrayList<String> distanceCentrality = distanceCentralityOfAVertex( db, repositoryNames, nodeLabel, nodeProperty );
		ArrayList<String> betweennessCentrality = betweennessCentralityOfAVertex( db, repositoryNames, nodeLabel, nodeProperty );
		
			
		// Display the results
		System.out.println( "Query successfully executed." ); 		
		System.out.println( "------------\nResults\n------------" );
		System.out.println( "Degree of " + displayNetworkName + " Vertexe(s):" ); 
		printParameters( repositoryNames, vertexDegree );
		System.out.println( "\n\nWeighted degree of " + displayNetworkName + " Vertexe(s):" ); 
		printParameters( repositoryNames, weightedDegree );
		System.out.println( "\n\nClustering coefficient of " + displayNetworkName + " Vertexe(s):" ); 
		printParameters( repositoryNames, clusteringCoefficient );
		System.out.println( "\n\nWeighted clustering coefficient of " + displayNetworkName + " Vertexe(s):" ); 
		printParameters( repositoryNames, weightedClusteringCoefficient );
		System.out.println( "\n\nDistance centrality (closeness centrality) of " + displayNetworkName + " Vertexe(s):" ); 
		printParameters( repositoryNames, distanceCentrality );
		System.out.println( "\n\nBetweenness centrality of " + displayNetworkName + " Vertexe(s):" ); 
		printParameters( repositoryNames, betweennessCentrality );
					
		
		// shutdown
		System.out.println("\n");
		System.out.println( "\nPreparing to shut down Neo4j " + displayNetworkName + " Network Database" ); // notification
 		db.shutdown();
 		System.out.println( "-------------------\nNeo4j " + displayNetworkName + " Network Database is shutdown!\n-------------------" );	// notification	
		
	}





	





	private ArrayList<String> degreeOfAVertex(GraphDatabaseService db, ArrayList<String> repositoryNames, String nodeLabel, String nodeProperty) {
		 
		ArrayList<String> vertexDegree = new ArrayList<String>();		
		
		ExecutionEngine engine = new ExecutionEngine( db );	
		
		for (int i = 0; i < repositoryNames.size(); i++ ){
			
			String rows = "";
			
			ExecutionResult result;
			try ( Transaction ignored = db.beginTx() )
			{
				result = engine.execute( " MATCH (a:" + nodeLabel + ")<-[r:EDGE]->(b:" + nodeLabel + ") "
									   + " WHERE a." + nodeProperty + " = '" + repositoryNames.get(i) + "' "
									   + " RETURN count(r); " );
					
			
				for ( Map<String, Object> row : result )
				{
				    for ( Entry<String, Object> column : row.entrySet() )
				    {
				        rows += column.getValue();
				    }
//				    rows += "\n";
				    
				}
				ignored.success();
			}			
			vertexDegree.add(rows);
		}						
		return vertexDegree;
		
	}
	
		
	
	
	private ArrayList<String> weightedDegreeOfAVertex(GraphDatabaseService db, ArrayList<String> repositoryNames, String nodeLabel,	String nodeProperty) {

		ArrayList<String> weightedDegree = new ArrayList<String>();
		
		ExecutionEngine engine = new ExecutionEngine( db );	
		
		for (int i = 0; i < repositoryNames.size(); i++ ){
			
			String rows = "";
			
			ExecutionResult result;
			try ( Transaction ignored = db.beginTx() )
			{
				result = engine.execute( " MATCH (a:" + nodeLabel + ")<-[r:EDGE]->(b:" + nodeLabel + ") "
									   + " WHERE a." + nodeProperty + " = '" + repositoryNames.get(i) + "' "
									   + " RETURN SUM(r.degree); " );
					
			
				for ( Map<String, Object> row : result )
				{
				    for ( Entry<String, Object> column : row.entrySet() )
				    {
				        rows += column.getValue();
				    }
//				    rows += "\n";
				    
				}
				ignored.success();
			}			
			weightedDegree.add(rows);
		}				
		return weightedDegree;
	}	
	
	
	
	
	private ArrayList<String> clusteringCoefficientOfAVertex(GraphDatabaseService db, ArrayList<String> repositoryNames, String nodeLabel,	String nodeProperty) {
		
		ArrayList<String> clusteringCoefficient = new ArrayList<String>(Arrays.asList( "null", "null", "null", "null", "null" ) );
		
		return clusteringCoefficient;
	}
	
	
	
	private ArrayList<String> weightedClusteringCoefficientOfAVertex(GraphDatabaseService db, ArrayList<String> repositoryNames, String nodeLabel,	String nodeProperty) {
		
		ArrayList<String> weightedClusteringCoefficient = new ArrayList<String>(Arrays.asList( "null", "null", "null", "null", "null" ) );
		
		return weightedClusteringCoefficient;
	}
	
	
	
	
	private ArrayList<String> distanceCentralityOfAVertex(GraphDatabaseService db, ArrayList<String> repositoryNames, String nodeLabel,	String nodeProperty) {
		
		ArrayList<String> distanceCentrality = new ArrayList<String>(Arrays.asList( "null", "null", "null", "null", "null" ) );
		
		return distanceCentrality;
	}

	
	
	private ArrayList<String> betweennessCentralityOfAVertex(GraphDatabaseService db, ArrayList<String> repositoryNames, String nodeLabel,	String nodeProperty) {
		
		ArrayList<String> betweennessCentrality = new ArrayList<String>(Arrays.asList( "null", "null", "null", "null", "null" ) );
		
		return betweennessCentrality;
	}
	
	
	
	
	
	
	
	private void printParameters(ArrayList<String> repositoryNames, ArrayList<String> vertexDegree) {
		for (int i = 0; i < repositoryNames.size(); i++ ){
			if(i == 0){
				System.out.printf("%s => %s", repositoryNames.get(i), vertexDegree.get(i) );
			} else {
				System.out.printf("   ||   %s => %s", repositoryNames.get(i), vertexDegree.get(i) );
			}
		}		
	}
	
}
