package search;

import java.util.ArrayList;

/**
 * This class is used to keep track of a single term entry in a boolean query
 * The class keeps track of whether or not it is a connector (i.e. AND or OR) or
 * a term, as well as whether the
 * 
 * @author dkauchak edited by LB
 */
public class BooleanQueryEntry{
	public static final String AND_TEXT = "AND";
	public static final String OR_TEXT = "OR";
	
	// A boolean query can be either a connector (i.e. AND or OR)
	private boolean connector;  // true if it's a connector
	private boolean isAnd;  // true if it's an AND, false otherwise
	
	// or it can be a query term
	// the term can be negated
	private String term = null;
	private boolean isNegated = false;
	
	/**
	 * queries are of the form:
	 * <term> <connector> <term> <connector> ...
	 *
	 * where <connector> is one of "AND" or "OR"
	 * and <term> is a string with no spaces and may be optionally preceded by '!' for NOT queries
	 */
	public static ArrayList<BooleanQueryEntry> processQuery(String query){
		String[] queryTokens = query.split("\\s+");
		
		ArrayList<BooleanQueryEntry> actualQuery = new ArrayList<BooleanQueryEntry>();
		
		for( int i = 0; i < queryTokens.length; i++ ){
			// every even entry should be a connector
			if( (i+1)%2 == 0 ){
				if( queryTokens[i].equals(AND_TEXT) ){
					actualQuery.add(new BooleanQueryEntry(true));
				}else if( queryTokens[i].equals(OR_TEXT) ){
					actualQuery.add(new BooleanQueryEntry(false));
				}else{
					throw new RuntimeException("BooleanQueryEntry - Bad query specified: \'" + query + "\'\nConnector must AND or OR");
				}
			}else{
				// this should be a term
				if( queryTokens[i].startsWith("!") ){
					actualQuery.add(new BooleanQueryEntry(queryTokens[i].substring(1), true));
				}else{
					actualQuery.add(new BooleanQueryEntry(queryTokens[i], false));
				}
			}
		}
		
		return actualQuery;
	}
	
	/**
	 * creates a new connector
	 * 
	 * @param isAnd whether or not this is an "AND" query
	 */
	public BooleanQueryEntry(boolean isAnd){
		connector = true;
		this.isAnd = isAnd;
	}
	
	/**
	 * creates a new term
	 * 
	 * @param term
	 * @param isNegated whether or not the term is negated
	 */
	public BooleanQueryEntry(String term, boolean isNegated){
		this.term = term;
		this.isNegated = isNegated;
	}
	
	/**
	 * @return whether or not this object is a connector (AND or OR)
	 */
	public boolean isConnector(){
		return connector;
	}
	
	/**
	 * @return whether or not this is an AND
	 */
	public boolean isAnd(){
		return connector && isAnd;
	}
	
	/**
	 * @return whether or not this is an OR
	 */
	public boolean isOr(){
		return connector && !isAnd;
	}
	
	/**
	 * @return whether or not this is a term
	 */
	public boolean isTerm(){
		return !connector;
	}
	
	/**
	 * Get the term associated with this entry as long as it is NOT a
	 * connector
	 * 
	 * @return the associated term
	 */
	public String getTerm(){
		if( connector ){
			throw new RuntimeException("Called \"getTerm\" on a connector");
		}
		
		return term;
	}
	
	/**
	 * @return whether or not this term is negated
	 */
	public boolean isNegated(){
		if( connector ){
			throw new RuntimeException("Called \"isNegated\" on a connector");
		}
		
		return isNegated;
	}
}
