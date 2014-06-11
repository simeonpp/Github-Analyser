package backUpTREE;

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
	
	
	/**
	 * Method to Analyse the passed Database
	 * @param neo4jDbpath String value for the Database Path. Example: "C:/Users/User/Documents/Neo4j/eclipse"
	 * @param nodeLabel String value. Label which will be searched by the query. Example: "User"
	 * @param nodeProperty String value. Example: 'username'
	 */
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
						
		
		ArrayList<Integer> vertexDegree = degreeOfAVertex( db, repositoryNames, nodeLabel, nodeProperty );
		ArrayList<Integer> weightedDegree = weightedDegreeOfAVertex( db, repositoryNames, nodeLabel, nodeProperty );
		ArrayList<Integer> clusteringCoefficient = clusteringCoefficientOfAVertex( db, repositoryNames, nodeLabel, nodeProperty );
		ArrayList<Integer> weightedClusteringCoefficient = weightedClusteringCoefficientOfAVertex( db, repositoryNames, nodeLabel, nodeProperty );
		ArrayList<Integer> distanceCentrality = distanceCentralityOfAVertex( db, repositoryNames, nodeLabel, nodeProperty );
		ArrayList<Integer> betweennessCentrality = betweennessCentralityOfAVertex( db, repositoryNames, nodeLabel, nodeProperty );
		
			
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






	




	/**
	 * Method to execute a query on the passed Database in order to retrieve Degree Of A Vertex  
	 * @param db GraphDatabaseService
	 * @param repositoryNames Strings ArrayList. The name of the repository
	 * @param nodeLabel String value. Label which will be searched by the query. Example: "User"
	 * @param nodeProperty String value. Example: 'username'
	 * @return the results of the query in an ArrayList of Strings
	 */
	private ArrayList<Integer> degreeOfAVertex(GraphDatabaseService db, ArrayList<String> repositoryNames, String nodeLabel, String nodeProperty) {
		 
		ArrayList<Integer> vertexDegree = new ArrayList<Integer>();		
		
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
			vertexDegree.add(Variables.convertStringToInteger(rows));
		}						
		return vertexDegree;		
	}
	
		
	
	/**
	 * Method to execute a query on the passed Database in order to retrieve Weighted Degree Of A Vertex 
	 * @param db GraphDatabaseService
	 * @param repositoryNames Strings ArrayList. The name of the repository
	 * @param nodeLabel String value. Label which will be searched by the query. Example: "User"
	 * @param nodeProperty String value. Example: 'username'
	 * @return the results of the query in an ArrayList of Strings
	 */
	private ArrayList<Integer> weightedDegreeOfAVertex(GraphDatabaseService db, ArrayList<String> repositoryNames, String nodeLabel,	String nodeProperty) {

		ArrayList<Integer> weightedDegree = new ArrayList<Integer>();
		
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
			weightedDegree.add(Variables.convertStringToInteger(rows));
		}				
		return weightedDegree;	}	
	
	
	
	/**
	 * Method to execute a query on the passed Database in order to retrieve Clustering Coefficient Of A Vertex 
	 * @param db GraphDatabaseService
	 * @param repositoryNames Strings ArrayList. The name of the repository
	 * @param nodeLabel String value. Label which will be searched by the query. Example: "User"
	 * @param nodeProperty String value. Example: 'username'
	 * @return the results of the query in an ArrayList of Strings
	 */
	private ArrayList<Integer> clusteringCoefficientOfAVertex(GraphDatabaseService db, ArrayList<String> repositoryNames, String nodeLabel,	String nodeProperty) {
		
		ArrayList<Integer> clusteringCoefficient = new ArrayList<Integer>(Arrays.asList( 0, 0, 0, 0, 0 ) );
		
//		int E = 0, K = 0, C;
//		C = E / K * (K - 1);
		
		return clusteringCoefficient;
	}
	
	
	
	/**
	 * Method to execute a query on the passed Database in order to retrieve Weighted Clustering Coefficient Of A Vertex 
	 * @param db GraphDatabaseService
	 * @param repositoryNames Strings ArrayList. The name of the repository
	 * @param nodeLabel String value. Label which will be searched by the query. Example: "User"
	 * @param nodeProperty String value. Example: 'username'
	 * @return the results of the query in an ArrayList of Strings
	 */
	private ArrayList<Integer> weightedClusteringCoefficientOfAVertex(GraphDatabaseService db, ArrayList<String> repositoryNames, String nodeLabel,	String nodeProperty) {
		
		ArrayList<Integer> weightedClusteringCoefficient = new ArrayList<Integer>(Arrays.asList( 0, 0, 0, 0, 0 ) );
		
		return weightedClusteringCoefficient;
	}
		
	
	
	/**
	 * Method to execute a query on the passed Database in order to retrieve Distance Centrality Of A Vertex 
	 * @param db GraphDatabaseService
	 * @param repositoryNames Strings ArrayList. The name of the repository
	 * @param nodeLabel String value. Label which will be searched by the query. Example: "User"
	 * @param nodeProperty String value. Example: 'username'
	 * @return the results of the query in an ArrayList of Strings
	 */
	private ArrayList<Integer> distanceCentralityOfAVertex(GraphDatabaseService db, ArrayList<String> repositoryNames, String nodeLabel,	String nodeProperty) {
		
		ArrayList<Integer> distanceCentrality = new ArrayList<Integer>(Arrays.asList( 0, 0, 0, 0, 0 ) );
		
		return distanceCentrality;
	}

	
	
	/**
	 * Method to execute a query on the passed Database in order to retrieve Betweenness Centrality Of A Vertex 
	 * @param db GraphDatabaseService
	 * @param repositoryNames Strings ArrayList. The name of the repository
	 * @param nodeLabel String value. Label which will be searched by the query. Example: "User"
	 * @param nodeProperty String value. Example: 'username'
	 * @return the results of the query in an ArrayList of Strings
	 */
	private ArrayList<Integer> betweennessCentralityOfAVertex(GraphDatabaseService db, ArrayList<String> repositoryNames, String nodeLabel,	String nodeProperty) {
		
		ArrayList<Integer> betweennessCentrality = new ArrayList<Integer>(Arrays.asList( 0, 0, 0, 0, 0 ) );
		
		return betweennessCentrality;
	}
	
	
	
	
	
	
	/**
	 * Method to print the results of two ArrayLists
	 * @param repositoryNames ArrayList of Strings 
	 * @param parameter ArrayList of Strings
	 */
	private void printParameters(ArrayList<String> repositoryNames, ArrayList<Integer> parameter) {
		for (int i = 0; i < repositoryNames.size(); i++ ){
			if(i == 0){
				System.out.printf("%s => %d", repositoryNames.get(i), parameter.get(i) );
			} else {
				System.out.printf("   ||   %s => %d", repositoryNames.get(i), parameter.get(i) );
			}
		}		
	}
	
}
