package search;

import java.util.Iterator;

/**
 * Defines the interface for an iterator over documents.  Each data set
 * may have it's own eccentricities, so we're going to define an interface
 * that they will share and then we can write individual readers for
 * different data sets.
 * 
 * @author dkauchak
 *
 */
public interface DocumentReader extends Iterator<Document> {	
	/**
	 * Set the tokenizer for this reader
	 * 
	 * @param tokenizer
	 */
	public void setTokenizer(Tokenizer tokenizer);
	
	/**
	 * set the token processor for this reader
	 * 
	 * @param tokenProcessor
	 */
	public void setTokenProcessor(TokenProcessor tokenProcessor);
}