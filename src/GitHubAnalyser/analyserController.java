package GitHubAnalyser;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.neo4j.kernel.impl.util.FileUtils;


public class analyserController {	
	
		
	private neo4jAPI neo4jAPI = new neo4jAPI();
	private GitHubAPI gitHubAPI = new GitHubAPI();
	databasePaths databasePaths = new databasePaths();
	mySqlAPI sqlAPI = new mySqlAPI();
	
	
	/**
	 * This method will delete all files in the GitHub Database Path
	 * as well as 'index' and 'schema' directories 
	 * @param databasePath 
	 */
	protected void removeData(String databasePath) {
		
		String displayDatabaseName = databasePaths.getDisplayDatabaseName( databasePath );
		System.out.println("Deleting " + displayDatabaseName + " Database...");	
		
		// delete all files in Neo4j Database Path
		File file = new File( databasePath );        
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
			FileUtils.deleteRecursively(new File(databasePath + "/index"));
			FileUtils.deleteRecursively(new File(databasePath + "/schema"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	    
	    System.out.println( displayDatabaseName + " Database was successfuly deleted.\n");	// notification
		
	}
	
	
	
	/**
	 * This method will create passed Indexes into the passed Database
	 * @param databasePath String value for the Database Path. Example: "C:/Users/User/Documents/Neo4j/eclipse"
	 * @param IndexDefiner 
	 */
	protected void createIndexes(String databasePath, int indexDefiner) {
		
		String displayDatabaseName = databasePaths.getDisplayDatabaseName( databasePath );
		
		String index1 = "User";
		String index1On = "username";
		String index2 = "Repository";
		String index2On = "name";
		
		String indexModuleNetwork = "Repository";
		String indexModuleNetworkOn = "name";
		
		String indexCommitersNetwork = "User";
		String indexCommitersNetworkOn = "username";
		
		neo4jAPI.setDatabasePath( databasePath );
		if ( indexDefiner == Variables.MainDatabase ){
			System.out.printf( "\nCreating Indexes in %s Database: %s on %s, %s on %s...", displayDatabaseName, index1, index1On, index2, index2On ); 
			neo4jAPI.IndexesCreation( index1, index1On, index2, index2On );		
		} else if ( indexDefiner == Variables.ModuleNetwork ) {
			System.out.printf( "\nCreating Indexes in %s Database: %s on %s...", displayDatabaseName, indexModuleNetwork, indexModuleNetworkOn  ); 
			neo4jAPI.IndexesCreation2( indexModuleNetwork, indexModuleNetworkOn );
		} else if ( indexDefiner == Variables.CommitersNetwork) {
			System.out.printf( "\nCreating Indexes in %s Database: %s on %s...", displayDatabaseName, indexCommitersNetwork, indexCommitersNetworkOn  ); 
			neo4jAPI.IndexesCreation2( indexCommitersNetwork, indexCommitersNetworkOn );
		} else { System.out.println("Sorry, the index definer is incorrect!"); }
        System.out.println( "\nIndexes Created!" ); // notification
        
    	// shutdown
        neo4jAPI.shutDown();		
	}
	
	
	
	
	/**
	 * This method creates Nodes and Relationships on the GitHub Database using GitHub and Neo4j APIs
	 * Level 1: Main GitHub Node
	 * Level 2: GitHub users
	 * Level 3: Users' repository(ies)
	 * Level 4: Contributors to certain repository
	 * Level 5: Contributors' repository(ies)
	 * @param databasePath 
	 * @throws IOException 
	 */
	protected void addData(String databasePath) throws IOException
	{		
		neo4jAPI.setDatabasePath( databasePath );				
		// -------------------------------------- LEVEL 1 Repositories --------------------------------------
		System.out.println("Getting list of Repositories from GitHub API...");
		gitHubAPI.setReposUserIds();
		ArrayList<String> repos = gitHubAPI.getGitHubRepos();
		ArrayList<Integer> reposId = gitHubAPI.getGitHubReposId();
		for (int i = 0; i < repos.size(); i++ ) 
		{			
			System.out.println("Creating Repository Node: " + repos.get(i) );
			neo4jAPI.createRepoNode ( repos.get(i), reposId.get(i) );
			
		// -------------------------------------- LEVEL 2 Owner + Contributors --------------------------------------
			ArrayList<String> repoOwner = gitHubAPI.getGitHubReposOwner();
			neo4jAPI.createUserNode( neo4jAPI.getRepoNode(), repoOwner.get(i) );
			
			gitHubAPI.setRepoContributorsWatchersAndForks( reposId.get(i), repoOwner.get(i), repos.get(i) );	// THROWS
			ArrayList<String> repoContributors = gitHubAPI.getContributors();
			ArrayList<Integer> repoContributorsCommits = gitHubAPI.getContributorsCommits();
			for (int j = 0; j < repoContributors.size(); j++){
				neo4jAPI.createContributorNodes( neo4jAPI.getRepoNode(), repoContributors.get(j), repoContributorsCommits.get(j) );
			}	
			
		// -------------------------------------- LEVEL 3 Watchers + Forks --------------------------------------
			ArrayList<String> repoWatchers = gitHubAPI.getWatchers();
			for ( int e = 0; e < repoWatchers.size(); e ++ ){
				neo4jAPI.createWatcherNodes( neo4jAPI.getRepoNode(), repoWatchers.get(e) );
			}
			
			ArrayList<String> repoForks = gitHubAPI.getForks();
			for ( int y = 0; y < repoForks.size(); y ++ ){
				neo4jAPI.createForkNodes( neo4jAPI.getRepoNode(), repoForks.get(y) );
			}
			
			System.out.println("Repository Node " + repos.get(i) + ", its owner, its contributors, its watchers and its forks were successfully created.");
		}		       
		
		// shutdown
		neo4jAPI.shutDown();			
	}


	
	/**
	 * This method execute a query on the Main Database to get all nodes and relationships 
	 * which would be created in the Network Database.
	 * After successful execution of the query the method calls 'createNetworkDatabase' method, which creates the Network Database.
	 * @param neo4jDbpath String value for the Database Path. Example: "C:/Users/User/Documents/Neo4j/eclipse"
	 * @param networkIdentifier Integer value to identify if the database is Module or Commiters Network. Example: 0 - Module Network, 1 - Commiters Network
	 * @return 
	 */
	protected List<String> executeNetworkQuery(String databasePath, int networkIdentifier) 
	{		 
		neo4jAPI.setDatabasePath( databasePath );
		String queryResults = neo4jAPI.executeQuery( Variables.getNetworkQuery( networkIdentifier ), "," );
		
		System.out.println( "Saving the results..." );
		String[] splitArray = queryResults.split(",");		
		List<String> splitList = new ArrayList<String>(Arrays.asList(splitArray));	
		
		// shutdown
		neo4jAPI.shutDown();
		return splitList;
	}



	protected void createNetworkDatabase(ArrayList<String> Name1, ArrayList<Integer> rel, ArrayList<String> Name2, String databasePath, int moduleNetwork) 
	{
		System.out.println( "Results successfully saved." );
		
		System.out.println( "Nodes and relationships which will be created:" );
		for (int i = 0; i < Name1.size(); i++){
			System.out.println("(" + Name1.get(i) + ")-[r:Degree: " + rel.get(i) + "]->(" + Name2.get(i) + ")");
		}
		
		neo4jAPI.setDatabasePath( databasePath );
		for (int i = 0; i < Name1.size(); i++){
			neo4jAPI.createNetworkDatabase( Variables.getNetworkQuery2( moduleNetwork ), Name1.get(i), Name2.get(i), rel.get(i) );
		} // ***End for loop (i)*** 
		
		// shutdown
		neo4jAPI.shutDown();		
	}


	
	/**
	 * Method to Analyse the passed Database
	 * @param databasePath 
	 * @param networkIdentifier 
	 * @param arrayList 
	 * @param moduleNetwork 
	 * @param neo4jDbpath String value for the Database Path. Example: "C:/Users/User/Documents/Neo4j/eclipse"
	 * @param nodeLabel String value. Label which will be searched by the query. Example: "User"
	 * @param nodeProperty String value. Example: 'username'
	 */
	protected void networkAnalyse(ArrayList<String> repositoryNames, String databasePath, int networkIdentifier) 
	{	
		neo4jAPI.setDatabasePath( databasePath );
		
		ArrayList<Integer> vertexDegree = new ArrayList<Integer>();	
		ArrayList<Integer> weightedDegree = new ArrayList<Integer>();
		
		for (int i = 0; i < repositoryNames.size(); i++ ){
			vertexDegree.add(Variables.convertStringToInteger( neo4jAPI.executeQuery( Variables.getParametersQuery( networkIdentifier, Variables.DegreeOfAVertex, repositoryNames.get(i)  ), "" ) ));
			weightedDegree.add(Variables.convertStringToInteger( neo4jAPI.executeQuery( Variables.getParametersQuery( networkIdentifier, Variables.WeightedDegreeOfAVertex, repositoryNames.get(i)  ), "" ) ));
		}
		
		
//		ArrayList<Integer> clusteringCoefficient = clusteringCoefficientOfAVertex( db, repositoryNames, nodeLabel, nodeProperty );
//		ArrayList<Integer> weightedClusteringCoefficient = weightedClusteringCoefficientOfAVertex( db, repositoryNames, nodeLabel, nodeProperty );
//		ArrayList<Integer> distanceCentrality = distanceCentralityOfAVertex( db, repositoryNames, nodeLabel, nodeProperty );
//		ArrayList<Integer> betweennessCentrality = betweennessCentralityOfAVertex( db, repositoryNames, nodeLabel, nodeProperty );
		
			
		// Display the results		
		System.out.println( "------------\nResults\n------------" );
		String displayDatabaseName = databasePaths.getDisplayDatabaseName( databasePath );
		Variables.printParameterResults( repositoryNames, vertexDegree, displayDatabaseName, "Degree" ); 
		Variables.printParameterResults( repositoryNames, weightedDegree, displayDatabaseName, "Weighted degree" );
//		System.out.println( "\n\nClustering coefficient of " + displayNetworkName + " Vertexe(s):" ); 
//		printParameters( repositoryNames, clusteringCoefficient );
//		System.out.println( "\n\nWeighted clustering coefficient of " + displayNetworkName + " Vertexe(s):" ); 
//		printParameters( repositoryNames, weightedClusteringCoefficient );
//		System.out.println( "\n\nDistance centrality (closeness centrality) of " + displayNetworkName + " Vertexe(s):" ); 
//		printParameters( repositoryNames, distanceCentrality );
//		System.out.println( "\n\nBetweenness centrality of " + displayNetworkName + " Vertexe(s):" ); 
//		printParameters( repositoryNames, betweennessCentrality );					
	}


	protected void MySQLDeleteData() throws SQLException {
		sqlAPI.deleteAllData();
	}

	protected void MySQLInsertData() throws ClassNotFoundException, SQLException {		
		ArrayList<String> neo4jQueries = new ArrayList<String>(Arrays.asList( " MATCH (a:User) RETURN DISTINCT a.username; ",
																		      " MATCH (a:Repository) RETURN DISTINCT a.name, a.Id; ",
																		      " MATCH (a:Repository)-[r:MANAGED_BY]->(b:User) RETURN b.username, a.Id; ",
																		      " MATCH (a:Repository)-[r:CONTRIBUTED_BY]->(b:User) RETURN  b.username, a.Id, r.commits; ",
																		      " MATCH (a:Repository)-[r:FORKED_BY]->(b:User) RETURN b.username, a.Id; ",
																		      " MATCH (a:Repository)-[r:WATCHED_BY]->(b:User) RETURN b.username, a.Id; "
																			));
		ArrayList<Integer> neo4jQueriesColumnsReturn = new ArrayList<Integer>(Arrays.asList( 1, 2, 2, 3, 2, 2 ));
		ArrayList<String> sqlInsertQueries = new ArrayList<String>(Arrays.asList(  " INSERT INTO user (username) VALUES (?) ",
																				   " INSERT INTO repository (repoName,repoGHId) VALUES (?,?) ",
																				   " INSERT INTO ownership (repoID,userID) VALUES (?,?) ",
																				   " INSERT INTO contributions (repoID,userID,commits) VALUES (?,?,?) ",
																				   " INSERT INTO forks (repoID,userID) VALUES (?,?) ",
																				   " INSERT INTO watches (repoID,userID) VALUES (?,?) "
																			  ));
		ArrayList<String> sqlSearchRepoQuery = new ArrayList<String>(Arrays.asList( "repositoryId", "repository", "repoGHId" ));
		ArrayList<String> sqlSearchUserQuery = new ArrayList<String>(Arrays.asList( "userId", "user", "username" ));
		
		//connection
		sqlAPI.connection();
		neo4jAPI.setDatabasePath(databasePaths.getGitHubDatabasePath());
		
		for(int i = 0; i < neo4jQueries.size(); i++){
			String neo4jQueryResult = neo4jAPI.executeQuery( neo4jQueries.get(i), ",");
			String[] splitArray = neo4jQueryResult.split(",");		
			List<String> splitList = new ArrayList<String>(Arrays.asList(splitArray));
			
			System.out.println(splitList);
			
			if ( neo4jQueriesColumnsReturn.get(i) == 1 ) {
				for (int j = 0; j < splitList.size(); j++){
					sqlAPI.insertData( splitList.get(j), sqlInsertQueries.get(i) );
				}
			} else if ( neo4jQueriesColumnsReturn.get(i) == 2 ) {
				
				if ( i > 1 ){
					ArrayList<String> neo4jQueryResultList1 = Variables.splitResultStringInPart(splitList, 1, 1);
					ArrayList<String> neo4jQueryResultList2 = Variables.splitResultStringInPart(splitList, 0, 1);
					
					for (int j = 0; j < neo4jQueryResultList1.size(); j++){
						String string1 = Variables.convertIntegerToString(sqlAPI.searchIdQuery(sqlSearchRepoQuery.get(0), sqlSearchRepoQuery.get(1), sqlSearchRepoQuery.get(2), neo4jQueryResultList1.get(j)));
						String string2 = Variables.convertIntegerToString(sqlAPI.searchIdQuery(sqlSearchUserQuery.get(0), sqlSearchUserQuery.get(1), sqlSearchUserQuery.get(2), neo4jQueryResultList2.get(j)));
						
						sqlAPI.inserData( string1, string2, sqlInsertQueries.get(i) );
					}					
				} else {
					ArrayList<String> neo4jQueryResultList1 = Variables.splitResultStringInPart(splitList, 0, 1);
					ArrayList<String> neo4jQueryResultList2 = Variables.splitResultStringInPart(splitList, 1, 1);
					
					for (int j = 0; j < neo4jQueryResultList1.size(); j++){
						sqlAPI.inserData( neo4jQueryResultList1.get(j), neo4jQueryResultList2.get(j), sqlInsertQueries.get(i) );
					}
				}										
				
			} else if ( neo4jQueriesColumnsReturn.get(i) == 3 ) {
				ArrayList<String> neo4jQueryResultList1 = Variables.splitResultStringInPart(splitList, 1, 2);
				ArrayList<String> neo4jQueryResultList2 = Variables.splitResultStringInPart(splitList, 0, 2);
				ArrayList<String> neo4jQueryResultList3 = Variables.splitResultStringInPart(splitList, 2, 2);				
				
				for (int j = 0; j < neo4jQueryResultList1.size(); j++){
					String string1 = Variables.convertIntegerToString(sqlAPI.searchIdQuery(sqlSearchRepoQuery.get(0), sqlSearchRepoQuery.get(1), sqlSearchRepoQuery.get(2), neo4jQueryResultList1.get(j)) );
					String string2 = Variables.convertIntegerToString(sqlAPI.searchIdQuery(sqlSearchUserQuery.get(0), sqlSearchUserQuery.get(1), sqlSearchUserQuery.get(2), neo4jQueryResultList2.get(j)));
					System.out.println( "Adding " + string1 + "-" + string2 + "#" + neo4jQueryResultList2.get(j) );
					sqlAPI.inserData( string1, string2, neo4jQueryResultList3.get(j), sqlInsertQueries.get(i) );
				}
			}
		}
				
		// shutdown
		sqlAPI.shutDown();
		neo4jAPI.shutDown();		
	}


	protected void MySQLInsertMetrics() throws SQLException, ClassNotFoundException {				
		ArrayList<String> neo4jQueries = new ArrayList<String>(Arrays.asList( " MATCH (a:Repository)-[r:CONTRIBUTED_BY]->(b:User) RETURN a.Id, SUM(r.commits); ",
																			  " MATCH (a:Repository)-[r:WATCHED_BY]->(b:User) RETURN a.Id,COUNT(r); ",
																			  " MATCH (a:Repository)-[r:FORKED_BY]->(b:User) RETURN a.Id,COUNT(r); ",
																			  
																			  " MATCH (a:User)<-[r:CONTRIBUTED_BY]-(b:Repository) RETURN a.username, SUM(r.commits); ",
																			  " MATCH (a:User)<-[r:WATCHED_BY]-(b:Repository) RETURN a.username, COUNT(r); ",
																			  " MATCH (a:User)<-[r:FORKED_BY]-(b:Repository) RETURN a.username, COUNT(r); "
																			));
		ArrayList<String> sqlInsertQueries = new ArrayList<String>(Arrays.asList(  " INSERT INTO rmtc (repoID,totalCommits,TimeStamp) VALUES (?,?,?) ",
																				   " INSERT INTO rmwa (repoID,watch,TimeStamp) VALUES (?,?,?) ",
																				   " INSERT INTO rmfo (repoID,fork,TimeStamp) VALUES (?,?,?) ",
																				   
																				   " INSERT INTO umtc (userID,totalCommits,TimeStamp) VALUES (?,?,?) ",
																				   " INSERT INTO umwa (userID,watch,TimeStamp) VALUES (?,?,?) ",
																				   " INSERT INTO umfo (userID,fork,TimeStamp) VALUES (?,?,?) "
																			  ));		
		ArrayList<String> sqlSearchRepoQuery = new ArrayList<String>(Arrays.asList( "repositoryId", "repository", "repoGHId" ));
		ArrayList<String> sqlSearchUserQuery = new ArrayList<String>(Arrays.asList( "userId", "user", "username" ));
		
		//connection
		sqlAPI.connection();
		neo4jAPI.setDatabasePath(databasePaths.getGitHubDatabasePath());
		
		for (int i = 0; i < neo4jQueries.size(); i++){
			String neo4jQueryResult = neo4jAPI.executeQuery( neo4jQueries.get(i), ",");
			String[] splitArray = neo4jQueryResult.split(",");		
			List<String> splitList = new ArrayList<String>(Arrays.asList(splitArray));
						
			ArrayList<String> neo4jQueryResultList1 = Variables.splitResultStringInPart(splitList, 0, 1);
			ArrayList<String> neo4jQueryResultList2 = Variables.splitResultStringInPart(splitList, 1, 1);
			
			for (int j = 0; j < neo4jQueryResultList1.size(); j++){
				String stringId;
				String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
				if ( i <= 2) {
					stringId = Variables.convertIntegerToString(sqlAPI.searchIdQuery(sqlSearchRepoQuery.get(0), sqlSearchRepoQuery.get(1), sqlSearchRepoQuery.get(2), neo4jQueryResultList1.get(j)));
				} else {
					stringId = Variables.convertIntegerToString(sqlAPI.searchIdQuery(sqlSearchUserQuery.get(0), sqlSearchUserQuery.get(1), sqlSearchUserQuery.get(2), neo4jQueryResultList1.get(j)));
				}
				System.out.println( "Adding " + stringId + "-" + neo4jQueryResultList2.get(j) + " #" +  timeStamp );				
				sqlAPI.inserData( stringId, neo4jQueryResultList2.get(j), timeStamp, sqlInsertQueries.get(i) );
			}					
		}		
		// shutdown
		sqlAPI.shutDown();
		neo4jAPI.shutDown();
						
		
		// ------------------------ CALCULATING SNA METRICS -----------------------------
		ArrayList<Integer> networkIdentifier = new ArrayList<Integer>(Arrays.asList( Variables.SNA_ModuleNetwork, Variables.SNA_CommitersNetwork));
		ArrayList<String> databasePathArray = new ArrayList<String>(Arrays.asList( databasePaths.getModuleNetworkDatabasePath(), databasePaths.getCommitersNetworkDatabasePath() ));
				
		for (int i = 0; i < networkIdentifier.size(); i++){
			
			ArrayList<String> sqlMetricsInsertQueries = new ArrayList<String>();
			if ( networkIdentifier.get(i) == Variables.SNA_ModuleNetwork ) {
				sqlMetricsInsertQueries = new ArrayList<String>(Arrays.asList(  " INSERT INTO rmde (repoID,degree,TimeStamp) VALUES (?,?,?) ",
																		   	    " INSERT INTO rmwd (repoID,weightedDegree,TimeStamp) VALUES (?,?,?) ",
																			    " INSERT INTO rmcc (repoID,clusterringCoefficient,TimeStamp) VALUES (?,?,?) ",						   
																			    " INSERT INTO rmwcc (repoID,weightedClusterringCoefficient,TimeStamp) VALUES (?,?,?) ",
																			    " INSERT INTO rmdc (repoID,distanceCentrality,TimeStamp) VALUES (?,?,?) ",
																			    " INSERT INTO rmbc (repoID,betwennessCentrality,TimeStamp) VALUES (?,?,?) "
																			));		
			} else {
				sqlMetricsInsertQueries = new ArrayList<String>(Arrays.asList( " INSERT INTO umde (userID,degree,TimeStamp) VALUES (?,?,?) ",
																			   " INSERT INTO umwd (userID,weightedDegree,TimeStamp) VALUES (?,?,?) ",
																			   " INSERT INTO umcc (userID,clusterringCoefficient,TimeStamp) VALUES (?,?,?) ",						   
																			   " INSERT INTO umwcc (userID,weightedClusterringCoefficient,TimeStamp) VALUES (?,?,?) ",
																			   " INSERT INTO umdc (userID,distanceCentrality,TimeStamp) VALUES (?,?,?) ",
																			   " INSERT INTO umbc (userID,betwennessCentrality,TimeStamp) VALUES (?,?,?) "
																			));
			}
			
			List<String> splitList = executeNetworkQuery( databasePathArray.get(i), networkIdentifier.get(i) );
			//connection
			sqlAPI.connection();
			neo4jAPI.setDatabasePath( databasePathArray.get(i) );
			
			ArrayList<String> repo_user = Variables.splitResultStringInOnePart( splitList );
			System.out.println(repo_user);
			
			ArrayList<Integer> vertexDegree = new ArrayList<Integer>();	
			ArrayList<Integer> weightedDegree = new ArrayList<Integer>();
			ArrayList<Double> clusteringCoefficient = new ArrayList<Double>();
			ArrayList<Double> weightedClusteringCoefficient = new ArrayList<Double>();
			ArrayList<Double> distanceCentrality = new ArrayList<Double>();
			ArrayList<Double> betweennessCentrality = new ArrayList<Double>();
						
			for (int j = 0; j < repo_user.size(); j++ ){
				vertexDegree.add(Variables.convertStringToInteger( neo4jAPI.executeQuery( Variables.getParametersQuery( networkIdentifier.get(i), Variables.DegreeOfAVertex, repo_user.get(j)  ), "" ) ));
				weightedDegree.add(Variables.convertStringToInteger( neo4jAPI.executeQuery( Variables.getParametersQuery( networkIdentifier.get(i), Variables.WeightedDegreeOfAVertex, repo_user.get(j)  ), "" ) ));
				ArrayList<Double> testArrayList = new ArrayList<Double>(Arrays.asList(9.9,9.9,9.9,9.9,9.9,9.9,9.9,9.9,9.9,9.9,9.9,9.9,9.9,9.9));
				clusteringCoefficient.addAll(testArrayList);
				weightedClusteringCoefficient.addAll(testArrayList);
				distanceCentrality.addAll(testArrayList);
				betweennessCentrality.addAll(testArrayList);
				
				String stringId;
				if ( i == 1 ){
					stringId = Variables.convertIntegerToString(sqlAPI.searchIdQuery(sqlSearchUserQuery.get(0), sqlSearchUserQuery.get(1), sqlSearchUserQuery.get(2), repo_user.get(j) ));
				} else {
					stringId = Variables.convertIntegerToString(sqlAPI.searchIdQuery(sqlSearchRepoQuery.get(0), sqlSearchRepoQuery.get(1), sqlSearchRepoQuery.get(2), repo_user.get(j)));
				}
				
				for ( int k = 0; k < sqlMetricsInsertQueries.size(); k++ ){
					ArrayList<String> metricArray = new ArrayList<String>();
					if ( k == 0 ) { metricArray.addAll(Variables.convertIntegerListToStringList(vertexDegree)); }
					else if ( k == 1 ) { metricArray.addAll(Variables.convertIntegerListToStringList(weightedDegree)); }
					else if ( k == 2 ) { metricArray.addAll(Variables.convertDoubleListToStringList(clusteringCoefficient)); }
					else if ( k == 3 ) { metricArray.addAll(Variables.convertDoubleListToStringList(weightedClusteringCoefficient)); }
					else if ( k == 4 ) { metricArray.addAll(Variables.convertDoubleListToStringList(distanceCentrality)); }
					else if ( k == 5 ) { metricArray.addAll(Variables.convertDoubleListToStringList(betweennessCentrality)); }
					
					String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
					
					System.out.println( "Adding " + stringId + "-" + metricArray.get(j) + " #" +  timeStamp );				
					sqlAPI.inserData( stringId, metricArray.get(j), timeStamp, sqlMetricsInsertQueries.get(k) );
				}
			}			
			// shutdown
			neo4jAPI.shutDown();
		}
		sqlAPI.shutDown();		
	} 
}
