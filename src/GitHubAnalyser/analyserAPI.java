package GitHubAnalyser;

import java.util.ArrayList;
import java.util.Arrays;

import org.neo4j.graphdb.GraphDatabaseService;



public class analyserAPI {
		
	
	
	/**
	 * Method to execute a query on the passed Database in order to retrieve Clustering Coefficient Of A Vertex 
	 * @param db GraphDatabaseService
	 * @param repositoryNames Strings ArrayList. The name of the repository
	 * @param nodeLabel String value. Label which will be searched by the query. Example: "User"
	 * @param nodeProperty String value. Example: 'username'
	 * @return the results of the query in an ArrayList of Strings
	 */
	protected ArrayList<Integer> clusteringCoefficientOfAVertex(GraphDatabaseService db, ArrayList<String> repositoryNames, String nodeLabel,	String nodeProperty) {
		
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
	protected ArrayList<Integer> weightedClusteringCoefficientOfAVertex(GraphDatabaseService db, ArrayList<String> repositoryNames, String nodeLabel,	String nodeProperty) {
		
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
	protected ArrayList<Integer> distanceCentralityOfAVertex(GraphDatabaseService db, ArrayList<String> repositoryNames, String nodeLabel,	String nodeProperty) {
		
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
	protected ArrayList<Integer> betweennessCentralityOfAVertex(GraphDatabaseService db, ArrayList<String> repositoryNames, String nodeLabel,	String nodeProperty) {
		
		ArrayList<Integer> betweennessCentrality = new ArrayList<Integer>(Arrays.asList( 0, 0, 0, 0, 0 ) );
		
		return betweennessCentrality;
	}
	
	
	

	
}
