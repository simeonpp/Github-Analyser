package GitHubAnalyser;

import java.util.ArrayList;

public class Variables {
	
	// databaseCreator API
	protected static int nodeCounter = 0;						// Initialize counter to check nodes number
	protected final static int nodeLimit = 100;					// Node Limit Initialize
	protected final static String authToken = "a22ecd1b13fd684e0b5e018abe8a73d6290ef0f5";
	
	// GitHub Analyse API
	protected final static int ModuleNetwork = 0;
	protected final static int CommitersNetwork = 1;
	protected static int nodeCounterAnalyse = 0;						// Initialize counter to check nodes number
	protected final static int DegreeOfAVertex = 0;
	protected final static int WeightedDegreeOfAVertex = 1;
	
		
	
	/**
	 * Method to increase the nodeCounter	
	 * @param int value to be increased with. Example: 1
	 */
	protected static void increaseNodeCounter(int increaseValueWith) {
		nodeCounter += increaseValueWith; }	
	/**
	 * Method to increase the nodeCounterAnalyse	
	 * @param int value to be increased with. Example: 1
	 */
	protected static  void increaseNodeCounterAnalyse(int increaseValueWith) {
		nodeCounterAnalyse += increaseValueWith; }	
	
	
	
	/**
	 * Method to convert Strings ArrayList to Integers ArrayList
	 * @param listOfStrings The ArrayList of Strings which will be converted
	 * @return The Integers ArrayList
	 */
	protected static ArrayList<Integer> convertStringListToIntegerList(ArrayList<String> listOfStrings) {
		ArrayList <Integer> intArray = new ArrayList <Integer>();
       
       for(int i = 0; i < listOfStrings.size(); i++)
       {
    	   intArray.add( Integer.parseInt(listOfStrings.get(i)) );
       }
       
       return intArray;
	}
	
	/**
	 * Method to convert Strings to Integers 
	 * @param string which will be converted
	 * @return the integer
	 */
	protected static Integer convertStringToInteger(String string) {
		
		int integer = Integer.parseInt(string);      
        return integer;
	}
	
	/**
	 * 
	 * @return
	 */
	protected static boolean isNodeLimitReached() {
		if ( nodeCounter < nodeLimit ) { return false;}
		else { return true; }
	}
	
}
