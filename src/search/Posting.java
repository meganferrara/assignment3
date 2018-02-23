package search;


/**
 * Represents a postings entry in the postings list
 * 
 * @author dkauchak
 *
 */
public class Posting{
	private int docID;
	
	/**
	 * Create a new posting entry
	 * 
	 * @param docID
	 */
	public Posting(int docID){
		this.docID = docID;
	}
	
	/**
	 * @return the docID associated with this posting
	 */
	public int docID(){
		return docID;
	}
}
