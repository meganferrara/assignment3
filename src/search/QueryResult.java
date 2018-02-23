package search;

/**
 * Basic interface for any query type.
 * 
 * Adding this allows us to support both boolean queries as
 * well as a ranked queries.
 * 
 * @author dkauchak
 *
 */
public interface QueryResult {
	/** 
	 * @return the docIDs that matched for the query 
	 */
	public int[] getIDs();
	
	/**
	 * Note: getIDs() and getScores() should return the same number of entries
	 * and the entries are aligned based on index.
	 * 
	 * @return the scores for the docs that matched the query
	 */
	public double[] getScores();
	
	/**
	 * @return the number of entries in this result
	 */
	public int size();
}
