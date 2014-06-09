package backUp;

public class Variables {
	
	// GitHub Analyse API
	protected final static int ModuleNetwork = 0;
	protected final static int CommitersNetwork = 1;
	protected static int nodeCounterAnalyse = 0;						// Initialize counter to check nodes number
	
	// databaseCreator API
	protected static int nodeCounter = 0;						// Initialize counter to check nodes number
	protected final static int nodeLimit = 100;					// Node Limit Initialize
	protected final static String authToken = "a22ecd1b13fd684e0b5e018abe8a73d6290ef0f5";	
	
	
	/**
	 * Method to increase the nodeCounter	
	 * @param int value to be increased with. Example: 1
	 */
	protected static void increaseNodeCounter(int increaseValueWith) {
		nodeCounter += increaseValueWith; }	
	/**
	 * Method to increase the nodeCounter	
	 * @param int value to be increased with. Example: 1
	 */
	protected static  void increaseNodeCounterAnalyse(int increaseValueWith) {
		nodeCounter += increaseValueWith; }	
	
}
